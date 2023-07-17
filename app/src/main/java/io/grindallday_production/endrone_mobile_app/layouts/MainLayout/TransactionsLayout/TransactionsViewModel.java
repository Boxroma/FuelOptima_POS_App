package io.grindallday_production.endrone_mobile_app.layouts.MainLayout.TransactionsLayout;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.grindallday_production.endrone_mobile_app.model.Sale;
import io.grindallday_production.endrone_mobile_app.repository.FireStoreRepository;

public class TransactionsViewModel extends AndroidViewModel {
    private final FireStoreRepository fireStoreRepository;


    public TransactionsViewModel(Application application) {
        super(application);
        this.fireStoreRepository = new FireStoreRepository(application);
    }

    public LiveData<List<Sale>> getSales(){
        return fireStoreRepository.getSaleLiveData();
    }
}