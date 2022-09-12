package io.grindallday.endrone_mobile_app.layouts.HomeLayout;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductAdapter() {
        this.productList = new ArrayList<>();
        Log.d("ProductAdapter", "Adapter called");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.description.setText(product.getDescription());
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
            productList.addAll(newProductList);
            notifyDataSetChanged();
            Log.d("ProductAdapter", "Values Updated");
        } else {
            productList = newProductList;
            Log.d("ProductAdapter", "Values Created" + productList);
        }
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView name, description;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_product_name);
            description = itemView.findViewById(R.id.tv_description);

        }
    }

}
