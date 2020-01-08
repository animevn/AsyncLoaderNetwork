package com.haanhgs.asyncloaderrecyclerviewnetwork.repo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.haanhgs.asyncloaderrecyclerviewnetwork.model.Constants;
import com.haanhgs.asyncloaderrecyclerviewnetwork.model.Book;
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
import static android.content.Context.INPUT_METHOD_SERVICE;

public class Repo {

    public static void hideKeyboard(Context context, View view){
        InputMethodManager manager = (InputMethodManager)
                context.getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null){
            manager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities info = manager.getNetworkCapabilities(manager.getActiveNetwork());
            return info != null
                    && (info.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || info.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }else if (manager != null && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP){
            NetworkInfo info = manager.getActiveNetworkInfo();
            return  info != null &&info.isConnected();
        }
        return false;
    }

    private static Uri getQueryUri(String query){
        return Uri.parse(Constants.BOOK_URL).buildUpon()
                .appendQueryParameter(Constants.QUERY, query)
                .appendQueryParameter(Constants.MAX_RESULT, "40")
                .appendQueryParameter(Constants.PRINT_TYPE, "books")
                .build();
    }

    private static HttpURLConnection getConnection(Uri uri)throws IOException {
        URL url = new URL(uri.toString());
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }

    private static String getResult(HttpURLConnection connection)throws IOException{
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) builder.append(line);
        return builder.length() == 0 ? null : builder.toString();
    }

    public static String getBookInfo(String query){
        String result = null;
        Uri uri = getQueryUri(query);
        try{
            HttpURLConnection connection = getConnection(uri);
            result = getResult(connection);
        }catch (Exception e){
            Log.e("E.Repo", "error getting book info");
        }
        return result;
    }

    private static Book getBook(JSONObject jsonObject)throws JSONException{
        Book book = new Book();
        JSONObject volumeInfo = jsonObject.getJSONObject(Constants.VOLUME_INFO);
        book.setTitle(volumeInfo.getString(Constants.TITLE));
        List<String> authors = new ArrayList<>();
        JSONArray jsonArray = volumeInfo.getJSONArray(Constants.AUTHORS);
        for (int i = 0; i < jsonArray.length(); i++){
            authors.add(jsonArray.getString(i));
        }
        book.setAuthors(authors);
        return book;
    }

    public static List<Book> getBookList(String string){
        List<Book> bookList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(string);
            JSONArray booksJson = jsonObject.getJSONArray(Constants.ITEMS);
            for (int i = 0; i < booksJson.length(); i++){
                JSONObject bookJson = booksJson.getJSONObject(i);
                bookList.add(getBook(bookJson));
            }
        }catch (Exception e){
            Log.e("E.Repo", "error getting book list");
        }
        return bookList;
    }

    public static String getAuthors(List<String> list){
        StringBuilder builder  = new StringBuilder();
        for (int i = 0; i < list.size(); i++){
            if (i == list.size() - 1){
                builder.append(list.get(i));
            }else {
                builder.append(list.get(i)).append("\n");
            }
        }
        return builder.toString();
    }

}
