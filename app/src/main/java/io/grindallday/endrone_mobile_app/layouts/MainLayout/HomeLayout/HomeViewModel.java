package io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import io.grindallday.endrone_mobile_app.model.Client;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.Shift;
import io.grindallday.endrone_mobile_app.model.Station;
import io.grindallday.endrone_mobile_app.model.User;
import io.grindallday.endrone_mobile_app.repository.FireStoreRepository;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "HomeViewModel";

    private final FireStoreRepository fireStoreRepository;

    public Double total = 0.0;

    public MutableLiveData<List<Product>> cartItems = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        fireStoreRepository = new FireStoreRepository(application);
    }

    // Methods to expose live data from Firestore Repository
    public MutableLiveData<Station> getStation() {
        return fireStoreRepository.getStationInfo();
    }

    public MutableLiveData<User> getUserMutableLiveData(){
        return fireStoreRepository.getUserInfo();
    }

    public LiveData<List<Product>> getProductList(){
        return fireStoreRepository.getProductLiveData();
    }

    public LiveData<List<Sale>> getSaleList(){
        return fireStoreRepository.getSaleLiveData();
    }

    public void makeSale(Sale sale){
        fireStoreRepository.createSale(sale);
    }

    public LiveData<List<Client>> getClientList(){
        return fireStoreRepository.getClientsLiveData();
    }

    public void updateClient(Client client){
        fireStoreRepository.updateClientDetails(client);
    }

    public void updateProduct(String id, Double quantity){
        fireStoreRepository.updateProductDetails(id,quantity);
    }

    // Method to handle add to cart action
    public void addToCart(Product product){
        List<Product> tempProductList = new ArrayList<>();

        //Get Current ProductList
        if(cartItems.getValue() != null){
            if(cartItems.getValue().size() != 0){
                tempProductList = cartItems.getValue();
            }
        }

        //Set
        boolean exists = false;
        for (Product product1: tempProductList){
            if(product1.getProduct_id().equals(product.getProduct_id())){
                product1.setQuantity(product.getQuantity());
                exists = true;
                break;
            }
        }

        if (!exists){
            //Add Item to List
            tempProductList.add(product);
        }

        //Set new list
        cartItems.setValue(tempProductList);
        Log.d(TAG,"added product " + product.getName() + " to Cart");
        Log.d(TAG,"quantity: " + product.getQuantity());
        Log.d(TAG, "price: " + (product.getPrice() * product.getQuantity()));
        Log.d(TAG,"Current Cart Size " + cartItems.getValue().size());

        setTotal();

    }

    //Method to handle remove from cart action
    public void removeFromCart(Product product){
        List<Product> tempProductList = new ArrayList<>();

        //Get Current ProductList
        if(cartItems.getValue() != null){
            if(cartItems.getValue().size() != 0){
                tempProductList = cartItems.getValue();
            }
        }

        tempProductList.remove(product);

        //Set new list
        cartItems.setValue(tempProductList);
        Log.d(TAG,"removed product " + product.getName() + " to Cart");
        Log.d(TAG,"quantity: " + product.getQuantity());
        Log.d(TAG, "price: " + (product.getPrice() * product.getQuantity()));
        //Log.d(TAG,"Current Cart Size " + cartItems.getValue().size());

        setTotal();

    }

    public void clearCart(){
        List<Product> tempCart = cartItems.getValue();
        if(tempCart!=null) {
            tempCart.clear();
        }
        cartItems.setValue(tempCart);
    }


    //Method to update cart Items
    public void setTotal(){
        double tempTotal = 0.0;
        //for every product in the list we multiply the quantity by the price and do some shit
        List<Product> cartList = cartItems.getValue();
        if(cartList!=null){
            for(Product product : cartList){
                tempTotal = tempTotal + (product.getPrice() * product.getQuantity());
            }
        }

        this.total = tempTotal;
        Log.d(TAG, "Updated Total: " + this.total);
    }

}