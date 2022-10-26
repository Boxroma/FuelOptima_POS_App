package io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.databinding.FragmentHomeBinding;
import io.grindallday.endrone_mobile_app.layouts.EndShiftLayout.EndShiftActivity;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.Adapters.ProductAdapter;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.CheckoutDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.DisplayCartDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.FuelSaleDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.ProductSaleDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.AddProductDialogListener;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.EndShiftDialogListener;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.MakeSaleDialogListener;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.RemoveProductDialogListener;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.ShowCheckoutDialogListener;
import io.grindallday.endrone_mobile_app.layouts.StartShiftLayout.StartShiftActivity;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeActivity;
import io.grindallday.endrone_mobile_app.model.Client;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.Shift;
import io.grindallday.endrone_mobile_app.model.Station;
import io.grindallday.endrone_mobile_app.model.User;
import io.grindallday.endrone_mobile_app.utils.PrintThread;
import timber.log.Timber;

public class HomeFragment extends Fragment implements AddProductDialogListener,
        RemoveProductDialogListener, ShowCheckoutDialogListener, MakeSaleDialogListener, EndShiftDialogListener {

    private static final String TAG = "HomeFragment";
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    public RecyclerView recyclerView;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private ProductAdapter productAdapter;
    private List<Product> products;
    private ArrayList<Product> cartList;
    private List<Client> clientList;
    private List<Sale> saleList;
    private User currentUser;
    private Station currentStation;
    private Product petrolObject, dieselObject, keroseneObject;


    public HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(firebaseUser == null){
            //reload();
            Intent intent = new Intent(getActivity(), StartShiftActivity.class);
            startActivity(intent);

        } else {
            //set user data
            String userId = firebaseUser.getUid();
            Timber.tag(TAG).d("User ID: %s", userId);

            //Set UI elements after confirming user exists
            setUi();

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container, false);
        View view = binding.getRoot();

        recyclerView = binding.recyclerView;

        return view;
    }

    @SuppressLint("DefaultLocale")
    private void update_ui(List<Product> productList) {
        if (productList != null){
            for (Product product : productList){
                switch (product.getName()) {
                    case "Petrol":
                        binding.tvFuelPriceP.setText(String.format("ZMK %,.2f",product.getPrice()));
                        String petrolID = product.getProduct_id();
                        petrolObject = product;
                        break;
                    case "Diesel":
                        binding.tvFuelPriceD.setText(String.format("ZMK %,.2f",product.getPrice()));
                        String dieselID = product.getProduct_id();
                        dieselObject = product;
                        break;
                    case "Kerosene":
                        binding.tvFuelPriceK.setText(String.format("ZMK %,.2f",product.getPrice()));
                        String keroseneID = product.getProduct_id();
                        keroseneObject = product;
                        break;
                    default:
                        break;
                }
            }

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Some Dumb Stuff

    }

    public void setUi(){

        productAdapter = new ProductAdapter(getContext(),HomeFragment.this);

        int noOfColumns = calculateNoOfColumns(requireContext(), 260);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), noOfColumns));
        recyclerView.setAdapter(productAdapter);

        homeViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(requireActivity().getApplication()))
                .get(HomeViewModel.class);

        homeViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null){
                currentUser = user;
                Timber.tag(TAG).d("Current User Info: %s", currentUser.getFirstName());
            }
        });

        homeViewModel.getStation().observe(getViewLifecycleOwner(), station -> {
            if (station != null){
                currentStation = station;
                Timber.tag(TAG).d("Station Info: %s", currentStation.getName());
            }
        });

        homeViewModel.getProductList().observe(getViewLifecycleOwner(), productList -> {
            if (productList != null){
                productAdapter.setProductList(productList);
                products = productList;
                update_ui(products);
            } else {
                Timber.tag("HomeFragment").d("Waiting For info");
            }
        });

        homeViewModel.cartItems.observe(getViewLifecycleOwner(), productList1 -> {
            if (productList1 != null){
                setCartButtonUI(productList1);
                cartList = (ArrayList<Product>) productList1;
                Timber.tag(TAG).d("Cart Item changed");
            }
        });

        homeViewModel.getClientList().observe(getViewLifecycleOwner(), clientList1 -> {
            if (clientList1!=null){
                clientList = clientList1;
                Timber.tag(TAG).d("Client List Updated");
            }
        });

        homeViewModel.getSaleList().observe(getViewLifecycleOwner(), sales -> {
            if (sales!=null){
                saleList = sales;
                updateTotal();
                Timber.tag(TAG).d("Sales List Updated");
                setDrawerValues();
            }
        });

        binding.shoppingCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Cart Button Pressed", Toast.LENGTH_SHORT).show();
                showCurrentCartDialog(homeViewModel.cartItems.getValue());
            }
        });

        binding.cvPetrolCard.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Petrol Item Selected", Toast.LENGTH_SHORT).show();
            showFuelSaleDialog(petrolObject);
        });

        binding.cvDieselCard.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Diesel Item Selected", Toast.LENGTH_SHORT).show();
            showFuelSaleDialog(dieselObject);
        });

        binding.cvKeroseneCard.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Kerosene Item Selected", Toast.LENGTH_SHORT).show();
            showFuelSaleDialog(keroseneObject);
        });

        updateShiftInfo();

    }

    @SuppressLint("DefaultLocale")
    private void updateTotal() {

        Timber.tag(TAG).d("Update Shift called");

        double total = 0.0;
        if (saleList!=null){
            for(Sale sale : saleList){
                total = total + sale.getTotal();
            }
        }

        ((HomeActivity) requireActivity()).setToolbarText(String.format("ZMW %,.2f",total));
    }

    public void setDrawerValues(){
        ((HomeActivity) requireActivity()).setNavigationDrawer(currentUser,saleList);
    }

    public void setCartButtonUI(List<Product> productList){

        double total = 0.0;
        if(productList !=null){
            for(Product product : productList){
                total = total + (product.getPrice() * product.getQuantity());
            }
        }
        binding.shoppingCartButton.setText(String.format("Cart Total: %s",total));
    }

    public void removeProduct(Product product){
        homeViewModel.removeFromCart(product);
    }

    @Override
    public void onAddProduct(Product product) {
        Timber.tag(TAG).d("Add Product method called: %s", product.getName());
        homeViewModel.addToCart(product);
    }

    @Override
    public void onRemoveProduct(Product product) {
        Timber.tag(TAG).d("Remove Product method called: %s", product.getName());
        homeViewModel.removeFromCart(product);
    }

    @Override
    public void showCheckoutDialog() {
        showCheckoutDialog(cartList,clientList,currentUser);
    }

    @Override
    public void onMakeSale(Sale sale, boolean print) {

        //Check if client exists and handle accordingly
        if (!Objects.equals(sale.getClientId(), "")){
            Client client = new Client();
            for (Client client1 : clientList) {
                if (Objects.equals(client1.getUid(), sale.getClientId())) {
                    client = client1;
                }
            }
            //if client exists in sales object check client balance
            if (client.getCurrentBalance() >= sale.getTotal()){
                //if balance checkouts proceed with transaction deduction
                client.setCurrentBalance(client.getCurrentBalance() - sale.getTotal());
                homeViewModel.updateClient(client);
            } else {
                //if balance less fail transaction
                Toast.makeText(getContext(), "Transaction failed.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //Handle Product quantity deductions
        for (Product product: products){
            Timber.tag(TAG).d("Parent Product Name %s and Quantity %s",product.getName(),product.getQuantity());
            for (Product subProduct: sale.getProductList()){
                if (Objects.equals(product.getProduct_id(), subProduct.getProduct_id())){
                    Timber.tag(TAG).d("Parent Product Quantity: %s - Child Product Quantity: %s",product.getQuantity(),subProduct.getQuantity());
                    Double quantity = product.getQuantity() - subProduct.getQuantity();
                    homeViewModel.updateProduct(product.getProduct_id(),quantity);
                    break;
                }
            }
        }

        //Sale
        Timber.tag(TAG).d("Product Count: %s", sale.getProductList().size());
        //Create Sale
        if (print){
            printReceipt(sale, currentUser);
        }

        //Write Sale Item
        homeViewModel.makeSale(sale);

        //Clear cart items
        cartList.clear();
        setCartButtonUI(cartList);

        //Show completed
        Toast.makeText(getContext(),"Purchase Processed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onEndShift(Timestamp timestamp) {

    }

    public void updateShiftInfo(){
        Timber.tag(TAG).d("Update Shift Info Called");
        ((HomeActivity)requireActivity()).updateShiftInfo(null,true);
        //homeViewModel.updateShift(null, true);
    }


    public void printReceipt(Sale sale, User user){
        PrintThread printThread = new PrintThread(requireContext(),sale, user);
        printThread.start();
    }

    public void showFuelSaleDialog(Product product) {
        FuelSaleDialogFragment dialogFragment = FuelSaleDialogFragment.newInstance(product,currentStation);
        dialogFragment.setListener(this);
        dialogFragment.show(getParentFragmentManager(),"ADD FUEL SELL DIALOG");
    }

    public void showProductSaleDialog(Product product){
        ProductSaleDialogFragment dialogFragment = ProductSaleDialogFragment.newInstance(product);
        dialogFragment.setListener(this);
        dialogFragment.show(getParentFragmentManager(),"ADD FUEL PRODUCT DIALOG");
    }

    public void showCurrentCartDialog(List<Product> productList){
        DisplayCartDialogFragment dialogFragment = DisplayCartDialogFragment.newInstance(productList);
        dialogFragment.setListener(this);
        dialogFragment.setShowCheckoutDialogListener(this);
        dialogFragment.show(getParentFragmentManager(),"DISPLAY CART ITEMS DIALOG");
    }

    public void showCheckoutDialog(List<Product> productList, List<Client> clientList, User user){
        CheckoutDialogFragment dialogFragment = CheckoutDialogFragment.newInstance(productList, clientList, currentStation, user, requireActivity().getApplication());
        dialogFragment.setListener(this);
        dialogFragment.show(getParentFragmentManager(),"DISPLAY CHECKOUT DIALOG");
    }

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = (displayMetrics.widthPixels / displayMetrics.density);
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

}