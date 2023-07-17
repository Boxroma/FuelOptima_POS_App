package io.grindallday_production.endrone_mobile_app.layouts.MainLayout.HomeLayout.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import io.grindallday_production.endrone_mobile_app.R;
import io.grindallday_production.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.DisplayCartDialogFragment;
import io.grindallday_production.endrone_mobile_app.model.Product;
import timber.log.Timber;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;
    public static String TAG = "CartItemsAdapter";
    public DisplayCartDialogFragment fragment;

    public CartItemsAdapter(Context context, List<Product> productList,DisplayCartDialogFragment fragment) {
        this.context = context;
        this.productList = productList;
        this.fragment = fragment;
        if(productList != null){
            Timber.tag(TAG).d("Adapter received %s products",productList.size());
        }

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.quantity.setText(String.format("%,.2f",product.getQuantity()));
        holder.price.setText(String.format("ZMK %,.2f", product.getPrice()));
        holder.total.setText(String.format("ZMK %,.2f", (product.getPrice()*product.getQuantity())));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(context, product.getName() + " Selected ", Toast.LENGTH_SHORT).show();
                Timber.tag(TAG).d("%s Selected ", product.getName());
            }
        });

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(context, product.getName() + " Remove", Toast.LENGTH_SHORT).show();                  Timber.tag(TAG).d("%s Selected ", product.getName());
                Timber.tag(TAG).d("%s Remove ", product.getName());
                fragment.removeProduct(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (productList != null){
            return productList.size();
        } else {
            return 0;
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setProductList(List<Product> newProductList){
        if(productList != null){
            productList.clear();

            for(Product product : newProductList){
                if(Objects.equals(product.getType(), "product")){
                    productList.add(product);
                }
            }
            //productList.addAll(newProductList);
            notifyDataSetChanged();
            Log.d("ProductAdapter", "Values Updated");
        } else {

            for(Product product : newProductList){
                if(Objects.equals(product.getType(), "product")){
                    productList.add(product);
                }
            }
            // productList = newProductList;
            Log.d("ProductAdapter", "Values Created" + productList);
        }
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView name, quantity, price, total;
        public CardView cardView;
        public ImageButton imageButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.materialCardView);
            name = itemView.findViewById(R.id.tv_product_name);
            quantity = itemView.findViewById(R.id.tv_quantity);
            price = itemView.findViewById(R.id.tv_price);
            total = itemView.findViewById(R.id.tv_total);
            imageButton = itemView.findViewById(R.id.bt_remove);



        }
    }

}
