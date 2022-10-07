package io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments;

import static io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeActivity.getScreenHeight;
import static io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeActivity.getScreenWidth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.databinding.DialogFragmentDisplayCartBinding;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.Adapters.CartItemsAdapter;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.RemoveProductDialogListener;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.ShowCheckoutDialogListener;
import io.grindallday.endrone_mobile_app.model.Product;

public class DisplayCartDialogFragment extends DialogFragment {

    public static String TAG = "DisplayCartDialogFragment";
    public static String product_id;
    public static List<Product> activeProductList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CartItemsAdapter adapter;
    RemoveProductDialogListener mCallback;
    ShowCheckoutDialogListener showCheckoutDialogListener;
    DialogFragmentDisplayCartBinding binding;

    public DisplayCartDialogFragment() {
    }

    public static DisplayCartDialogFragment newInstance(List<Product> productList){
        activeProductList = productList;
        return new DisplayCartDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null){
            product_id = getArguments().getString("product_id");
        }

        binding = DialogFragmentDisplayCartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        Dialog dialog = new Dialog(getContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog);
        dialog.setContentView(view);

        setUi();
        updateUI(activeProductList);

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
        params.height = getScreenHeight() - 20;
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
    public void updateUI(List<Product> productList){

    }

    @SuppressLint("DefaultLocale")
    public void setUi(){

        adapter = new CartItemsAdapter(getContext(),activeProductList, DisplayCartDialogFragment.this);
        recyclerView = binding.rvCartItems;

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        binding.btCheckout.setOnClickListener(view -> {
            Toast.makeText(getContext(),"Checkout button Pressed",Toast.LENGTH_SHORT).show();
            if(activeProductList!=null){
                showCheckoutDialogListener.showCheckoutDialog();
            }
            dismiss();
        });
        setCartTotal();

    }

    public void setShowCheckoutDialogListener(ShowCheckoutDialogListener checkoutDialogListener){
        try {
            showCheckoutDialogListener = checkoutDialogListener;
        }catch (ClassCastException e){
            throw new ClassCastException(checkoutDialogListener.toString() + " Must Implement ShowCheckoutDialogListener");
        }
    }

    public void setListener(RemoveProductDialogListener callback){
        try{
            mCallback = callback;
        }catch (ClassCastException e){
            throw new ClassCastException(callback.toString() + "Must implement AddProductDialogListener");
        }
    }

    public void removeProduct(Product product){
        activeProductList.remove(product);
        adapter = new CartItemsAdapter(getContext(),activeProductList, DisplayCartDialogFragment.this);
        recyclerView.setAdapter(adapter);
        mCallback.onRemoveProduct(product);
        setCartTotal();
    }

    @SuppressLint("DefaultLocale")
    public void setCartTotal(){
        double total = 0.00;
        if (activeProductList != null){
            if (activeProductList.size()!=0) {
                for (Product product : activeProductList) {
                    total = total + (product.getQuantity() * product.getPrice());
                }
                binding.tvCartTotal.setText(String.format("ZMK %,.2f", total));
                binding.btCheckout.setEnabled(true);
            }
            else {
                binding.tvCartTotal.setText("No Items in cart");
                binding.btCheckout.setEnabled(false);
            }
        } else {
            binding.tvCartTotal.setText("No Items in cart");
            binding.btCheckout.setEnabled(false);
        }

    }

}
