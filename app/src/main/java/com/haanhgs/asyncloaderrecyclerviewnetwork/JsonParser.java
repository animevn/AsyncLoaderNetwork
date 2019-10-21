package com.haanhgs.asyncloaderrecyclerviewnetwork;

import android.net.Uri;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class JsonParser {

    private JsonParser(){}

    private static Uri buildUri(String query){
        return Uri.parse(Constants.BOOK_URL).buildUpon()
                .appendQueryParameter(Constants.QUERY, query)
                .appendQueryParameter(Constants.MAX_RESULT, "20")
                .appendQueryParameter(Constants.PRINT_TYPE, "books")
                .build();
    }

    private static HttpURLConnection connectToUri(Uri uri)throws IOException {
        URL url = new URL(uri.toString());
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        return urlConnection;
    }

    private static String getResultFromURL(HttpURLConnection urlConnection)throws IOException{
        InputStream inputStream = urlConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) stringBuilder.append(line);
        return stringBuilder.length() > 0 ? stringBuilder.toString() : null;
    }

    public static String getBookInfo(String query){
        String result = null;
        Uri uri = buildUri(query);
        try{
            HttpURLConnection urlConnection = connectToUri(uri);
            result = getResultFromURL(urlConnection);
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    public static List<Book> getBookListFromResult(String string){
        List<Book> bookList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray(Constants.ITEMS);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                bookList.add(new Book(object));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return bookList;
    }
}
