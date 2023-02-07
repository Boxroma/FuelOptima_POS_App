package io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments;

import static io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeActivity.getScreenWidth;

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
import java.util.List;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.databinding.DialogFragmentFuelSaleBinding;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.AddProductDialogListener;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.model.Pump;
import io.grindallday.endrone_mobile_app.model.Station;
import timber.log.Timber;

public class FuelSaleDialogFragment extends DialogFragment {

    public static String TAG = "FuelSaleDialogFragment";
    public static String product_id;
    public static String selected_pump;
    public static Product activeProduct = new Product();
    public static Station activeStation = new Station();
    public static List<Pump> pumps = new ArrayList<>();
    private Double quantity;
    private Double amount;
    AddProductDialogListener mCallback;
    DialogFragmentFuelSaleBinding binding;
    boolean focused = false;

    public FuelSaleDialogFragment() {
    }

    public static FuelSaleDialogFragment newInstance(Product product, Station station, List<Pump> pumpList){
        activeProduct = product;
        activeStation = station;
        pumps = pumpList;
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
                // Over Engineered to avoid the static reference from the active Product object
                Product product = new Product(
                        activeProduct.getProduct_id(),
                        activeProduct.getName(),
                        activeProduct.getDescription(),
                        activeProduct.getPrice(),
                        activeProduct.getType(),
                        activeProduct.getStation_id(),
                        activeProduct.getPump_no(),
                        activeProduct.getQuantity(),
                        activeProduct.isActive()
                );
                if (quantity != null){
                    if(!Objects.equals(product.getPump_no(), "")){
                        product.setQuantity(quantity);
                        mCallback.onAddProduct(product);
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

        // Timber.tag(TAG).d(String.format("Station pump info: \n\tPetrol %s \n\tDiesel %s \n\tKerosene %s", activeStation.getPetrolPumpIds().size(), activeStation.getDieselPumpIds().size(), activeStation.getKerosenePumpIds().size()));
        switch (activeProduct.getName()){
            case "Petrol":
                for(Pump pump: pumps){
                    if (Objects.equals(pump.getType(), "petrol")){
                        pumpList.add(String.format("%s", pump.getName()));
                        Timber.tag(TAG).d(pump.getName(), "Added to list :%s");
                    }
                }
                break;
            case "Diesel":
                for(Pump pump: pumps){
                    if (Objects.equals(pump.getType(), "diesel")){
                        pumpList.add(String.format("%s", pump.getName()));
                        Timber.tag(TAG).d(pump.getName(), "Added to list :%s");
                    }
                }
                break;
            case "Kerosene":
                for(Pump pump: pumps){
                    if (Objects.equals(pump.getType(), "kerosene")){
                        pumpList.add(String.format("%s", pump.getName()));
                        Timber.tag(TAG).d(pump.getName(), "Added to list :%s");
                    }
                }
                break;
        }

        Timber.tag(TAG).d("Pump list size: %s", pumpList.size());

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.list_item, pumpList);
        binding.tvPumpList.setAdapter(stringArrayAdapter);
        binding.tvPumpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_pump = pumpList.get(i);
                Timber.tag(TAG).d("Selected Pump: %s", selected_pump);
                activeProduct.setPump_no(selected_pump);
            }
        });

    }

}
