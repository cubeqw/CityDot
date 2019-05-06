package com.example.itydot;


import android.util.Log;

import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.SearchType;
import com.yandex.mapkit.search.SuggestItem;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class FirstSearch implements SearchManager.SuggestListener{
    String query;
    double lat;
    double lon;
    private ArrayList<Object> suggestResult = new ArrayList<Object>();
    private static final int RESULT_NUMBER_LIMIT = 20;
    private final Point CENTER = new Point(lat, lon);
    private final double BOX_SIZE = 0.2;
    private final BoundingBox BOUNDING_BOX = new BoundingBox(
            new Point(CENTER.getLatitude() - BOX_SIZE, CENTER.getLongitude() - BOX_SIZE),
            new Point(CENTER.getLatitude() + BOX_SIZE, CENTER.getLongitude() + BOX_SIZE));
    private final SearchOptions SEARCH_OPTIONS = new SearchOptions().setSearchTypes(
            SearchType.GEO.value |
                    SearchType.BIZ.value |
                    SearchType.TRANSIT.value);
    private SearchManager searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

    public FirstSearch(String query) {
        this.query = query;
        requestSuggest(query);
        searchManager.suggest(query,BOUNDING_BOX,SEARCH_OPTIONS,this);
    }

    @Override
    public void onSuggestResponse(List<SuggestItem> suggest) {
        for (int i = 0; i < Math.min(RESULT_NUMBER_LIMIT, suggest.size()); i++) {
            suggestResult.add(suggest.get(i).getDisplayText());
            getPlaces();
        }
    }

    @Override
    public void onSuggestError(Error error) {
        if (error instanceof RemoteError) {
            Log.d("mytag2", "re");

        } else if (error instanceof NetworkError) {
            Log.d("mytag2", "ne");

        }

    }
    private void requestSuggest(String query) {
        searchManager.suggest(query, BOUNDING_BOX, SEARCH_OPTIONS, this);
    }
    public ArrayList<Object> getPlaces(){
        Log.d("mytag2", String.valueOf(suggestResult.size()));
        return suggestResult;
    }
}