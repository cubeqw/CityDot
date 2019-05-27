package com.cubeqw.citydot;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {
    protected ArrayAdapter resultAdapter;
    ListView lv;
    ArrayList<Object> l=new ArrayList<>();
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
        for (int i = history.size()-1; i >=0 ; i--) {
            l.add(history.get(i));
        }
        resultAdapter=setArrayAdapter(l);
        lv.setAdapter(resultAdapter);
        if (history.size()!=0){
    tv.setText("Вы побывали:");
}
        else {tv.setText("Вы не посетили ни одного места");}
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.clear:
                final View v = new View(this);
                        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                        alert.setMessage("Вы действительно хотите очистить истортю посещённых мест?");
                        alert.setTitle("Сброс");
                        alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                history.clear();
                                l.clear();
                                tinydb.remove("history");
                                resultAdapter.notifyDataSetChanged();
                            }
                        });

                        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                        alert.show();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }
}
