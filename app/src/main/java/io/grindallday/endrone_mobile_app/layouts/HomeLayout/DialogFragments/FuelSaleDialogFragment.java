package io.grindallday.endrone_mobile_app.layouts.HomeLayout.DialogFragments;

import static io.grindallday.endrone_mobile_app.layouts.MainActivity.getScreenWidth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.databinding.DialogFragmentFuelSaleBinding;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.interfaces.AddProductDialogListener;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.model.Station;

public class FuelSaleDialogFragment extends DialogFragment {

    public static String TAG = "FuelSaleDialogFragment";
    public static String product_id;
    public static String selected_pump;
    public static Product activeProduct = new Product();
    public static Station activeStation = new Station();
    private Double quantity;
    private Double amount;
    AddProductDialogListener mCallback;
    DialogFragmentFuelSaleBinding binding;
    boolean focused = false;

    public FuelSaleDialogFragment() {
    }

    public static FuelSaleDialogFragment newInstance(Product product, Station station){
        activeProduct = product;
        activeStation = station;
        return new FuelSaleDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null){
            product_id = getArguments().getString("product_id");
        }

        binding = DialogFragmentFuelSaleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        Dialog dialog = new Dialog(getContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog);
        dialog.setContentView(view);

        setUi();

        updateUI(activeProduct);

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
        //params.height = getScreenHeight() - 60;
        window.setAttributes(params);
    }

    @SuppressLint("DefaultLocale")
    public void updateUI(Product product){
        binding.tvCardTitle.setText(String.format("Enter amount of %s to be purchased", product.getName()));
        binding.tvProductName.setText(product.getName());
        binding.tvProductName.setVisibility(View.INVISIBLE);
        binding.tvProductDescription.setText(product.getDescription());
        binding.tvProductUnitPrice.setText(String.format("Unit Price: ZMK %,.2f", product.getPrice())); //ZMK %,.2f
    }

    public void setUi(){

        setDropDown();

        if(activeProduct != null) {
            binding.etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    focused = b;
                    Log.d(TAG, "is focused: " + b);
                }
            });
            binding.etAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @SuppressLint("DefaultLocale")
                @Override
                public void afterTextChanged(Editable editable) {
                    // TODO Format Text to ZMK

                    // Set liter count
                    if (editable != null) {
                        if (!editable.toString().equals("")){
                            if (focused) {
                                double value = Double.parseDouble(editable.toString());
                                // Set liter count
                                quantity = value / activeProduct.getPrice();
                                binding.etLiters.setText(String.format("%,.2f", quantity));
                            }
                        } else {
                            if (focused) {
                                binding.etLiters.setText(String.valueOf(0));
                            }
                        }
                    } else {
                        binding.etLiters.setText(String.valueOf(0));
                    }
                }
            });

            binding.etLiters.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @SuppressLint("DefaultLocale")
                @Override
                public void afterTextChanged(Editable editable) {
                    if (!editable.toString().equals("")){
                        if(!focused){
                            quantity = Double.parseDouble(editable.toString());
                            // Set ZMW amount
                            amount = quantity * activeProduct.getPrice();
                            binding.etAmount.setText(String.format("%,.2f",amount));
                        }
                    } else {
                        if(!focused){
                            binding.etLiters.setText(String.valueOf(0));
                        }
                    }

                }
            });

            binding.btAdd.setOnClickListener(view -> {

                if (quantity != null){
                    if(activeProduct != null && !Objects.equals(activeProduct.getPump_no(), "")){
                        activeProduct.setQuantity(quantity);
                        mCallback.onAddProduct(activeProduct);
                        dismiss();
                    } else {
                        Toast.makeText(getContext(),"Please Select Pump",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(),"Missing Quantity",Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    public void setListener(AddProductDialogListener callback){
        try{
            mCallback = callback;
        }catch (ClassCastException e){
            throw new ClassCastException(callback.toString() + "Must implement AddProductDialogListener");
        }
    }

    public void setDropDown(){
        ArrayList<String> pumpList = new ArrayList<>();

        Log.d(TAG,"Product Name: " + activeProduct.getName());
        Log.d(TAG,String.format("Station pump info: \n\tPetrol %s \n\tDiesel %s \n\tKerosene %s", activeStation.getNoPetrolPumps(), activeStation.getNoDieselPumps(), activeStation.getNoKerosenePumps()));
        switch (activeProduct.getName()){
            case "Petrol":
                for(int i = 1; i <= Integer.parseInt(activeStation.getNoPetrolPumps()); i++){
                    pumpList.add(String.format("Pump %s", i));
                    Log.d(TAG,"Added to list :" + i );
                }
                break;
            case "Diesel":
                for(int i = 1; i <= Integer.parseInt(activeStation.getNoDieselPumps()); i++){
                    pumpList.add(String.format("Pump %s", i));
                }
                break;
            case "Kerosene":
                for(int i = 1; i <= Integer.parseInt(activeStation.getNoKerosenePumps()); i++){
                    pumpList.add(String.format("Pump %s", i));
                }
                break;
        }

        Log.d(TAG, "Pump list size: " + pumpList.size());
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.list_item, pumpList);
        binding.tvPumpList.setAdapter(stringArrayAdapter);
        binding.tvPumpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_pump = pumpList.get(i);
                Log.d(TAG, "Selected Pump: " + selected_pump);
                activeProduct.setPump_no(selected_pump);
            }
        });

    }

}
