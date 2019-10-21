package com.haanhgs.asyncloaderrecyclerviewnetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private String title;
    private List<String> authors;

    public Book(){}

    public Book(JSONObject jsonObject)throws JSONException{
        JSONObject volumeInfo = jsonObject.getJSONObject(Constants.VOLUME_INFO);
        title = volumeInfo.getString(Constants.TITLE);
        JSONArray jsonArray = volumeInfo.getJSONArray(Constants.AUTHORS);
        authors = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            authors.add(jsonArray.getString(i));
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
