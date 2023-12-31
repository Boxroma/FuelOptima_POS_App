package io.grindallday_production.endrone_mobile_app.repository;

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

import io.grindallday_production.endrone_mobile_app.model.Client;
import io.grindallday_production.endrone_mobile_app.model.Product;
import io.grindallday_production.endrone_mobile_app.model.Pump;
import io.grindallday_production.endrone_mobile_app.model.Sale;
import io.grindallday_production.endrone_mobile_app.model.Shift;
import io.grindallday_production.endrone_mobile_app.model.ShiftStaff;
import io.grindallday_production.endrone_mobile_app.model.Station;
import io.grindallday_production.endrone_mobile_app.model.User;
import timber.log.Timber;

public class FireStoreRepository {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Product>> productListliveData = new MutableLiveData<>();
    private final MutableLiveData<List<Sale>> saleListliveData = new MutableLiveData<>();
    private final MutableLiveData<List<Client>> clientListLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Pump>> pumpListliveData = new MutableLiveData<>();
    private final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Shift> shiftMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ShiftStaff> shiftStaffMutableLiveData = new MutableLiveData<>();
    private final String userId;
    private final String stationId;
    private final String shiftId;
    private final String shiftStaffId;
    private final Long loginMills;

    SharedPreferences.Editor editor;


    private final String TAG = "FireStoreRepository";
    private final MutableLiveData<Station> stationMutableLiveData = new MutableLiveData<>();

    public FireStoreRepository(Application application) {
        //initialize shared preference
        SharedPreferences sharedPref = application.getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        userId = sharedPref.getString("userId", "");
        stationId = sharedPref.getString("stationId", "");
        loginMills = sharedPref.getLong("loginMills", 0);
        shiftId = sharedPref.getString("shiftId", "");
        shiftStaffId = sharedPref.getString("shiftStaffId", "");

        Timber.tag(TAG).d("Retrieved user details: \n\tUser Id: %s \n\tStation Id: %s \n\tShift Id: %s \n\tShiftStaff Id: %s", userId, stationId, shiftId, shiftStaffId);

    }

    /**
     * Method to Pull Products for the fuel station
     */
    public MutableLiveData<List<Product>> getProductLiveData() {

        Query query = firebaseFirestore.collection("products").whereEqualTo("station_id", stationId);
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
                                        doc.getString("name"),
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
     */
    public void updateProductDetails(String id, Double quantity) {
        DocumentReference productRef = firebaseFirestore.collection("products").document(id);

        productRef.update("quantity", quantity)
                .addOnSuccessListener(unused -> Timber.tag(TAG).d("Updated %s quantity to %s", id, quantity))
                .addOnFailureListener(e -> Timber.tag(TAG).d(e));
    }

    /**
     * Method to Pull Sales for the fuel station
     */
    public MutableLiveData<List<Sale>> getSaleLiveData() {
        Date date = new Date(loginMills);
        Timber.tag(TAG).d("Login Timestamp: %s", date);

        if (!Objects.equals(shiftStaffId, "")){
            Query query = firebaseFirestore.collection("sales")
                    .whereEqualTo("shiftStaffId", shiftStaffId);

            query.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, error) -> {
                if (error != null) {
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

                        if (objectList != null) {
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
                                        doc.getString("shiftStaffId"),
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
        }

        return saleListliveData;
    }

    /**
     * Method to Add Sales for the fuel station
     */
    public void createSale(Sale sale) {

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
     */
    public MutableLiveData<List<Client>> getClientsLiveData() {

        Query query = firebaseFirestore.collection("clients")
                .whereEqualTo("stationId", stationId)
                .whereEqualTo("active", true);

        query.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, error) -> {
            if (error != null) {
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
     */
    public void updateClientDetails(Client client) {

        DocumentReference clientRef = firebaseFirestore.collection("clients").document(client.getUid());
        clientRef.update("currentBalance", client.getCurrentBalance())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Timber.tag(TAG).d("Updated Client %s new balance = %s", client.getName(), client.getCurrentBalance());
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
     */
    public MutableLiveData<User> getUserInfo() {

        Timber.tag(TAG).d("User Id: %s", userId);

        if (!Objects.equals(userId, "")) {
            DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
            documentReference.addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, error) -> {
                if (error != null) {
                    Timber.tag(TAG).d(error, "Listen Failed: ");
                }

                User user = new User();
                if (documentSnapshot != null) {
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
     */
    public MutableLiveData<Station> getStationInfo() {

        Timber.tag(TAG).d("get Station ID: %s", stationId);

        DocumentReference document = firebaseFirestore.collection("stations").document(stationId);
        document.addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, error) -> {
            if (error != null) {
                Timber.tag(TAG).d(error, "Listen Failed: ");
            }

            Station station = null;
            if (documentSnapshot != null) {
                Timber.tag(TAG).d("Station Data: %s", documentSnapshot.getString("name"));
                station = new Station(
                        documentSnapshot.getId(),
                        documentSnapshot.getString("address"),
                        documentSnapshot.getTimestamp("created"),
                        documentSnapshot.getString("activeShiftId"),
                        documentSnapshot.getDouble("dieselTankSize"),
                        documentSnapshot.getDouble("petrolTankSize"),
                        documentSnapshot.getDouble("keroseneTankSize"),
                        documentSnapshot.get("noDieselPumps"),
                        documentSnapshot.get("noPetrolPumps"),
                        documentSnapshot.get("noKerosenePumps"),
                        documentSnapshot.getString("name"));
            }
            stationMutableLiveData.setValue(station);
        });
        return stationMutableLiveData;
    }

    /**
     * Method to Pull Shift Details for the fuel station
     */
    public MutableLiveData<Shift> getShiftInfo() {

        Timber.tag(TAG).d("get Shift ID: %s", shiftId);

        if (!Objects.equals(shiftId, "")) {
            DocumentReference document = firebaseFirestore.collection("shifts").document(shiftId);
            document.addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, error) -> {
                if (error != null) {
                    Timber.tag(TAG).d(error, "Listen Failed: ");
                }

                Shift shift = new Shift();
                if (documentSnapshot != null) {
                    Timber.tag(TAG).d("Station Data: %s", documentSnapshot.getString("name"));

                    /*  */
                    Timber.tag(TAG).d("Product List: %s", documentSnapshot.get("staff"));

                    shift = new Shift(
                            documentSnapshot.getId(),
                            documentSnapshot.getString("name"),
                            documentSnapshot.getTimestamp("start"),
                            documentSnapshot.getTimestamp("stop"),
                            documentSnapshot.getString("stationId"),
                            documentSnapshot.getString("status"));
                }
                shiftMutableLiveData.setValue(shift);

                editor.putString("shiftName", shift.getName());
                editor.putString("shiftStart", shift.getStart().toDate().toString());
                editor.putString("shiftStop", shift.getStop().toDate().toString());
                editor.apply();

            });
        }

        else {
            Timber.tag(TAG).d("Shift Id Not Set");
        }
        return shiftMutableLiveData;
    }

    /**
     * Method to Pull ShiftStaff Details for the fuel station
     */
    public MutableLiveData<ShiftStaff> getShiftStaff() {

        Timber.tag(TAG).d("get ShiftStaff ID: %s", shiftStaffId);

        if (!Objects.equals(shiftStaffId, "")) {
            DocumentReference document = firebaseFirestore.collection("shiftStaff").document(shiftStaffId);
            document.addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, error) -> {
                if (error != null) {
                    Timber.tag(TAG).d(error, "Listen Failed: ");
                }

                ShiftStaff shiftStaff = new ShiftStaff();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Timber.tag(TAG).d("ShiftStaff Data: %s", documentSnapshot.getString("name"));

                    shiftStaff = new ShiftStaff(
                            documentSnapshot.getId(),
                            documentSnapshot.getString("shiftId"),
                            documentSnapshot.getString("stationId"),
                            documentSnapshot.getString("userId"),
                            documentSnapshot.getString("userName"),
                            documentSnapshot.getTimestamp("startTime"),
                            documentSnapshot.getTimestamp("endTime"),
                            Boolean.TRUE.equals(documentSnapshot.getBoolean("active")),
                            documentSnapshot.getDouble("totalSales"),
                            documentSnapshot.getDouble("expectedCash"),
                            documentSnapshot.getBoolean("reconciled")
                    );
                }
                shiftStaffMutableLiveData.setValue(shiftStaff);

                if (shiftStaff != null){
                    editor.putString("shiftStaffName", shiftStaff.getUserName());
//                    editor.putString("shiftStaffStart", shiftStaff.getStartTime().toDate().toString());
//                    editor.putString("shiftStaffStop", shiftStaff.getEndTime().toDate().toString());
                    editor.apply();
                }
            });
        }


        else {
            Timber.tag(TAG).d("Shift Id Not Set");
        }
        return shiftStaffMutableLiveData;
    }

    /**
     * Method to Add Staff Object to List Of Staff in Shift Object
     */
    public void updateShiftDetails(ShiftStaff staff) {
        DocumentReference document = firebaseFirestore.collection("shifts").document(shiftId);
        // document.update("staff", FieldValue.arrayUnion()
    }

    /**
     * Method to Pull Pumps for the fuel station
     */
    public MutableLiveData<List<Pump>> getPumpLiveData() {

        Query query = firebaseFirestore.collection("stationPumps")
                .whereEqualTo("stationId", stationId);

        query.addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, error) -> {
            if (error != null) {
                Timber.tag("FireStoreRepository").w(error);
                return;
            }

            List<Pump> pumps = new ArrayList<>();
            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {

                    //Get Sale Data and create Sales Object
                    pumps.add(
                            new Pump(doc.getString("id"),
                                    doc.getString("name"),
                                    doc.getString("stationId"),
                                    doc.getString("type"))
                    );
                    Timber.tag(TAG).d("Pump Pulled \nid: %s \nname: %s \ntype: %s", doc.getString("id"), doc.getString("name"), doc.getString("type"));
                    Timber.tag(TAG).d("date time: %s", doc.getDate("time"));
                }
            } else {
                Timber.tag(TAG).e("snapshots not returned");
            }
            Timber.tag("FireStoreRepository").d("Pump Count: %s", pumps.size());
            pumpListliveData.setValue(pumps);
        });
        return pumpListliveData;
    }


}