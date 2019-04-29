package com.example.itydot;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;

import java.util.ArrayList;
import java.util.List;


public class Suggest extends Activity {
    boolean firstS=true;
    String city_name;
    double lat, lon;
    private final String MAPKIT_API_KEY = "67336d59-08cb-47f3-9b18-334860703128";
    private SearchManager searchManager;
    private ListView suggestResultView, suggestResultView1, suggestResultView2, suggestResultView3;
    private ArrayAdapter resultAdapter, resultAdapter1, resultAdapter2,resultAdapter3;
    private List<String> museum, teatr, cinema, mem;
    TinyDB tinyDB = new TinyDB(getApplicationContext());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);
        setContentView(R.layout.activity_suggest);
        super.onCreate(savedInstanceState);
        city_name = getIntent().getStringExtra("city_name");
        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        suggestResultView =findViewById(R.id.suggest_result);
        suggestResultView1 =findViewById(R.id.suggest_result1);
        suggestResultView2 =findViewById(R.id.suggest_result2);
        suggestResultView3 =findViewById(R.id.suggest_result3);
        if(firstS){
        museum =getPlaces("Музеи");
        teatr=getPlaces("Театры");
        cinema=getPlaces("Кинотеатры");
        mem=getPlaces("Скульптуры"); }
        else{museum=tinyDB.getListString("museum");
        teatr=tinyDB.getListString("teatr");
        cinema= tinyDB.getListString("cinema");
        mem=tinyDB.getListString("mem");
        }
            resultAdapter =setArrayAdapter(museum);
            resultAdapter1 =setArrayAdapter(teatr);
            resultAdapter2=setArrayAdapter(cinema);
            resultAdapter3=setArrayAdapter(mem);
        suggestResultView.setAdapter(resultAdapter);
        suggestResultView1.setAdapter(resultAdapter1);
        suggestResultView2.setAdapter(resultAdapter2);
        suggestResultView3.setAdapter(resultAdapter3);
    }

    @Override
    protected void onStop() {
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
    }
    public List getPlaces(String s){
        List e=new ArrayList<>();
        FirstSearch d=new FirstSearch(city_name+": "+s);
        for (int i = 0; i < d.getPlaces().size(); i++) {
            e.add(d.getPlaces().get(i).toString());
        }
        e= d.getPlaces();
        return  e;
    }
    public ArrayAdapter setArrayAdapter(List f){
        ArrayAdapter g=new ArrayAdapter(this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                f);
        return g;
    }
    public void onClick(View v){
        saveList(museum,"museum");
        saveList(mem,"mem");
        saveList(cinema,"cinema");
        saveList(teatr,"teatr");
    }
    public void saveList(List f, String s){
        if(museum.size()!=0) {
            tinyDB.putListString(s, (ArrayList<String>) f);
            ArrayList al = new ArrayList();
            al = tinyDB.getListString(s);
            Log.d("sptest",s+" "+ String.valueOf(al.size())+" "+al.get(1));
        }
    }
}