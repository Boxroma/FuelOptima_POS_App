package io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.databinding.FragmentHomeBinding;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.Adapters.ProductAdapter;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.CheckoutDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.DisplayCartDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.FuelSaleDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.ProductSaleDialogFragment;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.AddProductDialogListener;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.MakeSaleDialogListener;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.RemoveProductDialogListener;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.ShowCheckoutDialogListener;
import io.grindallday.endrone_mobile_app.layouts.StartShiftLayout.StartShiftActivity;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeActivity;
import io.grindallday.endrone_mobile_app.model.Client;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.model.Pump;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.Shift;
import io.grindallday.endrone_mobile_app.model.Station;
import io.grindallday.endrone_mobile_app.model.User;
import io.grindallday.endrone_mobile_app.utils.PrintThread;
import timber.log.Timber;

public class HomeFragment extends Fragment implements AddProductDialogListener,
        RemoveProductDialogListener, ShowCheckoutDialogListener, MakeSaleDialogListener {

    private static final String TAG = "HomeFragment";
    private FirebaseAuth mAuth;
    public RecyclerView recyclerView;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private ProductAdapter productAdapter;
    private List<Product> products;
    private ArrayList<Product> cartList;
    private List<Pump> pumpList;
    private List<Client> clientList;
    private List<Sale> saleList = new ArrayList<>();
    private User currentUser;
    private Shift currentShift;
    private Station currentStation;
    private Product petrolObject, dieselObject, keroseneObject;
    private boolean shiftSync = false;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    public HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        //initialize shared preference
        sharedPref = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

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

        shiftSync = false;

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
        int noOfColumns = calculateNoOfColumns(requireContext(), 160);
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
                editor.putString("shiftId", station.getShiftId());
                editor.apply();
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
                Timber.tag(TAG).d("Client Count : %s",clientList.size());
            }
        });

        homeViewModel.getSaleList().observe(getViewLifecycleOwner(), sales -> {
            if (sales!=null){
                saleList = sales;
                Timber.tag(TAG).d("Sales List Updated");
            }
        });

        homeViewModel.getPumps().observe(getViewLifecycleOwner(), pumps -> {
            if (pumps!=null){
                pumpList = pumps;
            }
        });

        binding.shoppingCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getContext(), "Cart Button Pressed", Toast.LENGTH_SHORT).show();
                Timber.tag(TAG).d("Cart Button Pressed");
                showCurrentCartDialog(homeViewModel.cartItems.getValue());
            }
        });

        binding.cvPetrolCard.setOnClickListener(view -> {
            // Toast.makeText(getContext(), "Petrol Item Selected", Toast.LENGTH_SHORT).show();
            Timber.tag(TAG).d("Petrol Item Selected");
            showFuelSaleDialog(petrolObject);
        });

        binding.cvDieselCard.setOnClickListener(view -> {
            // Toast.makeText(getContext(), "Diesel Item Selected", Toast.LENGTH_SHORT).show();
            Timber.tag(TAG).d("Diesel Item Selected");
            showFuelSaleDialog(dieselObject);
        });

        binding.cvKeroseneCard.setOnClickListener(view -> {
            // Toast.makeText(getContext(), "Kerosene Item Selected", Toast.LENGTH_SHORT).show();
            Timber.tag(TAG).d("Kerosene Item Selected");
            showFuelSaleDialog(keroseneObject);
        });
    }

    @SuppressLint("DefaultLocale")
    public void setCartButtonUI(List<Product> productList){

        double total = 0.0;
        if(productList !=null){
            for(Product product : productList){
                total = total + (product.getPrice() * product.getQuantity());
            }
        }
        binding.shoppingCartButton.setText(String.format("Cart Total: %,.2f", total));
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

    public void printReceipt(Sale sale, User user){
        Sale reciptSaleItem = new Sale(
                sale.getUid(),
                sale.getAttendant_id(),
                sale.getAttendantName(),
                sale.getShift_id(),
                sale.getShiftStaffId(),
                sale.getClientId(),
                sale.getClientType(),
                sale.getClientName(),
                sale.getStation_id(),
                sale.getStationName(),
                sale.getTime(),
                sale.getTotal(),
                sale.getProductList()
        );

        PrintThread printThread = new PrintThread(requireContext(), reciptSaleItem, user, reciptSaleItem.getProductList(), currentStation);
        printThread.start();
    }

    public void showFuelSaleDialog(Product product) {
        FuelSaleDialogFragment dialogFragment = FuelSaleDialogFragment.newInstance(product,currentStation,pumpList);
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

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example column Width dp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = (displayMetrics.widthPixels / displayMetrics.density);
        return (int) (screenWidthDp / columnWidthDp);
    }
}