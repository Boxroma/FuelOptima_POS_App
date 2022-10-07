package io.grindallday.endrone_mobile_app.layouts.HomeLayout.DialogFragments;

import static io.grindallday.endrone_mobile_app.layouts.MainActivity.getScreenWidth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.databinding.DialogFragmentCheckoutDialogBinding;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.Adapters.ClientSpinnerAdapter;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.interfaces.AddProductDialogListener;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.interfaces.MakeSaleDialogListener;
import io.grindallday.endrone_mobile_app.model.Client;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.Station;
import io.grindallday.endrone_mobile_app.model.User;

public class CheckoutDialogFragment extends DialogFragment {

    public static String TAG = "DisplayCartDialogFragment";
    public static String product_id;
    public static List<Product> activeProductList = new ArrayList<>();
    public static List<Client> clientList = new ArrayList<>();
    private static Client activeClient;
    private static User user;
    private static Station station;
    private static Sale sale;
    private static double saleTotal;
    MakeSaleDialogListener makeSaleDialogListener;
    DialogFragmentCheckoutDialogBinding binding;
    private ClientSpinnerAdapter adapter;

    public CheckoutDialogFragment() {
    }

    public static CheckoutDialogFragment newInstance(List<Product> productList, List<Client> clientList,Station station, User user){
        activeProductList = productList;
        CheckoutDialogFragment.clientList = clientList;
        CheckoutDialogFragment.user = user;
        CheckoutDialogFragment.station = station;
        return new CheckoutDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null){
            product_id = getArguments().getString("product_id");
        }

        binding = DialogFragmentCheckoutDialogBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        Dialog dialog = new Dialog(getContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog);
        dialog.setContentView(view);

        setUi();

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = getScreenWidth() - 20;
        //params.height = getScreenHeight() - 20;
        window.setAttributes(params);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Fragment parentFragment = getParentFragment();
        if(parentFragment instanceof DialogInterface.OnDismissListener){
            ((DialogInterface.OnDismissListener) parentFragment).onDismiss(dialog);
        }
    }

    @SuppressLint("DefaultLocale")
    public void setUi(){

        setTotal();

        //Set Client Type
        String[] clientTypes = { "Drop In Client", "Pre-Paid Client"};
        ArrayAdapter clientTypeAdapter = new ArrayAdapter(getContext(), R.layout.list_item,clientTypes);
        binding.spClientType.setAdapter(clientTypeAdapter);

        binding.spClientType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(requireContext(),"Selected Type: " + clientTypes[i], Toast.LENGTH_SHORT).show();
                if(i == 0){
                    binding.llSelectClient.setVisibility(View.INVISIBLE);
                }else {
                    binding.llSelectClient.setVisibility(View.VISIBLE);
                }
            }

        });

        //Set Client Spinner
        adapter = new ClientSpinnerAdapter(requireContext(), android.R.layout.simple_spinner_item,clientList.toArray(new Client[0]));
        binding.spClient.setAdapter(adapter);

        binding.spClient.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Set Active Client
                activeClient = adapter.getItem(i);
                if(activeClient!=null) {
                    Toast.makeText(getContext(), "Current Client Selected: " + activeClient.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Set Button Listener
        binding.btConfirmSale.setOnClickListener(view -> {
            makeSale(activeProductList,user,activeClient);
            dismiss();
        });

    }

    @SuppressLint("DefaultLocale")
    private void setTotal(){

        //Display cart total
        double total = 0.00;

        for (Product product : activeProductList){
            total = total + (product.getPrice()*product.getQuantity());
        }

        saleTotal = total;

        binding.tvTotal.setText(String.format("Sale Total :ZMK %,.2f",total));
    }

    public void makeSale(List<Product> productList, User user, Client client){

        Timestamp timestamp = Timestamp.now();

        sale = new Sale(String.valueOf(UUID.randomUUID()),
                    user.getUid(),
                    user.getFirstName() + " " + user.getSecondName(),
                    client!=null ? client.getUid() : "",
                    client!=null ? "PrePaid" : "WalkInClient",
                    client!=null ? client.getName() : "",
                    user.getStationId(),
                    user.getStationName(),
                    timestamp,
                    saleTotal,
                    productList
            );

        //Make Sale Call
        makeSaleDialogListener.onMakeSale(sale, binding.cbPrint.isChecked());
    }

    public void setListener(MakeSaleDialogListener callback){
        try{
            makeSaleDialogListener = callback;
        }catch (ClassCastException e){
            throw new ClassCastException(callback.toString() + "Must implement MakeSaleDialogListener");
        }
    }



}
