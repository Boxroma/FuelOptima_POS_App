package io.grindallday.endrone_mobile_app.layouts.EndShiftLayout;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Field;
import java.util.List;

import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.repository.FireStoreRepository;

public class EndShiftViewModel extends AndroidViewModel {
    private final FireStoreRepository fireStoreRepository;

    public EndShiftViewModel(@NonNull Application application) {
        super(application);
        this.fireStoreRepository = new FireStoreRepository(application);

    }

    public LiveData<List<Sale>> getSales() {
        return fireStoreRepository.getSaleLiveData();
    }
}