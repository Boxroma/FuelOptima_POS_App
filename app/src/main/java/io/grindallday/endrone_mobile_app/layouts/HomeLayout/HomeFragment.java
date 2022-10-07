package io.grindallday.endrone_mobile_app.layouts.HomeLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.databinding.FragmentHomeBinding;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.Adapters.ProductAdapter;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.DialogFragments.CheckoutDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.DialogFragments.DisplayCartDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.DialogFragments.FuelSaleDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.DialogFragments.ProductSaleDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.interfaces.AddProductDialogListener;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.interfaces.MakeSaleDialogListener;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.interfaces.RemoveProductDialogListener;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.interfaces.ShowCheckoutDialogListener;
import io.grindallday.endrone_mobile_app.layouts.MainActivity;
import io.grindallday.endrone_mobile_app.model.Client;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.Station;
import io.grindallday.endrone_mobile_app.model.User;
import io.grindallday.endrone_mobile_app.utils.PrintThread;

public class HomeFragment extends Fragment implements AddProductDialogListener, RemoveProductDialogListener, ShowCheckoutDialogListener, MakeSaleDialogListener {

    private static final String TAG = "HomeFragment";
    private FirebaseAuth mAuth;
    public RecyclerView recyclerView;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private ProductAdapter productAdapter;
    private List<Product> cartList;
    private List<Client> clientList;
    private List<Sale> saleList;
    private User currentUser;
    private Station currentStation;
    private Product petrolObject, dieselObject, keroseneObject;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null){
            //reload();

            NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_loginFragment);
        } else {
            //set user data
            String userId = firebaseUser.getUid();
            Log.d(TAG,"User ID: " + userId);

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

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(productAdapter);

        homeViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(requireActivity().getApplication()))
                .get(HomeViewModel.class);

        homeViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null){
                currentUser = user;
                Log.d(TAG,"Current User Info: " + currentUser.getFirstName());
                ((MainActivity) requireActivity()).setNavigationDrawer(currentUser,saleList);
            }
        });

        homeViewModel.getStation().observe(getViewLifecycleOwner(), station -> {
            if (station != null){
                currentStation = station;
                Log.d(TAG, "Station Info: " + currentStation.getName());
            }
        });

        homeViewModel.getProductList().observe(getViewLifecycleOwner(), productList -> {

            if (productList != null){
                productAdapter.setProductList(productList);

                update_ui(productList);
            } else {
                Log.d("HomeFragment", "Waiting For info");
            }
        });

        homeViewModel.cartItems.observe(getViewLifecycleOwner(), productList1 -> {
            if (productList1 != null){
                setCartButtonUI(productList1);
                cartList = productList1;
                Log.d(TAG, "Cart Item changed");
            }
        });

        homeViewModel.getClientList().observe(getViewLifecycleOwner(), clientList1 -> {
            if (clientList1!=null){
                clientList = clientList1;
                Log.d(TAG, "Client List Updated");
            }
        });

        homeViewModel.getSaleList().observe(getViewLifecycleOwner(), sales -> {
            if (sales!=null){
                saleList = sales;
                updateTotal();
                Log.d(TAG, "Sales List Updated");
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

    }

    @SuppressLint("DefaultLocale")
    private void updateTotal() {

        double total = 0.0;
        if (saleList!=null){
            for(Sale sale : saleList){
                total = total + sale.getTotal();
            }
        }

        ((MainActivity) requireActivity()).setToolbarText(String.format("ZMW %,.2f",total));
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
        Log.d(TAG, "Add Product method called: " + product.getName());
        homeViewModel.addToCart(product);
    }

    @Override
    public void onRemoveProduct(Product product) {
        Log.d(TAG, "Remove Product method called: " + product.getName());
        homeViewModel.removeFromCart(product);
    }

    @Override
    public void showCheckoutDialog() {
        showCheckoutDialog(cartList,clientList,currentUser);
    }

    @Override
    public void onMakeSale(Sale sale, boolean print) {

        //Create Sale
        if (print){
            printReceipt(sale, currentUser);
        }

        //Write Sale Item
        homeViewModel.makeSale(sale);

        //Clear cart items
        cartList = new ArrayList<>();

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
        CheckoutDialogFragment dialogFragment = CheckoutDialogFragment.newInstance(productList, clientList, currentStation, user);
        dialogFragment.setListener(this);
        dialogFragment.show(getParentFragmentManager(),"DISPLAY CHECKOUT DIALOG");
    }
}