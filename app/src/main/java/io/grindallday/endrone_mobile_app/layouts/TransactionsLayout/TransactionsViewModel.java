package io.grindallday.endrone_mobile_app.layouts.TransactionsLayout;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.repository.FireStoreRepository;

public class TransactionsViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private final FireStoreRepository fireStoreRepository;


    public TransactionsViewModel(Application application) {
        super(application);
        this.fireStoreRepository = new FireStoreRepository(application);
    }

    public LiveData<List<Sale>> getSales(){
        return fireStoreRepository.getSaleLiveData();
    }
}