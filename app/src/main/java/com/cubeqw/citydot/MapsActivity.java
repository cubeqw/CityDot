package com.cubeqw.citydot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;


public class MapsActivity extends AppCompatActivity implements Session.SearchListener, CameraListener {
    private ArrayList<Object> museum;
    private ArrayList<Object> otherplace=new ArrayList<>();
    private ArrayList<Object> theater;
    private ArrayList<Object> cinema;
    private ArrayList<Object> history=new ArrayList<>();
    NumberProgressBar progressBar;
    private ArrayList<Object> memorial;
    private ArrayList<Object> moll;
    private ArrayList<Object> square;
    private Object lock = new Object();
    public static final String SP_NAME = "spNames";
    public static final String SP_KEY_FIRST_START = "spKeyFirstStarts";
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
    MaterialTapTargetPrompt mFabPrompt;
    ArrayList al=new ArrayList();
    boolean map_paint=false;

    private void submitQuery(List query, List complete) {
        for (int i = 0; i < query.size(); i++) {
            searchSession = searchManager.submit(
                    (String) query.get(i),
                    VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                    new SearchOptions(),
                    new Session.SearchListener() {
                        @Override
                        public void onSearchResponse(@NonNull Response response) {
                            MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
                            if(f){
                                mapObjects.clear();
                                f=false;
                                mapView.onStop();
                                MapKitFactory.getInstance().onStop();
                                MapKitFactory.getInstance().onStart();
                                mapView.onStart();}
                            for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
                                Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();

                                if (resultLocation != null) {
                                    mapObjects.addPlacemark(
                                            resultLocation,
                                            ImageProvider.fromResource(getApplicationContext(), R.drawable.search_result));
                                }
                            }
                        }

                        @Override
                        public void onSearchError(@NonNull Error error) {
                            Snackbar.make(coordLayout, "Не удаётся подключится", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
        }  for (int i = 0; i < complete.size(); i++) {
            searchSession = searchManager.submit(
                    (String) complete.get(i),
                    VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                    new SearchOptions(),
                    new Session.SearchListener() {
                        @Override
                        public void onSearchResponse(@NonNull Response response) {
                            MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
                            if(f){
                                mapObjects.clear();
                                f=false;
                                mapView.onStop();
                                MapKitFactory.getInstance().onStop();
                                MapKitFactory.getInstance().onStart();
                                mapView.onStart();}
                            for (GeoObjectCollection.Item searchResult : response.getCollection().getChildren()) {
                                Point resultLocation = searchResult.getObj().getGeometry().get(0).getPoint();
                                if (resultLocation != null) {
                                    mapObjects.addPlacemark(
                                            resultLocation,
                                            ImageProvider.fromResource(getApplicationContext(), R.drawable.complete));
                                }
                            }
                        }

                        @Override
                        public void onSearchError(@NonNull Error error) {
                            Snackbar.make(coordLayout, "Не удаётся подключится", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
        }}
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        boolean firstStart = sp.getBoolean(SP_KEY_FIRST_START, true);
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        coordLayout = findViewById(R.id.maps);
        ma = findViewById(R.id.ma);
        final FloatingActionButton a=findViewById(R.id.fab);
        final FloatingActionButton b=findViewById(R.id.s);
        progressBar=findViewById(R.id.number_progress_bar);
        tinydb = new TinyDB(getApplicationContext());
        history= (tinydb.getListObject("history", String.class));
        mapView = findViewById(R.id.mapview);
        mapView.getMap().addCameraListener(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (firstStart ) {
            sp.edit().putBoolean(SP_KEY_FIRST_START, false).apply();
            a.setClickable(false);
            b.setClickable(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        a.setClickable(true);
                        b.setClickable(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new MaterialTapTargetSequence()
                    .addPrompt(new MaterialTapTargetPrompt.Builder(MapsActivity.this)
                            .setTarget(findViewById(R.id.s))
                            .setPrimaryText("Где я побывал?")
                            .setSecondaryText("Нажмите на эту кнопку, чтобы попасть в историю посещённых мест").setBackgroundColour(Color.parseColor("#00B0F0"))
                            .create(), 4000).addPrompt(new MaterialTapTargetPrompt.Builder(this).setBackgroundColour(Color.parseColor("#00B0F0"))
                    .setTarget(findViewById(R.id.fab))
                    .setPrimaryText("Давайте начнём!")
                    .setSecondaryText("Нажмите на эту кнопку, чтобы получить список мест для вашего города")
                    .setAutoDismiss(false)
                    .setAutoFinish(false)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                    {
                        @Override
                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state)
                        {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                            {
                                prompt.finish();
                                mFabPrompt = null;
                            }
                            else if (state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED)
                            {
                                mFabPrompt = null;
                            }
                        }
                    }))
                    .show();
             }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);

            lat = lat_def;
            lon = lon_def;
            try {
                city_name = hereLocation(lat_def, lon_def);
                setTitle(city_name);
                sp();
                place();

            } catch (NullPointerException e) {
                Snackbar.make(coordLayout, "Что-то пошло не так", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                lat = lat_def;
                lon = lon_def;
                city_name = hereLocation(lat_def, lon_def);
                setTitle(city_name);
                sp();
                place();

                mapView.getMap().move(

                        new CameraPosition(new Point(lat_def, lon_def), 12f, 0.0f, 0.0f));
            }

            mapView.getMap().move(

                    new CameraPosition(new Point(lat_def, lon_def), 12f, 0.0f, 0.0f));
        } else {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try {
                lat = l.getLatitude();
                lon = l.getLongitude();
                city_name = hereLocation(l.getLatitude(), l.getLongitude());
                setTitle(city_name);
                sp();
                place();

                mapView.getMap().move(
                        new CameraPosition(new Point(l.getLatitude(), l.getLongitude()), 12.0f, 0.0f, 0.0f));
            } catch (NullPointerException e) {
                Snackbar.make(coordLayout, "Что-то пошло не так "+e, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                lat = lat_def;
                lon = lon_def;
                city_name = hereLocation(lat_def, lon_def);
                setTitle(city_name);
                sp();
                place();

                mapView.getMap().move(
                        new CameraPosition(new Point(lat_def, lon_def), 12f, 0.0f, 0.0f));
            }
        }

        double i=history.size()+al.size();
        int h=history.size()+al.size();
        double e=i/100;
        double progress=history.size()/e;
        progressBar.setProgress((int) progress);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        mapView = findViewById(R.id.mapview);
        mapView.getMap().addCameraListener(this);
        mapView.getMap().move(
                new CameraPosition(new Point(lat, lon), 12f, 0.0f, 0.0f));
while(map_paint){
    submitQuery(al, history);
    map_paint=false;
}
        ma.setText(history.size()+" из "+h+" мест");

    }

public void onClick(View v){
    Intent intent = new Intent(this, History.class);
    startActivity(intent);
}



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1000:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    LocationManager lm=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location l=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    try {
                        lat = l.getLatitude();
                        lon = l.getLongitude();
                        city_name = hereLocation(l.getLatitude(), l.getLongitude());
                        setTitle(city_name);
                        sp();
                        place();

                        mapView.getMap().move(
                                new CameraPosition(new Point(l.getLatitude(), l.getLongitude()), 12f, 0.0f, 0.0f));
                    }catch(NullPointerException e){
                        Snackbar.make(coordLayout, "Что-то пошло не так", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        lat=lat_def;
                        lon=lon_def;
                        city_name= hereLocation(lat_def, lon_def);
                        setTitle(city_name);
                        sp();
                        place();
                        mapView.getMap().move(

                                new CameraPosition(new Point(lat_def,lon_def), 12f, 0.0f, 0.0f));
                        Snackbar.make(coordLayout, "Дайте приложению доступ к местоположению в настройках", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
                else{
                    lat=lat_def;
                    lon=lon_def;
                    city_name= hereLocation(lat_def, lon_def);
                    setTitle(city_name);
                    sp();
                    place();

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
        f=false;
            mapView.onStop();
            MapKitFactory.getInstance().onStop();
            MapKitFactory.getInstance().onStart();
            mapView.onStart();}
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
        Snackbar.make(coordLayout, "Не удаётся подключится", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        System.runFinalizersOnExit(true);
        System.exit(0);

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
        }for (int i = 0; i < otherplace.size(); i++) {
            al.add(city_name+" "+otherplace.get(i).toString());
        }
        map_paint=true;
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }}
    public void sp(){
            museum = (tinydb.getListObject("museum", String.class));
            memorial = (tinydb.getListObject("memorial", String.class));
            square = (tinydb.getListObject("square", String.class));
            theater = (tinydb.getListObject("theater", String.class));
            cinema = (tinydb.getListObject("cinema", String.class));
            moll = (tinydb.getListObject("moll", String.class));
            otherplace=(tinydb.getListObject("other", String.class));

    }
    @Override
    public void onBackPressed()
    {
        finish();
        System.exit(0);
        super.onBackPressed();
    }
}
