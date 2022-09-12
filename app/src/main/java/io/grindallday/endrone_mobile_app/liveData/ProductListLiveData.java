package io.grindallday.endrone_mobile_app.liveData;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.grindallday.endrone_mobile_app.model.Product;

public class ProductListLiveData extends LiveData<List<Product>> implements EventListener<QuerySnapshot> {

    private final List<Product> productListTemp = new ArrayList<>();
    public MutableLiveData<List<Product>> productList = new MutableLiveData<>();

    private final CollectionReference collectionReference;

    private ListenerRegistration listenerRegistration = () -> {};

    public ProductListLiveData(CollectionReference collectionReference){
        this.collectionReference = collectionReference;
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

        Log.d("ProductListLiveData","On Event Called");

        if (error != null) {
            Log.w("ProductListLiveData", error);
            return;
        }

        productListTemp.clear();

        for (QueryDocumentSnapshot doc : value) {
            if(doc.get("name") != null ){
                productListTemp.add(new Product(doc.getString("name")));
            }
        }

        Log.d("ProductListLiveData", "Current Products: " + productListTemp);

        productList.setValue(productListTemp);

    }

    @Override
    protected void onActive() {
        listenerRegistration = collectionReference.addSnapshotListener((value, error) -> {});
        super.onActive();
    }

    @Override
    protected void onInactive() {
        listenerRegistration.remove();
        super.onInactive();
    }
}
