package com.haanhgs.asyncloaderrecyclerviewnetwork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    private RecyclerView rvMain;
    private EditText etQuery;
    private Button bnSearch;

    private void initViews(){
        rvMain = findViewById(R.id.rvMain);
        etQuery = findViewById(R.id.etQuery);
        bnSearch = findViewById(R.id.bnSearch);
    }

    private void initLoader(){
        if (LoaderManager.getInstance(this).getLoader(1) != null){
            LoaderManager.getInstance(this).initLoader(1, null, this);
        }
    }

    private void hideKeyboard(View view){
        InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null){
            manager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void startSearching(View view){
        if (!TextUtils.isEmpty(etQuery.getText()) && Repo.checkNetwork(this)){
            hideKeyboard(view);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SEARCH_QUERY, etQuery.getText().toString());
            LoaderManager.getInstance(this).restartLoader(1, bundle, this);
        }
    }

    private void handleButton(){
        bnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearching(v);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initLoader();
        handleButton();
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String query = args != null ? args.getString(Constants.SEARCH_QUERY) : "";
        return new ResultLoader(this, query);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data != null){
            List<Book> bookList = Repo.getBookListFromResult(data);
            rvMain.setLayoutManager(new LinearLayoutManager(this));
            rvMain.setItemAnimator(new DefaultItemAnimator());
            rvMain.setAdapter(new BookAdapter(bookList));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
