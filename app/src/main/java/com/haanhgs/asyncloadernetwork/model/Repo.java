package com.haanhgs.asyncloadernetwork.model;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import androidx.lifecycle.MutableLiveData;

public class Repo {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 8, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());
    private List<Book>bookList = new ArrayList<>();
    private MutableLiveData<List<Book>>liveData = new MutableLiveData<>();

    public Repo() {
        liveData.setValue(bookList);
    }

    public MutableLiveData<List<Book>> getLiveData() {
        return liveData;
    }

    private Uri getQueryUri(String query){
        return Uri.parse(Constants.BOOK_URL).buildUpon()
                .appendQueryParameter(Constants.QUERY, query)
                .appendQueryParameter(Constants.MAX_RESULT, "40")
                .appendQueryParameter(Constants.PRINT_TYPE, "books")
                .build();
    }

    private HttpURLConnection getConnection(Uri uri)throws IOException {
        URL url = new URL(uri.toString());
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }

    private String getResult(HttpURLConnection connection)throws IOException{
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) builder.append(line);
        return builder.length() == 0 ? null : builder.toString();
    }

    private Task<String> getQueryResult(String query){
        return Tasks.call(executor, () -> {
            String result = null;
            Uri uri = getQueryUri(query);
            try{
                HttpURLConnection connection = getConnection(uri);
                result = getResult(connection);
            }catch (Exception e){
                Log.e("E.Repo", "error getting book info");
            }
            return result;
        });
    }

    private Book getBookFromJson(JSONObject jsonObject)throws JSONException{
        Book book = new Book();
        JSONObject volumeInfo = jsonObject.getJSONObject(Constants.VOLUME_INFO);
        book.setTitle(volumeInfo.getString(Constants.TITLE));
        StringBuilder builder  = new StringBuilder();
        JSONArray jsonArray = volumeInfo.getJSONArray(Constants.AUTHORS);
        for (int i = 0; i < jsonArray.length(); i++){
            if (i == jsonArray.length() - 1){
                builder.append(jsonArray.getString(i));
            }else {
                builder.append(jsonArray.getString(i)).append("\n");
            }
        }
        book.setAuthors(builder.toString());
        return book;
    }

    private void getBookListFromResult(String string){
        bookList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(string);
            JSONArray booksJson = jsonObject.getJSONArray(Constants.ITEMS);
            for (int i = 0; i < booksJson.length(); i++){
                JSONObject bookJson = booksJson.getJSONObject(i);
                bookList.add(getBookFromJson(bookJson));
            }
        }catch (Exception e){
            Log.e("E.Repo", "error getting book list");
        }
        liveData.postValue(bookList);
    }


    public void runQuery(String query){
        getQueryResult(query)
                .addOnFailureListener(e -> Log.e("E.Repo", "error getting book info"))
                .addOnSuccessListener(this::getBookListFromResult);
    }
}
