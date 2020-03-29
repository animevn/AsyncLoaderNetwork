package com.haanhgs.asyncloadernetwork.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.haanhgs.asyncloadernetwork.R;
import com.haanhgs.asyncloadernetwork.model.Constants;
import com.haanhgs.asyncloadernetwork.repo.BookLoader;
import com.haanhgs.asyncloadernetwork.repo.Repo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    @BindView(R.id.etQuery)
    EditText etQuery;
    @BindView(R.id.bnSearch)
    Button bnSearch;
    @BindView(R.id.rvMain)
    RecyclerView rvMain;

    private Loader<String> loader;

    private void initLoader(){
        if (LoaderManager.getInstance(this).getLoader(1) != null){
            LoaderManager.getInstance(this).initLoader(1, null, this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initLoader();
    }

    private void handleButton(View view){
        if (!TextUtils.isEmpty(etQuery.getText()) && Repo.isNetworkAvailable(this)){
            Repo.hideKeyboard(this, view);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SEARCH_QUERY, etQuery.getText().toString());
            LoaderManager.getInstance(this).restartLoader(1, bundle, this);
        }
    }

    @OnClick(R.id.bnSearch)
    public void onViewClicked(View view) {
        handleButton(view);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String query = args == null ? "" : args.getString(Constants.SEARCH_QUERY);
        return new BookLoader(this, query);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data != null){
            rvMain.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            rvMain.setAdapter(new BookAdapter(Repo.getBookList(data)));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
