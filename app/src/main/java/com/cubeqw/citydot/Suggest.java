package com.cubeqw.citydot;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;

import java.util.ArrayList;
import java.util.List;


public class Suggest extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    String city_name;
    TextView tv;
    double lat, lon;
    private final String MAPKIT_API_KEY = "67336d59-08cb-47f3-9b18-334860703128";
    private SearchManager searchManager;
    private ListView suggestResultView, suggestResultView1, suggestResultView2, suggestResultView3,sv4, sv5;
    private ArrayAdapter resultAdapter, resultAdapter1, resultAdapter2,resultAdapter3,ra4, ra5;
    TinyDB tinydb;
    private ArrayList<Object> museum;
    private ArrayList<Object> theater;
    private ArrayList<Object> cinema;
    private ArrayList<Object> memorial;
    private ArrayList<Object> moll;
    private ArrayList<Object> square;
    public static final String SP_NAME = "spName";
    public static final String SP_KEY_FIRST_START = "spKeyFirstStart";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    boolean firstStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setTitle("Добавить место");
        tinydb= new TinyDB(getApplicationContext());
        SearchFactory.initialize(this);
        setContentView(R.layout.activity_suggest);
        super.onCreate(savedInstanceState);
        tv=findViewById(R.id.tv);
        mSwipeRefreshLayout =findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        city_name = getIntent().getStringExtra("city_name");
        tv.setText(city_name);
        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        suggestResultView =findViewById(R.id.suggest_result);
        suggestResultView1 =findViewById(R.id.suggest_result1);
        suggestResultView2 =findViewById(R.id.suggest_result2);
        suggestResultView3 =findViewById(R.id.suggest_result3);
        sv5 =findViewById(R.id.suggest_result5);
        sv4=findViewById(R.id.suggest_result6);
        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        boolean firstStart = sp.getBoolean(SP_KEY_FIRST_START, true);
        if(firstStart) {
            sp.edit().putBoolean(SP_KEY_FIRST_START, false).apply();
            Toast.makeText(getApplicationContext(), "Обновление списка мест...", Toast.LENGTH_LONG).show();
            museum =getPlaces("Место:Музеи");
            theater =getPlaces("Место:Театры");
            cinema=getPlaces("Место:Кинотеатры");
            memorial =getPlaces("Место:Скульптуры");
            moll=getPlaces("Место:Торговые центры");
            square=getPlaces("Место:Скверы");
            Thread  thread = new Thread(){
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(3000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    update();
                                    if (museum.size()+memorial.size()+square.size()+theater.size()+cinema.size()+moll.size()==0) {
                                        Toast.makeText(getApplicationContext(), "Я не смог найти места\nСделайте свайп вниз, чтобы обновить", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();}
        else {
            museum=(tinydb.getListObject("museum", String.class));
            memorial =(tinydb.getListObject("memorial", String.class));
            square=(tinydb.getListObject("square", String.class));
            theater =(tinydb.getListObject("theater", String.class));
            cinema=(tinydb.getListObject("cinema", String.class));
            moll=(tinydb.getListObject("moll", String.class));
        }

        resultAdapter =setArrayAdapter(museum);
        resultAdapter1 =setArrayAdapter(theater);
        resultAdapter2=setArrayAdapter(cinema);
        resultAdapter3=setArrayAdapter(memorial);
        ra5=setArrayAdapter(moll);
        ra4=setArrayAdapter(square);
        suggestResultView.setAdapter(resultAdapter);
        suggestResultView1.setAdapter(resultAdapter1);
        suggestResultView2.setAdapter(resultAdapter2);
        suggestResultView3.setAdapter(resultAdapter3);
        sv4.setAdapter(ra4);
        sv5.setAdapter(ra5);

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
    public ArrayAdapter setArrayAdapter(List f){
        ArrayAdapter g=new ArrayAdapter(this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                f);
        return g;
    }
    public void update(){
        if (!(museum.size()==0&& memorial.size()==0&&square.size()==0&&theater.size()==0&&cinema.size()==0&&moll.size()==0)) {
        resultAdapter.notifyDataSetChanged();
        resultAdapter1.notifyDataSetChanged();
        resultAdapter2.notifyDataSetChanged();
        resultAdapter3.notifyDataSetChanged();
        ra4.notifyDataSetChanged();
        ra5.notifyDataSetChanged();
            tinydb.putListObject("museum",museum);
        tinydb.putListObject("memorial", memorial);
        tinydb.putListObject("theater", theater);
        tinydb.putListObject("cinema", cinema);
        tinydb.putListObject("moll", moll);
        tinydb.putListObject("square", square);}
    }
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                museum =getPlaces("Место:Музеи");
                theater =getPlaces("Место:Театры");
                cinema=getPlaces("Место:Кинотеатры");
                memorial =getPlaces("Место:Скульптуры");
                moll=getPlaces("Место:Торговые центры");
                square=getPlaces("Место:Скверы");
                Thread  thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(3000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tinydb.remove("museum");
                                        tinydb.remove("moll");
                                        tinydb.remove("memorial");
                                        tinydb.remove("cinema");
                                        tinydb.remove("square");
                                        tinydb.remove("theater");
                                        update();
                                        if (museum.size()==0&& memorial.size()==0&&square.size()==0&&theater.size()==0&&cinema.size()==0&&moll.size()==0) {
                                            Toast.makeText(getApplicationContext(), "Я не смог найти места\nСделайте свайп вниз, чтобы обновить", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
                resultAdapter.notifyDataSetChanged();
                resultAdapter1.notifyDataSetChanged();
                resultAdapter2.notifyDataSetChanged();
                resultAdapter3.notifyDataSetChanged();
                ra4.notifyDataSetChanged();
                ra5.notifyDataSetChanged();
            }
        }, 4000);
    }
}
