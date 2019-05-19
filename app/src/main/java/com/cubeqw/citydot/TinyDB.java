package com.cubeqw.citydot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;


public class TinyDB {

    private SharedPreferences preferences;
    public TinyDB(Context appContext) {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }
    public ArrayList<String> getListString(String key) {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }

    public ArrayList<Object> getListObject(String key, Class<?> mClass){
    	Gson gson = new Gson();

    	ArrayList<String> objStrings = getListString(key);
    	ArrayList<Object> objects =  new ArrayList<Object>();

    	for(String jObjString : objStrings){
    		Object value  = gson.fromJson(jObjString,  mClass);
    		objects.add(value);
    	}
    	return objects;
    }

    public void putListString(String key, ArrayList<String> stringList) {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

    public void putListObject(String key, ArrayList<Object> objArray){
    	checkForNullKey(key);
    	Gson gson = new Gson();
    	ArrayList<String> objStrings = new ArrayList<String>();
  	for(Object obj : objArray){
    		objStrings.add(gson.toJson(obj));
    	}
    	putListString(key, objStrings);
    }


    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

        public void clear() {
        preferences.edit().clear().apply();
    }
    public void checkForNullKey(String key){
        if (key == null){
            throw new NullPointerException();
        }

    }
    public void putString(String key, String value) {
        checkForNullKey(key); checkForNullValue(value);
        preferences.edit().putString(key, value).apply();
    }
    public void checkForNullValue(String value){
        if (value == null){
            throw new NullPointerException();
        }
    }
    }
