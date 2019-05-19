package com.cubeqw.citydot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends AppCompatActivity implements Session.SearchListener, CameraListener {
    private ArrayList<Object> museum;
    private ArrayList<Object> theater;
    private ArrayList<Object> cinema;
    private ArrayList<Object> memorial;
    private ArrayList<Object> moll;
    private ArrayList<Object> square;
    public static final String SP_NAME = "spName";
    public static final String SP_KEY_FIRST_START = "spKeyFirstStart";
    private final String MAPKIT_API_KEY = "67336d59-08cb-47f3-9b18-334860703128";
    String city_name;
    boolean f=true;
    double lat, lon,  lat_def=55.75222, lon_def=37.61556;
    TinyDB tinydb;
    private CoordinatorLayout coordLayout;
    private SearchManager searchManager;
    private Session searchSession;
    private MapView mapView;
    TextView tv, ma;
    ArrayList al=new ArrayList();
    boolean map_paint=false;
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
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);
        setTitle("Главная");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        coordLayout = findViewById(R.id.maps);
        ma = findViewById(R.id.ma);
        tv = findViewById(R.id.tv);
        tinydb = new TinyDB(getApplicationContext());
        mapView = findViewById(R.id.mapview);
        mapView.getMap().addCameraListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);

            lat = lat_def;
            lon = lon_def;
            try {
                tv.setText(hereLocation(lat_def, lon_def));
                city_name = hereLocation(lat_def, lon_def);
            } catch (NullPointerException e) {
                Snackbar.make(coordLayout, "Что-то пошло не так", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                lat = lat_def;
                lon = lon_def;
                tv.setText(hereLocation(lat_def, lon_def));
                city_name = hereLocation(lat_def, lon_def);
                mapView.getMap().move(

                        new CameraPosition(new Point(lat_def, lon_def), 12f, 0.0f, 0.0f));
            }

            mapView.getMap().move(

                    new CameraPosition(new Point(lat_def, lon_def), 12f, 0.0f, 0.0f));
        } else {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try {
                tv.setText(hereLocation(l.getLatitude(), l.getLongitude()));
                lat = l.getLatitude();
                lon = l.getLongitude();
                city_name = hereLocation(l.getLatitude(), l.getLongitude());
                mapView.getMap().move(
                        new CameraPosition(new Point(l.getLatitude(), l.getLongitude()), 12.0f, 0.0f, 0.0f));
            } catch (NullPointerException e) {
                Snackbar.make(coordLayout, "Что-то пошло не так", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                lat = lat_def;
                lon = lon_def;
                tv.setText(hereLocation(lat_def, lon_def));
                city_name = hereLocation(lat_def, lon_def);
                mapView.getMap().move(

                        new CameraPosition(new Point(lat_def, lon_def), 12f, 0.0f, 0.0f));
            }
        }

        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        boolean firstStart = sp.getBoolean(SP_KEY_FIRST_START, true);
        if (firstStart) {
            sp.edit().putBoolean(SP_KEY_FIRST_START, false).apply();
            Toast.makeText(getApplicationContext(), "Обновление списка мест...", Toast.LENGTH_LONG).show();
            museum = getPlaces("Место:Музеи");
            theater = getPlaces("Место:Театры");
            cinema = getPlaces("Место:Кинотеатры");
            memorial = getPlaces("Место:Скульптуры");
            moll = getPlaces("Место:Торговые центры");
            square = getPlaces("Место:Скверы");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(3000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    update();
                                    place();
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            Intent i = new Intent(this, this.getClass());
            finish();
            this.startActivity(i);
        } else {
            museum = (tinydb.getListObject("museum", String.class));
            memorial = (tinydb.getListObject("memorial", String.class));
            square = (tinydb.getListObject("square", String.class));
            theater = (tinydb.getListObject("theater", String.class));
            cinema = (tinydb.getListObject("cinema", String.class));
            moll = (tinydb.getListObject("moll", String.class));
            place();
        }
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        mapView = findViewById(R.id.mapview);
        mapView.getMap().addCameraListener(this);
        mapView.getMap().move(
                new CameraPosition(new Point(lat, lon), 12f, 0.0f, 0.0f));
while(map_paint){
    submitQuery(al);
    map_paint=false;
}
    }

public void onClick(View v){
    Intent intent = new Intent(this, History.class);
    startActivity(intent);}



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1000:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    LocationManager lm=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location l=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        tv.setText(hereLocation(l.getLatitude(), l.getLongitude()));
                        lat = l.getLatitude();
                        lon = l.getLongitude();
                        city_name = hereLocation(l.getLatitude(), l.getLongitude());
                        mapView.getMap().move(
                                new CameraPosition(new Point(l.getLatitude(), l.getLongitude()), 12f, 0.0f, 0.0f));
                    }catch(NullPointerException e){
                        Snackbar.make(coordLayout, "Что-то пошло не так", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        lat=lat_def;
                        lon=lon_def;
                        tv.setText(hereLocation(lat_def, lon_def));
                        city_name= hereLocation(lat_def, lon_def);
                        mapView.getMap().move(

                                new CameraPosition(new Point(lat_def,lon_def), 12f, 0.0f, 0.0f));
                        Snackbar.make(coordLayout, "Дайте приложению доступ к местоположению в настройках", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
                else{
                    lat=lat_def;
                    lon=lon_def;
                    tv.setText(hereLocation(lat_def, lon_def));
                    city_name= hereLocation(lat_def, lon_def);
                    mapView.getMap().move(

                            new CameraPosition(new Point(lat_def,lon_def), 12f, 0.0f, 0.0f));
                    Snackbar.make(coordLayout, "Дайте приложению доступ к местоположению в настройках", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;

        }
    }

    private String hereLocation(double lat, double lon){
        String city="";
        Geocoder gc=new Geocoder(this, Locale.getDefault());
        List<Address> a;
        try{
            a= gc.getFromLocation(lat, lon,10);
            if (a.size()>0){
                for (Address adr: a) {
                    if(adr.getLocality()!=null&&adr.getLocality().length()!=0){
                        city=adr.getLocality(); break;
                    }
                }
            }
        } catch (IOException e){
            Snackbar.make(coordLayout, "Что-то пошло не так", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();        }
        return city;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.termsmaps:
                Intent browserIntent = new
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/maps_termsofuse/"));
                startActivity(browserIntent);                return true;
             default:return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCameraPositionChanged(
            Map map,
            CameraPosition cameraPosition,
            CameraUpdateSource cameraUpdateSource,
            boolean finished) {
        if (finished) {
        }
    }
public void onClick2(View v){
    Intent intent = new Intent(this, Suggest.class);
    intent.putExtra("city_name", city_name);
    intent.putExtra("lat", lat);
    intent.putExtra("lon", lon);
    startActivity(intent);
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
    public ArrayList<Object> getPlaces(String s){
        ArrayList<Object> e=new ArrayList<>();
        FirstSearch d=new FirstSearch(city_name+": "+s, lat, lon);
        if(d.getPlaces()!=null){
            for (int i = 0; i < d.getPlaces().size(); i++) {
                e.add(d.getPlaces().get(i).toString());
            }
            e= d.getPlaces();
            return  e;}
        else{e.add("error");
            return e;}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();

    }

    @Override
    protected void onPause() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    public void update(){
        if (!(museum.size()==0&& memorial.size()==0&&square.size()==0&&theater.size()==0&&cinema.size()==0&&moll.size()==0)) {
            tinydb.putListObject("museum",museum);
            tinydb.putListObject("memorial", memorial);
            tinydb.putListObject("theater", theater);
            tinydb.putListObject("cinema", cinema);
            tinydb.putListObject("moll", moll);
            tinydb.putListObject("square", square);
            map_paint=true;
        }
    }
    public void place(){
        if(!(museum.size()==0&& memorial.size()==0&&square.size()==0&&theater.size()==0&&cinema.size()==0&&moll.size()==0)){
        for (int i = 0; i < memorial.size(); i++) {
            al.add(city_name+":Место:Скульптура "+memorial.get(i).toString());
        }
        for (int i = 0; i < theater.size(); i++) {
            al.add(city_name+":Место:Театр "+theater.get(i).toString());
        }
        for (int i = 0; i < museum.size(); i++) {
            al.add(city_name+":Место:Музей "+museum.get(i).toString());
        }
        for (int i = 0; i < cinema.size(); i++) {
            al.add(city_name+":Место:Кинотеатр "+cinema.get(i).toString());
        }
        for (int i = 0; i < square.size(); i++) {
            al.add(city_name+":Место:Парк "+square.get(i).toString());
        }for (int i = 0; i < moll.size(); i++) {
            al.add(city_name+":Место:Торговый центр "+moll.get(i).toString());
        }
        map_paint=true;
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }}
}
