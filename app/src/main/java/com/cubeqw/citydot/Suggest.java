package com.cubeqw.citydot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence;


public class Suggest extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String city_name;
   private TextView tv, other;
    double lat, lon;
    private final String MAPKIT_API_KEY = "67336d59-08cb-47f3-9b18-334860703128";
    private SearchManager searchManager;
    private ListView suggestResultView, suggestResultView1, suggestResultView2, suggestResultView3, sv4, sv5, sv6;
    protected ArrayAdapter resultAdapter, resultAdapter1, resultAdapter2, resultAdapter3, ra4, ra5, ra6;
    public TinyDB tinydb;
    protected ArrayList<Object> museum;
    public ArrayList<Object> theater;
    public ArrayList<Object> cinema;
    public ArrayList<Object> memorial;
    public ArrayList<Object> moll;
    public ArrayList<Object> square;
    private TextView tvmus;
    MaterialTapTargetPrompt mFabPrompt;
    private TextView tvmem;
    private TextView tvcin;
    private TextView tvmoll;
    private TextView tvthr;
    private TextView tvsqr;
    public ArrayList<Object> history=new ArrayList<>();
    public ArrayList<Object> otherplace = new ArrayList<>();
    public static final String SP_NAME = "spName";
    public static final String SP_KEY_FIRST_START = "spKeyFirstStart";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    boolean firstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setTitle("Места поблизости");
        tinydb = new TinyDB(getApplicationContext());
        SearchFactory.initialize(this);
        setContentView(R.layout.activity_suggest);
        super.onCreate(savedInstanceState);
        tv = findViewById(R.id.tv);
        tvmus=findViewById(R.id.mus);
        tvcin=findViewById(R.id.cin);
        tvmoll=findViewById(R.id.moll);
        tvmem=findViewById(R.id.mem);
        tvsqr=findViewById(R.id.sqr);
        other = findViewById(R.id.other);
        tvthr=findViewById(R.id.thr);
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        boolean firstStart = sp.getBoolean(SP_KEY_FIRST_START, true);
        if (firstStart) {
            final FloatingActionButton a=findViewById(R.id.add);
            final FloatingActionButton b=findViewById(R.id.fab);
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
            SpannableStringBuilder secondaryText = new SpannableStringBuilder(
                    "Смахните влево, чтобы изучить место\n" +
                            "Смахните вправо, чтобы удалить место из списка, если вы не хотите посещать его\n" +
                            "Нажмите на название места, чтобы проложить маршрут или найти его в интернете");
            secondaryText.setSpan(
                    new BackgroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)),
                    0, 191, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableStringBuilder primaryText = new SpannableStringBuilder(
                    "Как пользоваться списком");
            primaryText.setSpan(
                    new BackgroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary)),
                    0, 24, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            new MaterialTapTargetSequence()
                    .addPrompt(new MaterialTapTargetPrompt.Builder(Suggest.this)
                            .setTarget(R.id.ssss)
                            .setPrimaryText(primaryText)
                            .setSecondaryText(secondaryText).setBackgroundColour(Color.parseColor("#00B0F0"))                    .setAutoDismiss(false)
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
                            })
                            .create(), 10000).addPrompt(
            new MaterialTapTargetPrompt.Builder(this).setBackgroundColour(Color.parseColor("#00B0F0"))
                .setTarget(findViewById(R.id.add))
                .setPrimaryText("Чего-то не хватает?")
                .setSecondaryText("Добавте своё место, нажав на эту кнопку").                    setAutoDismiss(false)
                    .create(), 4000).addPrompt(new MaterialTapTargetPrompt.Builder(this).setBackgroundColour(Color.parseColor("#00B0F0"))
                    .setTarget(findViewById(R.id.swype))
                    .setPrimaryText("Сначала")
                    .setSecondaryText("Сделайте свайп вниз, чтобы сбросить статистику"))
                .show();}
       city_name = getIntent().getStringExtra("city_name");
        tv.setText(city_name);
        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        suggestResultView = findViewById(R.id.suggest_result);
        suggestResultView1 = findViewById(R.id.suggest_result1);
        suggestResultView2 = findViewById(R.id.suggest_result2);
        suggestResultView3 = findViewById(R.id.suggest_result3);
        sv5 = findViewById(R.id.suggest_result5);
        sv4 = findViewById(R.id.suggest_result6);
        sv6 = findViewById(R.id.suggest_result7);
        history= (tinydb.getListObject("history", String.class));
        otherplace= (tinydb.getListObject("other", String.class));
        String scity = tinydb.getString("city");
        if (firstStart || !(city_name.equals(scity))||(history.size()==0&&otherplace.size()==0)) {
            sp.edit().putBoolean(SP_KEY_FIRST_START, false).apply();
            tinydb.putString("city", city_name);
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
                                    resultAdapter.notifyDataSetChanged();
                                    resultAdapter1.notifyDataSetChanged();
                                    resultAdapter2.notifyDataSetChanged();
                                    resultAdapter3.notifyDataSetChanged();
                                    ra4.notifyDataSetChanged();
                                    ra5.notifyDataSetChanged();
                                    update();
                                    if (museum.size() + memorial.size() + square.size() + theater.size() + cinema.size() + moll.size() == 0) {
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
        } else {

            museum = (tinydb.getListObject("museum", String.class));
            memorial = (tinydb.getListObject("memorial", String.class));
            square = (tinydb.getListObject("square", String.class));
            theater = (tinydb.getListObject("theater", String.class));
            cinema = (tinydb.getListObject("cinema", String.class));
            moll = (tinydb.getListObject("moll", String.class));
            otherplace = (tinydb.getListObject("other", String.class));
            if (museum.size()==0){
                tvmus.setText("Вы изучили все музеи");
            }if (memorial.size()==0){
                tvmem.setText("Вы изучили все скульптуры");
            }if (square.size()==0){
                tvsqr.setText("Вы изучили все парки");
            }if (cinema.size()==0){
                tvcin.setText("Вы изучили все кинотеатры");
            }if (moll.size()==0){
                tvmoll.setText("Вы изучили все ТЦ");
     }if (theater.size()==0){
                tvthr.setText("Вы изучили все театры");
     }}
        if (otherplace.size() != 0) {
            other.setText("Другие места");
        }
        resultAdapter = setArrayAdapter(museum);
        resultAdapter1 = setArrayAdapter(theater);
        resultAdapter2 = setArrayAdapter(cinema);
        resultAdapter3 = setArrayAdapter(memorial);
        ra5 = setArrayAdapter(moll);
        ra4 = setArrayAdapter(square);
        ra6 = setArrayAdapter(otherplace);
        suggestResultView.setAdapter(resultAdapter);
        suggestResultView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(),
                suggestResultView));
        suggestResultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Suggest.this);
                alert.setTitle("Какое действие выполнить?");
                final String texte =museum.get(position)+"";
                alert.setMessage(texte);
                alert.setPositiveButton("Проложить маршрут", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {

                            Uri uri = Uri.parse("yandexmaps://maps.yandex.ru/?text=" + texte);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException e) {
                            Uri uri1 = Uri.parse("http://maps.yandex.ru/?text=" + texte);
                            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                            startActivity(intent1);
                        }}});
                        alert.setNegativeButton("Найти в интернете", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Uri uri = Uri.parse("https://yandex.ru/search/?text=" + texte);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        });
                alert.show();
            }});
        suggestResultView1.setAdapter(resultAdapter1);
        suggestResultView1.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(),
                suggestResultView1));
        suggestResultView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Suggest.this);
                alert.setTitle("Какое действие выполнить?");
                final String texte =theater.get(position)+"";
                alert.setMessage(texte);
                alert.setPositiveButton("Проложить маршрут", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {

                            Uri uri = Uri.parse("yandexmaps://maps.yandex.ru/?text=" + texte);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException e) {
                            Uri uri1 = Uri.parse("http://maps.yandex.ru/?text=" + texte);
                            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                            startActivity(intent1);
                        }}});
                alert.setNegativeButton("Найти в интернете", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Uri uri = Uri.parse("https://yandex.ru/search/?text=" + texte);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                alert.show();
            }});
        suggestResultView2.setAdapter(resultAdapter2);
        suggestResultView2.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(),
                suggestResultView2));
        suggestResultView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {  @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(Suggest.this);
            alert.setTitle("Какое действие выполнить?");
            final String texte =cinema.get(position)+"";
            alert.setMessage(texte);
            alert.setPositiveButton("Проложить маршрут", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    try {

                        Uri uri = Uri.parse("yandexmaps://maps.yandex.ru/?text=" + texte);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        Uri uri1 = Uri.parse("http://maps.yandex.ru/?text=" + texte);
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                        startActivity(intent1);
                    }}});
            alert.setNegativeButton("Найти в интернете", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Uri uri = Uri.parse("https://yandex.ru/search/?text=" + texte);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            alert.show();
        }});
        suggestResultView3.setAdapter(resultAdapter3);
        suggestResultView3.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(),
                suggestResultView3));
        suggestResultView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Suggest.this);
                alert.setTitle("Какое действие выполнить?");
                final String texte =memorial.get(position)+"";
                alert.setMessage(texte);
                alert.setPositiveButton("Проложить маршрут", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {

                            Uri uri = Uri.parse("yandexmaps://maps.yandex.ru/?text=" + texte);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException e) {
                            Uri uri1 = Uri.parse("http://maps.yandex.ru/?text=" + texte);
                            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                            startActivity(intent1);
                        }}});
                alert.setNegativeButton("Найти в интернете", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Uri uri = Uri.parse("https://yandex.ru/search/?text=" + texte);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                alert.show();
            }});
        sv4.setAdapter(ra4);
        sv4.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(),
                sv4));
        sv4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Suggest.this);
                alert.setTitle("Какое действие выполнить?");
                final String texte =square.get(position)+"";
                alert.setMessage(texte);
                alert.setPositiveButton("Проложить маршрут", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {

                            Uri uri = Uri.parse("yandexmaps://maps.yandex.ru/?text=" + texte);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException e) {
                            Uri uri1 = Uri.parse("http://maps.yandex.ru/?text=" + texte);
                            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                            startActivity(intent1);
                        }}});
                alert.setNegativeButton("Найти в интернете", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Uri uri = Uri.parse("https://yandex.ru/search/?text=" + texte);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                alert.show();
            }});
        sv5.setAdapter(ra5);
        sv5.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(),
                sv5));
        sv5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Suggest.this);
                alert.setTitle("Какое действие выполнить?");
                final String texte =moll.get(position)+"";
                alert.setMessage(texte);
                alert.setPositiveButton("Проложить маршрут", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {

                            Uri uri = Uri.parse("yandexmaps://maps.yandex.ru/?text=" + texte);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException e) {
                            Uri uri1 = Uri.parse("http://maps.yandex.ru/?text=" + texte);
                            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                            startActivity(intent1);
                        }}});
                alert.setNegativeButton("Найти в интернете", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Uri uri = Uri.parse("https://yandex.ru/search/?text=" + texte);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                alert.show();
            }});
        sv6.setAdapter(ra6);
        sv6.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(),
                sv6));
        sv6.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Suggest.this);
                alert.setTitle("Какое действие выполнить?");
                final String texte =otherplace.get(position)+"";
                alert.setMessage(texte);
                alert.setPositiveButton("Проложить маршрут", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {

                            Uri uri = Uri.parse("yandexmaps://maps.yandex.ru/?text=" + texte);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException e) {
                            Uri uri1 = Uri.parse("http://maps.yandex.ru/?text=" + texte);
                            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                            startActivity(intent1);
                        }}});
                alert.setNegativeButton("Найти в интернете", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Uri uri = Uri.parse("https://yandex.ru/search/?text=" + texte);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                alert.show();
            }});
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

    public ArrayList<Object> getPlaces(String s) {
        ArrayList<Object> e = new ArrayList<>();
        FirstSearch d = new FirstSearch(city_name + ": " + s, lat, lon);
        if (d.getPlaces() != null) {
            for (int i = 0; i < d.getPlaces().size(); i++) {
                e.add(d.getPlaces().get(i).toString());
            }
            e = d.getPlaces();
            return e;
        } else {
            e.add("error");
            return e;
        }
    }

    protected ArrayAdapter setArrayAdapter(List f) {

        ArrayAdapter g = new ArrayAdapter(this,
                R.layout.row_bg,
                R.id.text,
                f);
        return g;
    }

    protected void update() {
        if (!(museum.size() == 0 && memorial.size() == 0 && square.size() == 0 && theater.size() == 0 && cinema.size() == 0 && moll.size() == 0&&otherplace.size()==0)) {
            tinydb.putListObject("museum", museum);
            tinydb.putListObject("memorial", memorial);
            tinydb.putListObject("theater", theater);
            tinydb.putListObject("cinema", cinema);
            tinydb.putListObject("moll", moll);
            tinydb.putListObject("square", square);
            tinydb.putListObject("other", otherplace);
            tinydb.putListObject("history", history);
        }
    }

    public void onRefresh() {
        final View v = new View(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setMessage("Вы действительно хотите сбросить статистику?");
                alert.setTitle("Сброс");
                alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
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
                                                tinydb.remove("museum");
                                                tinydb.remove("moll");
                                                tinydb.remove("memorial");
                                                tinydb.remove("cinema");
                                                tinydb.remove("square");
                                                tinydb.remove("theater");
                                                tinydb.remove("other");
                                                otherplace.clear();
                                                update();
                                                if (museum.size() == 0 && memorial.size() == 0 && square.size() == 0 && theater.size() == 0 && cinema.size() == 0 && moll.size() == 0) {
                                                    Toast.makeText(getApplicationContext(), "Я не смог найти места\nПроверте подключение к сети\n" +
                                                            "Сделайте свайп вниз, чтобы обновить",Toast.LENGTH_LONG).show();
                                                }
                                                resultAdapter.notifyDataSetChanged();
                                                resultAdapter1.notifyDataSetChanged();
                                                resultAdapter2.notifyDataSetChanged();
                                                resultAdapter3.notifyDataSetChanged();
                                                ra4.notifyDataSetChanged();
                                                ra5.notifyDataSetChanged();
                                                ra6.notifyDataSetChanged();
                                                other.setText("");
                                                mSwipeRefreshLayout.setRefreshing(false);
                                            }
                                        });
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    }
                });

                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                });
                alert.show();
            }
        }, 1000);
    }

    public void onClick(View v) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
        finish();

    }

    public void onClick2(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Добавить место");
        alert.setView(R.layout.edittext);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edittext, null);
        final EditText edittext =dialogView.findViewById(R.id.et);
        alert.setView(dialogView);
        alert.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String input = edittext.getText().toString();
                if (input.isEmpty()) {
                } else {
                    otherplace.add(input);
                    ra6.notifyDataSetChanged();
                    other.setText("Другие места");
                    update();
                }
            }
        });

        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        ListView list;
        private GestureDetector gestureDetector;
        private Context context;

        public OnSwipeTouchListener(Context ctx, ListView list) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            context = ctx;
            this.list = list;
        }

        public OnSwipeTouchListener() {
            super();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);

        }

        public void onSwipeRight(int pos) {
            if(list.getAdapter()==resultAdapter){
                Toast.makeText(getApplicationContext(),"Место "+museum.get(pos)+" удалено", Toast.LENGTH_SHORT).show();
                museum.remove(pos);
                resultAdapter.notifyDataSetChanged();
                update();
                if (museum.size()==0){
                    tvmus.setText("Вы изучили все музеи");
                }
            }
            if(list.getAdapter()==resultAdapter1){
                Toast.makeText(getApplicationContext(),"Место "+theater.get(pos)+" удалено", Toast.LENGTH_SHORT).show();
                theater.remove(pos);
                resultAdapter1.notifyDataSetChanged();
                update();
                if (theater.size()==0){
                    tvthr.setText("Вы изучили все театры");
                }
            }
            if(list.getAdapter()==resultAdapter2){
                Toast.makeText(getApplicationContext(),"Место "+cinema.get(pos)+" удалено", Toast.LENGTH_SHORT).show();
                cinema.remove(pos);
                resultAdapter2.notifyDataSetChanged();
                update();
                if (cinema.size()==0){
                    tvcin.setText("Вы изучили все кинотеатры");
                }
            }
            if(list.getAdapter()==resultAdapter3){
                Toast.makeText(getApplicationContext(),"Место "+memorial.get(pos)+" удалено", Toast.LENGTH_SHORT).show();
                memorial.remove(pos);
                resultAdapter3.notifyDataSetChanged();
                update();
                if (memorial.size()==0){
                    tvmem.setText("Вы изучили все скульптуры");
                }
            }
            if(list.getAdapter()==ra4){
                Toast.makeText(getApplicationContext(),"Место "+square.get(pos)+" удалено", Toast.LENGTH_SHORT).show();
                square.remove(pos);
                ra4.notifyDataSetChanged();
                update();
                if (square.size()==0){
                    tvsqr.setText("Вы изучили все парки");
                }
            }
            if(list.getAdapter()==ra5){
                Toast.makeText(getApplicationContext(),"Место "+moll.get(pos)+" удалено", Toast.LENGTH_SHORT).show();
                moll.remove(pos);
                ra5.notifyDataSetChanged();
                update();
                    if (moll.size()==0){
                        tvmoll.setText("Вы изучили все ТЦ");
                    }

            }
            if(list.getAdapter()==ra6){
                Toast.makeText(getApplicationContext(),"Место "+otherplace.get(pos)+" удалено", Toast.LENGTH_SHORT).show();
                otherplace.remove(pos);
                ra6.notifyDataSetChanged();
                update();
                if (otherplace.size()==0){
                    tvmoll.setText("");
                }
            }
        }


        public void onSwipeLeft(int pos) {
            if(list.getAdapter()==resultAdapter){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
                String formatted =museum.get(pos).toString()+'\n'+ format1.format(cal.getTime());
                Toast.makeText(getApplicationContext(),"Место "+museum.get(pos)+" изучено "+format1.format(cal.getTime()), Toast.LENGTH_SHORT).show();
                        history.add(formatted);
                museum.remove(pos);
                resultAdapter.notifyDataSetChanged();
                update();
                if (museum.size()==0){
                    tvmus.setText("Вы изучили все музеи");
                }
            }
            if(list.getAdapter()==resultAdapter1){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
                String formatted =theater.get(pos).toString()+'\n'+ format1.format(cal.getTime());
                Toast.makeText(getApplicationContext(),"Место "+theater.get(pos)+" изучено "+format1.format(cal.getTime()), Toast.LENGTH_SHORT).show();
                        history.add(formatted);
                theater.remove(pos);
                resultAdapter1.notifyDataSetChanged();
                update();
                if (theater.size()==0){
                    tvthr.setText("Вы изучили все театры");
                }

            }
            if(list.getAdapter()==resultAdapter2){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
                String formatted =cinema.get(pos).toString()+'\n'+ format1.format(cal.getTime());
                Toast.makeText(getApplicationContext(),"Место "+cinema.get(pos)+" изучено "+format1.format(cal.getTime()), Toast.LENGTH_SHORT).show();
                        history.add(formatted);
                cinema.remove(pos);
                resultAdapter2.notifyDataSetChanged();
                update();
                if (cinema.size()==0){
                    tvcin.setText("Вы изучили все кинотеатры");
                }
            }
            if(list.getAdapter()==resultAdapter3){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
                String formatted =memorial.get(pos).toString()+'\n'+ format1.format(cal.getTime());
                Toast.makeText(getApplicationContext(),"Место "+memorial.get(pos)+" изучено "+format1.format(cal.getTime()), Toast.LENGTH_SHORT).show();
                        history.add(formatted);
                memorial.remove(pos);
                resultAdapter3.notifyDataSetChanged();
                update();
                if (memorial.size()==0){
                    tvmem.setText("Вы изучили все ТЦ");
                }
            }
            if(list.getAdapter()==ra4){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
                String formatted =square.get(pos).toString()+'\n'+ format1.format(cal.getTime());
                Toast.makeText(getApplicationContext(),"Место "+square.get(pos)+" изучено "+format1.format(cal.getTime()), Toast.LENGTH_SHORT).show();
                        history.add(formatted);
                square.remove(pos);
                ra4.notifyDataSetChanged();
                update();
                if (square.size()==0){
                    tvsqr.setText("Вы изучили все парки");
                }
            }
            if(list.getAdapter()==ra5){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
                String formatted =moll.get(pos).toString()+'\n'+ format1.format(cal.getTime());
                Toast.makeText(getApplicationContext(),"Место "+moll.get(pos)+" изучено "+format1.format(cal.getTime()), Toast.LENGTH_SHORT).show();
                        history.add(formatted);
                moll.remove(pos);
                ra5.notifyDataSetChanged();
                update();
                if (moll.size()==0){
                    tvmoll.setText("Вы изучили все ТЦ");
                }
            }
            if(list.getAdapter()==ra6){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
                String formatted =otherplace.get(pos).toString()+'\n'+ format1.format(cal.getTime());
                Toast.makeText(getApplicationContext(),"Место "+otherplace.get(pos)+" изучено "+format1.format(cal.getTime()), Toast.LENGTH_SHORT).show();
                        history.add(formatted);
                otherplace.remove(pos);
                ra6.notifyDataSetChanged();
                update();
                if (otherplace.size()==0){
                    other.setText("");
                }
            }
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            private int getPostion(MotionEvent e1) {
                return list.pointToPosition((int) e1.getX(), (int) e1.getY());
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY)
                        && Math.abs(distanceX) > SWIPE_THRESHOLD
                        && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight(getPostion(e1));
                    else
                        onSwipeLeft(getPostion(e1));
                    return true;
                }
                return false;
            }

        }
    }
    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
        finish();

        super.onBackPressed();
    }
}
