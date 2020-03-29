package com.haanhgs.asyncloadernetwork.viewmodel;

import com.haanhgs.asyncloadernetwork.model.Book;
import com.haanhgs.asyncloadernetwork.model.Repo;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    private Repo repo = new Repo();

    public LiveData<List<Book>> getData(){
        return repo.getLiveData();
    }

    public void runQuery(String query){
        repo.runQuery(query);
    }

}
