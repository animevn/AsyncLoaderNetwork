package com.haanhgs.asyncloaderrecyclerviewnetwork;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class ResultLoader extends AsyncTaskLoader<String> {

    private String query;

    public ResultLoader(@NonNull Context context, String query) {
        super(context);
        this.query = query;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return JsonParser.getBookInfo(query);
    }
}
