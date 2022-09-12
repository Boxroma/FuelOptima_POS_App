package io.grindallday.endrone_mobile_app.layouts.HomeLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.grindallday.endrone_mobile_app.liveData.ProductListLiveData;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.repository.FireStoreRepository;

public class HomeViewModel extends ViewModel {

    private final FireStoreRepository fireStoreRepository = new FireStoreRepository();

    public LiveData<List<Product>> getProductList(){
        return fireStoreRepository.getFireStoreLiveData();
    }

}