package io.grindallday.endrone_mobile_app.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import io.grindallday.endrone_mobile_app.model.Client;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.Station;
import io.grindallday.endrone_mobile_app.model.User;

public class FireStoreRepository {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Product>> productListliveData = new MutableLiveData<>();
    private final MutableLiveData<List<Sale>> saleListliveData = new MutableLiveData<>();
    private final MutableLiveData<List<Client>> clientListLiveData = new MutableLiveData<>();
    private final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    private final String userId;
    private final String stationId;
    private final Long loginMills;

    public FireStoreRepository(Application application) {
        SharedPreferences sharedPref = application.getSharedPreferences("pref", Context.MODE_PRIVATE);
        userId = sharedPref.getString("clientId","");
        stationId = sharedPref.getString("stationId", "");
        loginMills = sharedPref.getLong("loginMills",0);


        Log.d(TAG, String.format("Retrieved user details: \n\tUser Id: %s \n\tStation Id: %s",userId,stationId));

    }

    private final MutableLiveData<Station> stationMutableLiveData = new MutableLiveData<>();

    private final String TAG = "FireStoreRepository";


    /**
     * Method to Pull Products for the fuel station
     * */
    public MutableLiveData<List<Product>> getProductLiveData(){

        Query query = firebaseFirestore.collection("products").whereEqualTo("station_id",stationId);
        query.addSnapshotListener((snapshots, error) -> {
            if (error != null){
                Log.w("FireStoreRepository", error);
                return;
            }

            List<Product> products = new ArrayList<>();
            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {
                    if (doc.get("name") != null) {
                        products.add(
                                new Product(
                                        doc.getId(),
                                        doc.getString("name"),
                                        doc.getString("description"),
                                        doc.getDouble("price") !=null ? doc.getDouble("price") : 0.0,
                                        doc.getString("type"),
                                        doc.getString("station_id"),
                                        "",
                                        0)
                        );
                        Log.d(TAG, doc.getId());
                    }

                }
            } else {
                Log.e(TAG,"snapshots not returned");
            }
            String source = snapshots.getMetadata().isFromCache() ? "local cache" : "server";
            Log.d("FireStoreRepository", "Data fetched from : " + source);
            Log.d("FireStoreRepository", "Current Products: " + products);
            productListliveData.setValue(products);
        });

        return productListliveData;
    }

    /**
     * Method to Pull Sates for the fuel station
     * */
    public MutableLiveData<List<Sale>> getSaleLiveData(){
        Date date = new Date(loginMills);
        Log.d(TAG, "Login Timestamp: " + date);

        Query query = firebaseFirestore.collection("sales")
                .whereEqualTo("station_id",stationId)
                .whereEqualTo("attendant_id",userId)
                .whereGreaterThan("time",date);

        query.addSnapshotListener((snapshots, error) -> {
            if (error != null){
                Log.w("FireStoreRepository", error);
                return;
            }

            List<Sale> sales = new ArrayList<>();
            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {

                    Log.d(TAG, "Product List: " + doc.get("productList"));
                    List<Product> productList = new ArrayList<>();

                    // Get Product List
                    List<Map<String, Object>> objectList = (List<Map<String, Object>>) doc.get("productList");

                    if (objectList != null){
                        Log.d(TAG,"Object List Not Null");
                        for (Map<String, Object> entry : objectList) {
                            productList.add(
                                    new Product(
                                            entry.get("product_id").toString(),
                                            entry.get("name").toString(),
                                            entry.get("description").toString(),
                                            Double.parseDouble(entry.get("price").toString()),
                                            entry.get("type").toString(),
                                            entry.get("station_id").toString(),
                                            entry.get("pump_no").toString(),
                                            Double.parseDouble(entry.get("quantity").toString())
                                    )
                            );
                        }
                    } else {
                        Log.d(TAG,"Failed to return Product List");
                    }

                    Log.d(TAG,"Count of products in sale: " + productList.size());

                    //Get Sale Data and create Sales Object
                    sales.add(
                            new Sale(doc.getString("uid"),
                                    doc.getString("attendant_id"),
                                    doc.getString("attendant_name"),
                                    doc.getString("client_id"),
                                    doc.getString("clientType"),
                                    doc.getString("client_name"),
                                    doc.getString("station_id"),
                                    doc.getString("stationName"),
                                    doc.getTimestamp("time"),
                                    doc.getDouble("total"),
                                    productList)
                    );
                    Log.d(TAG, "document added " + doc.getId());
                    Log.d(TAG, "date time: " + doc.getDate("time"));
                }

            } else {
                Log.e(TAG,"snapshots not returned");
            }
            String source = snapshots.getMetadata().isFromCache() ? "local cache" : "server";
            Log.d("FireStoreRepository", "Data fetched from : " + source);
            Log.d("FireStoreRepository", "Current Sales: " + sales.size());
            saleListliveData.setValue(sales);
        });

        return saleListliveData;
    }

    /**
     * Method to Add Sales for the fuel station
     * */
    public void createSale(Sale sale){

        firebaseFirestore.collection("sales").document(sale.getUid())
                .set(sale)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Sale Succesfully Created");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error Creating Sale: " + e);
                    }
                });
    }

    /**
     * Method to Pull Clients for the fuel station
     * */
    public MutableLiveData<List<Client>> getClientsLiveData(){

        Query query = firebaseFirestore.collection("clients").whereEqualTo("stationId",stationId);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.w("FireStoreRepository", error);
                    return;
                }

                List<Client> clients = new ArrayList<>();
                if (snapshots != null) {
                    for (QueryDocumentSnapshot doc : snapshots) {

                        //Get Sale Data and create Sales Object
                        clients.add(
                                new Client(
                                        doc.getId(),
                                        doc.getString("name"),
                                        doc.getString("email"),
                                        doc.getString("number"),
                                        doc.getTimestamp("dateCreated"),
                                        doc.getString("station_id"),
                                        doc.getDouble("total")
                                )
                        );
                        Log.d(TAG, "Client Name: " + doc.getString("name"));
                    }

                } else {
                    Log.e(TAG,"snapshots not returned");
                }
                String source = snapshots.getMetadata().isFromCache() ? "local cache" : "server";
                Log.d("FireStoreRepository", "Data fetched from : " + source);
                Log.d("FireStoreRepository", "Current Client Count: " + clients.size());
                clientListLiveData.setValue(clients);
            }
        });

        return clientListLiveData;
    }

    /**
     * Method to Pull User Details for the fuel station
     * */
    public MutableLiveData<User> getUserInfo(){

        Log.d(TAG, "User Id: " + userId);

        if(!Objects.equals(userId, "")){
            DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    if(error != null){
                        Log.d(TAG, "Listen Failed: ", error);
                    }

                    User user = new User();
                    if(documentSnapshot != null){
                        Log.d(TAG, "user data: " + documentSnapshot.getData());
                        user = new User(
                                documentSnapshot.getString("uid"),
                                documentSnapshot.getString("firstName"),
                                documentSnapshot.getString("secondName"),
                                documentSnapshot.getString("email"),
                                documentSnapshot.getString("date"),
                                documentSnapshot.getString("nrc"),
                                documentSnapshot.getString("stationId"),
                                documentSnapshot.getString("stationName"),
                                documentSnapshot.getString("stationAddress"),
                                documentSnapshot.getString("role"));
                    }

                    userMutableLiveData.setValue(user);

                }
            });
        }

        return userMutableLiveData;
    }

    /**
     * Method to Pull Station Details for the fuel station
     * */
    public MutableLiveData<Station> getStationInfo(){

        Log.d(TAG, "get Station ID: " + stationId);

        DocumentReference document = firebaseFirestore.collection("stations").document(stationId);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "Listen Failed: ", error);
                }

                Station station = new Station();
                if (documentSnapshot != null) {
                    Log.d(TAG, "Station Data: " + documentSnapshot.getString("name"));
                    station = new Station(
                            documentSnapshot.getString("address"),
                            documentSnapshot.getTimestamp("created"),
                            documentSnapshot.getString("dieselTankSize"),
                            documentSnapshot.getString("petrolTankSize"),
                            documentSnapshot.getString("keroseneTankSize"),
                            documentSnapshot.getString("noDieselPumps"),
                            documentSnapshot.getString("noPetrolPumps"),
                            documentSnapshot.getString("noKerosenePumps"),
                            documentSnapshot.getString("name"));
                }
                stationMutableLiveData.setValue(station);
            }
        });
        return stationMutableLiveData;
    }

}