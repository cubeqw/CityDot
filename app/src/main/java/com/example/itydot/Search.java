package com.example.itydot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;


public class Search extends Activity implements Session.SearchListener, CameraListener {

    private final String MAPKIT_API_KEY = "67336d59-08cb-47f3-9b18-334860703128";
    int count=0;
    boolean f=true;
    private MapView mapView;
    private SearchManager searchManager;
    private Session searchSession;
    double lat, lon;
    ArrayList al;
    TinyDB tinydb;
TextView tv;
    private void submitQuery(List query) {
        for (int i = 0; i < query.size(); i++) {
        searchSession = searchManager.submit(
                (String) query.get(i),
                VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                new SearchOptions(),
                this);
    }}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        tinydb= new TinyDB(getApplicationContext());
        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);
        setContentView(R.layout.activity_search);
        super.onCreate(savedInstanceState);
tv=findViewById(R.id.tv);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        al=new ArrayList();
        al.addAll(tinydb.getListObject("museum", String.class));
        al.addAll(tinydb.getListObject("mem", String.class));
        al.addAll(tinydb.getListObject("teatr", String.class));
        al.addAll(tinydb.getListObject("cinema", String.class));
        al.addAll(tinydb.getListObject("square", String.class));
        Toast.makeText(getApplicationContext(), al.size()+"", Toast.LENGTH_SHORT).show();
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.getMap().addCameraListener(this);
            mapView.getMap().move(
                    new CameraPosition(new Point(lat, lon), 12.0f, 0.0f, 0.0f));
            submitQuery(al);
        }



    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onSearchResponse(Response response) {
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        if(f){
        mapObjects.clear();
        f=false;}
        count++;

        for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
            Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();

            if (resultLocation != null) {
                mapObjects.addPlacemark(
                        resultLocation,
                        ImageProvider.fromResource(this, R.drawable.search_result));
            }
        }
    }

    @Override
    public void onSearchError(Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPositionChanged(
            Map map,
            CameraPosition cameraPosition,
            CameraUpdateSource cameraUpdateSource,
            boolean finished) {

    }
}


