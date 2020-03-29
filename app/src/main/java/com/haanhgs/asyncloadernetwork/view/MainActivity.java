package com.haanhgs.asyncloadernetwork.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.haanhgs.asyncloadernetwork.R;
import com.haanhgs.asyncloadernetwork.model.Book;
import com.haanhgs.asyncloadernetwork.viewmodel.MyViewModel;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.haanhgs.asyncloadernetwork.view.MainHelper.hideKeyboard;
import static com.haanhgs.asyncloadernetwork.view.MainHelper.isNetworkAvailable;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etQuery)
    EditText etQuery;
    @BindView(R.id.bnSearch)
    Button bnSearch;
    @BindView(R.id.rvMain)
    RecyclerView rvMain;

    private MyViewModel viewModel;
    private BookAdapter adapter = new BookAdapter();
    private List<Book> bookList = new ArrayList<>();

    private void initRecyclerView(){
        rvMain.setLayoutManager(new LinearLayoutManager(this));
        adapter.setBookList(bookList);
        rvMain.setAdapter(adapter);
    }

    private void initViewModel(){
        viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        viewModel.getData().observe(this, books -> {
            bookList.clear();
            bookList.addAll(books);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initRecyclerView();
        initViewModel();
    }

    private void handleButton(View view){
        if (!TextUtils.isEmpty(etQuery.getText()) && isNetworkAvailable(this)){
            hideKeyboard(this, view);
            viewModel.runQuery(etQuery.getText().toString());
        }
    }

    @OnClick(R.id.bnSearch)
    public void onViewClicked(View view) {
        handleButton(view);
    }

}
