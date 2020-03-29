package com.haanhgs.asyncloadernetwork.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class MainHelper {

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

}
