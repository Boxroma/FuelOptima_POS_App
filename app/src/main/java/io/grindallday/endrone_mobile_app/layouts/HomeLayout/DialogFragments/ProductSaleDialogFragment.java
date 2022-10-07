package io.grindallday.endrone_mobile_app.layouts.HomeLayout.DialogFragments;

import static io.grindallday.endrone_mobile_app.layouts.MainActivity.getScreenWidth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import io.grindallday.endrone_mobile_app.databinding.DialogFragmentProductSaleBinding;
import io.grindallday.endrone_mobile_app.layouts.HomeLayout.interfaces.AddProductDialogListener;
import io.grindallday.endrone_mobile_app.model.Product;

public class ProductSaleDialogFragment extends DialogFragment {

    public static String TAG = "ProductSaleDialogFragment";
    public static String product_id;
    public static Product activeProduct = new Product();
    private static Double quantity;
    AddProductDialogListener mCallback;
    DialogFragmentProductSaleBinding binding;

    public ProductSaleDialogFragment() {
    }

    public static ProductSaleDialogFragment newInstance(Product product){
        activeProduct = product;
        quantity = 1.0;
        return new ProductSaleDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null){
            product_id = getArguments().getString("product_id");
        }

        binding = DialogFragmentProductSaleBinding.inflate(getLayoutInflater());
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

    @SuppressLint("DefaultLocale")
    public void setUi(){
        if(activeProduct != null) {

            binding.npQuantity.setMinValue(1);
            binding.npQuantity.setMaxValue(100);

            binding.etCost.setText(String.format("ZMK %,.2f", binding.npQuantity.getValue() * activeProduct.getPrice()));

            binding.npQuantity.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    quantity = (double) i1;
                    Log.d(TAG,"int value = " + i);
                    Log.d(TAG, "int1 value = " + i1);
                    binding.etCost.setText(String.format("ZMK %,.2f", quantity * activeProduct.getPrice()));
                }
            });

            binding.btAdd.setOnClickListener(view -> {
                Toast.makeText(getContext(),"Add button Pressed",Toast.LENGTH_SHORT).show();
                Product product = activeProduct;
                product.setQuantity(quantity);
                //
                mCallback.onAddProduct(product);
                dismiss();
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

}
