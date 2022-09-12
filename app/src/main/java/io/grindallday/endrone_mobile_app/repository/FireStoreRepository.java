package io.grindallday.endrone_mobile_app.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.grindallday.endrone_mobile_app.liveData.ProductListLiveData;
import io.grindallday.endrone_mobile_app.model.Product;

public class FireStoreRepository {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private MutableLiveData<List<Product>> liveData = new MutableLiveData();

    public MutableLiveData<List<Product>> getFireStoreLiveData(){

        firebaseFirestore.collection("products")
                .whereEqualTo("type","product")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Log.w("FireStoreRepository", error);
                            return;
                        }

                        List<Product> products = new ArrayList<>();
                        for(QueryDocumentSnapshot doc : value) {
                            if (doc.get("name") != null){
                                products.add(new Product(doc.getString("name")));
                                Log.d("FireStoreRepository", doc.getString("name"));
                            }

                        }
                        String source = value.getMetadata().isFromCache() ? "local cache" : "server";
                        Log.d("FireStoreRepository", "Data fetched from : " + source);
                        Log.d("FireStoreRepository", "Current Products: " + products);
                        liveData.setValue(products);
                    }
                });

        return liveData;
    }
}