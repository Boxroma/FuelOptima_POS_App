package io.grindallday.endrone_mobile_app.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.model.Client;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.Station;
import io.grindallday.endrone_mobile_app.model.User;
import timber.log.Timber;

public class FireStoreRepository {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Product>> productListliveData = new MutableLiveData<>();
    private final MutableLiveData<List<Sale>> saleListliveData = new MutableLiveData<>();
    private final MutableLiveData<List<Client>> clientListLiveData = new MutableLiveData<>();
    private final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    private final String userId;
    private final String stationId;
    private final String shiftId;
    private final Long loginMills;

    public FireStoreRepository(Application application) {
        SharedPreferences sharedPref = application.getSharedPreferences("pref", Context.MODE_PRIVATE);
        userId = sharedPref.getString("clientId","");
        stationId = sharedPref.getString("stationId", "");
        loginMills = sharedPref.getLong("loginMills",0);
        shiftId =  sharedPref.getString("shiftId","");


        Timber.tag(TAG).d("Retrieved user details: \n\tUser Id: %s \n\tStation Id: %s", userId, stationId);

    }

    private final MutableLiveData<Station> stationMutableLiveData = new MutableLiveData<>();

    private final String TAG = "FireStoreRepository";


    /**
     * Method to Pull Products for the fuel station
     * */
    public MutableLiveData<List<Product>> getProductLiveData(){

        Query query = firebaseFirestore.collection("products").whereEqualTo("station_id",stationId);
        query.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, error) -> {
            if (error != null) {
                Timber.tag("FireStoreRepository").w(error);
                return;
            }

            List<Product> products = new ArrayList<>();
            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {
                    if (doc.get("name") != null) {
                        products.add(
                                new Product(
                                        doc.getId(),
                                        doc. getString("name"),
                                        doc.getString("description"),
                                        doc.getDouble("price") != null ? doc.getDouble("price") : 0.0,
                                        doc.getString("type"),
                                        doc.getString("station_id"),
                                        "",
                                        doc.getDouble("quantity") != null ? doc.getDouble("quantity") : 0.0,
                                        doc.getBoolean("active") != null ? doc.getBoolean("active") : false)
                        );
                        Timber.tag(TAG).d(doc.getId());
                    }

                }
            } else {
                Timber.tag(TAG).e("snapshots not returned");
            }
            String source = snapshots.getMetadata().isFromCache() ? "local cache" : "server";
            Timber.tag("FireStoreRepository").d("Data fetched from : %s", source);
            Timber.tag("FireStoreRepository").d("Current Products: %s", products);
            productListliveData.setValue(products);
        });

        return productListliveData;
    }

    /**
     * Method to Update Product Info for the fuel station
     * */
    public void updateProductDetails (String id, Double quantity) {
        DocumentReference productRef = firebaseFirestore.collection("products").document(id);

        productRef.update("quantity", quantity)
                .addOnSuccessListener(unused -> Timber.tag(TAG).d("Updated %s quantity to %s",id,quantity))
                .addOnFailureListener(e -> Timber.tag(TAG).d(e));
    }

    /**
     * Method to Pull Sates for the fuel station
     * */
    public MutableLiveData<List<Sale>> getSaleLiveData(){
        Date date = new Date(loginMills);
        Timber.tag(TAG).d("Login Timestamp: %s", date);

        Query query = firebaseFirestore.collection("sales")
                .whereEqualTo("station_id",stationId)
                .whereEqualTo("attendant_id",userId)
                .whereGreaterThan("time",date);

        query.addSnapshotListener(MetadataChanges.INCLUDE,(snapshots, error) -> {
            if (error != null){
                Timber.tag("FireStoreRepository").w(error);
                return;
            }

            List<Sale> sales = new ArrayList<>();
            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {

                    Timber.tag(TAG).d("Product List: %s", doc.get("productList"));
                    List<Product> productList = new ArrayList<>();

                    // Get Product List
                    List<Map<String, Object>> objectList = (List<Map<String, Object>>) doc.get("productList");

                    if (objectList != null){
                        Timber.tag(TAG).d("Object List Not Null");
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
                                            Double.parseDouble(entry.get("quantity").toString()),
                                            true
                                    )
                            );
                        }
                    } else {
                        Timber.tag(TAG).d("Failed to return Product List");
                    }

                    Timber.tag(TAG).d("Count of products in sale: %s", productList.size());

                    //Get Sale Data and create Sales Object
                    sales.add(
                            new Sale(doc.getString("uid"),
                                    doc.getString("attendant_id"),
                                    doc.getString("attendant_name"),
                                    doc.getString("shift_Id"),
                                    doc.getString("client_id"),
                                    doc.getString("clientType"),
                                    doc.getString("client_name"),
                                    doc.getString("station_id"),
                                    doc.getString("stationName"),
                                    doc.getTimestamp("time"),
                                    doc.getDouble("total"),
                                    productList)
                    );
                    Timber.tag(TAG).d("document added %s", doc.getId());
                    Timber.tag(TAG).d("date time: %s", doc.getDate("time"));
                }

            } else {
                Timber.tag(TAG).e("snapshots not returned");
            }
            String source = snapshots.getMetadata().isFromCache() ? "local cache" : "server";
            Timber.tag("FireStoreRepository").d("Data fetched from : %s", source);
            Timber.tag("FireStoreRepository").d("Current Sales: %s", sales.size());
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
                        Timber.tag(TAG).d("Sale Successfully Created");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.tag(TAG).w(e);
                    }
                });
    }

    /**
     * Method to Pull Clients for the fuel station
     * */
    public MutableLiveData<List<Client>> getClientsLiveData(){

        Query query = firebaseFirestore.collection("clients").whereEqualTo("stationId",stationId);

        query.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, error) -> {
            if (error != null){
                Timber.tag(TAG).w(error);
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
                                    doc.getDouble("currentBalance")
                            )
                    );
                    Timber.tag(TAG).d("Client Name: %s", doc.getString("name"));
                }

            } else {
                Timber.tag(TAG).e("snapshots not returned");
            }
            String source = snapshots.getMetadata().isFromCache() ? "local cache" : "server";
            Timber.tag("FireStoreRepository").d("Data fetched from : %s", source);
            Timber.tag("FireStoreRepository").d("Current Client Count: %s", clients.size());
            clientListLiveData.setValue(clients);
        });

        return clientListLiveData;
    }

    /**
     * Method to Update Client Info for the fuel station
     * */
    public void updateClientDetails (Client client) {

        DocumentReference clientRef = firebaseFirestore.collection("clients").document(client.getUid());
        clientRef.update("currentBalance", client.getCurrentBalance())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Timber.tag(TAG).d("Updated Client %s new balance = %s",client.getName(),client.getCurrentBalance());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.tag(TAG).d(e);
                    }
                });
    }


    /**
     * Method to Pull User Details for the fuel station
     * */
    public MutableLiveData<User> getUserInfo(){

        Timber.tag(TAG).d("User Id: %s", userId);

        if(!Objects.equals(userId, "")){
            DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
            documentReference.addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, error) -> {
                if(error != null){
                    Timber.tag(TAG).d(error, "Listen Failed: ");
                }

                User user = new User();
                if(documentSnapshot != null){
                    Timber.tag(TAG).d("user data: %s", documentSnapshot.getData());
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

            });
        }

        return userMutableLiveData;
    }

    /**
     * Method to Pull Station Details for the fuel station
     * */
    public MutableLiveData<Station> getStationInfo(){

        Timber.tag(TAG).d("get Station ID: %s", stationId);

        DocumentReference document = firebaseFirestore.collection("stations").document(stationId);
        document.addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, error) -> {
            if (error != null) {
                Timber.tag(TAG).d(error, "Listen Failed: ");
            }

            Station station = new Station();
            if (documentSnapshot != null) {
                Timber.tag(TAG).d("Station Data: %s", documentSnapshot.getString("name"));
                station = new Station(
                        documentSnapshot.getString("address"),
                        documentSnapshot.getTimestamp("created"),
                        documentSnapshot.getDouble("dieselTankSize").toString(),
                        documentSnapshot.getDouble("petrolTankSize").toString(),
                        documentSnapshot.getDouble("keroseneTankSize").toString(),
                        documentSnapshot.getString("noDieselPumps"),
                        documentSnapshot.getString("noPetrolPumps"),
                        documentSnapshot.getString("noKerosenePumps"),
                        documentSnapshot.getString("name"));
            }
            stationMutableLiveData.setValue(station);
        });
        return stationMutableLiveData;
    }


}