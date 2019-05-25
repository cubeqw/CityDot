package com.cubeqw.citydot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {
    protected ArrayAdapter resultAdapter;
    ListView lv;
    public ArrayList<Object> history=new ArrayList<>();
TinyDB tinydb;
TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("История");
        setTheme(R.style.AppTheme2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        tv=findViewById(R.id.tv);
        lv=findViewById(R.id.lv);
        tinydb = new TinyDB(getApplicationContext());
        history= (tinydb.getListObject("history", String.class));
        ArrayList<Object> l=new ArrayList<>();
        for (int i = history.size()-1; i >=0 ; i--) {
            l.add(history.get(i));
        }
        resultAdapter=setArrayAdapter(l);
        lv.setAdapter(resultAdapter);
        if (history.size()!=0){
    tv.setText("Вы побывали:");
}
    }
    public void onClick(View v){
        Intent i=new Intent(this, MapsActivity.class);
        startActivity(i);
    }
    protected ArrayAdapter setArrayAdapter(List f) {

        ArrayAdapter g = new ArrayAdapter(this,
                R.layout.row_bg,
                R.id.text,
                f);
        return g;
    }
    public void onBackPressed()
    {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
        super.onBackPressed();
    }
}
