package io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.HomeFragment;
import io.grindallday.endrone_mobile_app.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private HomeFragment homeFragment;

    public ProductAdapter(Context context, HomeFragment homeFragment) {
        this.context = context;
        this.productList = new ArrayList<>();
        this.homeFragment = homeFragment;
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
        holder.price.setText(String.format("ZMK %,.2f", product.getPrice()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, product.getName() + " Selected ", Toast.LENGTH_SHORT).show();
                homeFragment.showProductSaleDialog(product);
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
        }

        for(Product product : newProductList){
            if(Objects.equals(product.getType(), "product")){
                productList.add(product);
            }
        }
        //productList.addAll(newProductList);
        notifyDataSetChanged();
        Log.d("ProductAdapter", "Values Updated");

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView name, description, price;
        public CardView cardView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.materialCardView);
            name = itemView.findViewById(R.id.tv_product_name);
            description = itemView.findViewById(R.id.tv_description);
            price = itemView.findViewById(R.id.tv_price);


        }
    }

}
