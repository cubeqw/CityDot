package com.example.itydot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;

import java.util.ArrayList;
import java.util.List;


public class Suggest extends Activity {
    String city_name;
    double lat, lon;
    private final String MAPKIT_API_KEY = "67336d59-08cb-47f3-9b18-334860703128";
    private SearchManager searchManager;
    private ListView suggestResultView, suggestResultView1, suggestResultView2, suggestResultView3,sv4, sv5;
    private ArrayAdapter resultAdapter, resultAdapter1, resultAdapter2,resultAdapter3,ra4, ra5;
    private ArrayList<Object> museum;
    private ArrayList<Object> teatr;
    private ArrayList<Object> cinema;
    private ArrayList<Object> mem;
    private ArrayList<Object> moll;
    private ArrayList<Object> square;
    TinyDB tinydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        tinydb= new TinyDB(getApplicationContext());
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
        sv5 =findViewById(R.id.suggest_result5);
        sv4=findViewById(R.id.suggest_result6);
        museum =getPlaces("Место:Музеи");
        teatr=getPlaces("Место:Театры");
        cinema=getPlaces("Место:Кинотеатры");
        mem=getPlaces("Место:Скульптуры");
        moll=getPlaces("Место:ТЦ");
        square=getPlaces("Место:Скверы");
            resultAdapter =setArrayAdapter(museum);
            resultAdapter1 =setArrayAdapter(teatr);
            resultAdapter2=setArrayAdapter(cinema);
            resultAdapter3=setArrayAdapter(mem);
            ra5=setArrayAdapter(moll);
            ra4=setArrayAdapter(square);
        suggestResultView.setAdapter(resultAdapter);
        suggestResultView1.setAdapter(resultAdapter1);
        suggestResultView2.setAdapter(resultAdapter2);
        suggestResultView3.setAdapter(resultAdapter3);
        sv4.setAdapter(ra4);
        sv5.setAdapter(ra5);
        Toast.makeText(getApplicationContext(), "Обновление списка мест...", Toast.LENGTH_LONG).show();
      Thread  thread = new Thread(){
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(5000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              update();
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            };
        };
        thread.start();
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
    public ArrayList<Object> getPlaces(String s){
        ArrayList<Object> e=new ArrayList<>();
        FirstSearch d=new FirstSearch(city_name+": "+s);
        if(d.getPlaces()!=null){
        for (int i = 0; i < d.getPlaces().size(); i++) {
            e.add(d.getPlaces().get(i).toString());
        }
        e= d.getPlaces();
        return  e;}
        else{e.add("error");
        return e;}
    }
    public ArrayAdapter setArrayAdapter(List f){
        ArrayAdapter g=new ArrayAdapter(this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                f);
        return g;
    }
   public void update(){

       resultAdapter.notifyDataSetChanged();
       resultAdapter1.notifyDataSetChanged();
       resultAdapter2.notifyDataSetChanged();
       resultAdapter3.notifyDataSetChanged();
       ra4.notifyDataSetChanged();
       ra5.notifyDataSetChanged();
      tinydb.putListObject("museum",museum);
     tinydb.putListObject("mem", mem);
     tinydb.putListObject("teatr", teatr);
     tinydb.putListObject("cinema", cinema);
     tinydb.putListObject("moll", moll);
     tinydb.putListObject("square", square);
    }
}