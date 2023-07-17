package io.grindallday_production.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments;

import static io.grindallday_production.endrone_mobile_app.layouts.MainLayout.HomeActivity.getScreenWidth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.Timestamp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.grindallday_production.endrone_mobile_app.R;
import io.grindallday_production.endrone_mobile_app.databinding.DialogFragmentCheckoutDialogBinding;
import io.grindallday_production.endrone_mobile_app.layouts.MainLayout.HomeLayout.Adapters.ClientSpinnerAdapter;
import io.grindallday_production.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.MakeSaleDialogListener;
import io.grindallday_production.endrone_mobile_app.model.Client;
import io.grindallday_production.endrone_mobile_app.model.Product;
import io.grindallday_production.endrone_mobile_app.model.Sale;
import io.grindallday_production.endrone_mobile_app.model.Station;
import io.grindallday_production.endrone_mobile_app.model.User;
import timber.log.Timber;

public class CheckoutDialogFragment extends DialogFragment {

    public static String TAG = "DisplayCartDialogFragment";
    public static String product_id;
    public static List<Product> activeProductList = new ArrayList<>();
    public static List<Client> clientList = new ArrayList<>();
    private static Client activeClient;
    private static User user;
    private static String shiftId;
    private static String shiftStaffId;
    private static String selectedClientType;
    private static double saleTotal;
    private Dialog dialog;
    private View dialogView;
    MakeSaleDialogListener makeSaleDialogListener;
    DialogFragmentCheckoutDialogBinding binding;
    private ClientSpinnerAdapter adapter;

    public CheckoutDialogFragment() {
    }

    public static CheckoutDialogFragment newInstance(List<Product> productList, List<Client> clientList, Station station, User user, Application application){
        SharedPreferences sharedPref = application.getSharedPreferences("pref", Context.MODE_PRIVATE);
        activeProductList = productList;
        CheckoutDialogFragment.clientList = clientList;
        CheckoutDialogFragment.user = user;
        CheckoutDialogFragment.shiftId = sharedPref.getString("shiftId","");
        CheckoutDialogFragment.shiftStaffId = sharedPref.getString("shiftStaffId","");
        return new CheckoutDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null){
            product_id = getArguments().getString("product_id");
        }

        binding = DialogFragmentCheckoutDialogBinding.inflate(getLayoutInflater());
        dialogView = binding.getRoot();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        dialog = new Dialog(getContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog);
        dialog.setContentView(dialogView);

        setUi();

        activeClient = null;

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
        String[] clientTypes = { "Cash", "Mobile-Money", "Pre-Paid"};
        ArrayAdapter clientTypeAdapter = new ArrayAdapter(getContext(), R.layout.list_item,clientTypes);

        binding.spClientType.setShowSoftInputOnFocus(false);

        binding.spClientType.setAdapter(clientTypeAdapter);

        binding.spClientType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Toast.makeText(requireContext(),"Selected Type: " + clientTypes[i], Toast.LENGTH_SHORT).show();
                Timber.tag(TAG).d("Selected Type: %s", clientTypes[i] );
                if(i == 0 || i == 1){
                    binding.llSelectClient.setVisibility(View.INVISIBLE);
                }else {
                    binding.llSelectClient.setVisibility(View.VISIBLE);
                }
                selectedClientType = clientTypes[i];
            }

        });

        //Set Client Spinner
        adapter = new ClientSpinnerAdapter(requireContext(), R.layout.list_item,clientList.toArray(new Client[0]));

        binding.spClient.setAdapter(adapter);

        binding.spClient.setShowSoftInputOnFocus(false);

        binding.spClient.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Set Active Client
                activeClient = adapter.getItem(i);
                if(activeClient!=null) {
                    // Toast.makeText(getContext(), "Current Client Selected: " + activeClient.getName(), Toast.LENGTH_SHORT).show();
                    Timber.tag(TAG).d("Current Client Selected: %s", activeClient.getName());
                }
            }
        });

        //Set Button Listener
        binding.btConfirmSale.setOnClickListener(view -> {
            if (selectedClientType != null){
                makeSale(activeProductList,user,activeClient);
                dismiss();
            }
            else {
                Toast.makeText(getContext(),"Please select Payment Type",Toast.LENGTH_SHORT).show();
            }
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

        Timber.tag(TAG).d("Shift ID at create sale: %s", shiftId);

        Timestamp timestamp = Timestamp.now();

        Sale sale = new Sale(String.valueOf(UUID.randomUUID()),
                user.getUid(),
                user.getFirstName() + " " + user.getSecondName(),
                shiftId,
                shiftStaffId,
                client != null ? client.getUid() : "",
                selectedClientType,
                client != null ? client.getName() : "",
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
