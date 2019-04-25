package com.example.itydot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.SearchFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends AppCompatActivity implements CameraListener {
    private final String MAPKIT_API_KEY = "67336d59-08cb-47f3-9b18-334860703128";
    String city_name;
    double lat, lon;
    private MapView mapView;
TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
tv=findViewById(R.id.tv);
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.getMap().addCameraListener(this);

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M&& checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        }
        else{
            LocationManager lm=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location l=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            tv.setText(hereLocation(l.getLatitude(), l.getLongitude()));
            lat=l.getLatitude();
            lon=l.getLongitude();
            city_name=hereLocation(l.getLatitude(), l.getLongitude());
            mapView.getMap().move(

                    new CameraPosition(new Point(l.getLatitude(), l.getLongitude()), 12.0f, 0.0f, 0.0f));
        }
    }
public void onClick(View v){
    Intent intent = new Intent(this, Search.class);
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
                    tv.setText(hereLocation(l.getLatitude(), l.getLongitude()));
                    city_name=hereLocation(l.getLatitude(), l.getLongitude());
                    lat=l.getLatitude();
                    lon=l.getLongitude();
                }
                else{            Toast.makeText(getApplicationContext(), "Не удалось определить местоположение.",Toast.LENGTH_LONG);
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
            Toast.makeText(getApplicationContext(), "Не удалось определить местоположение.",Toast.LENGTH_LONG);
        }
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

}
