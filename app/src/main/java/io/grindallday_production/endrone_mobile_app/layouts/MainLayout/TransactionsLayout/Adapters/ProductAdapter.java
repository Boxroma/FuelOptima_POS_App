package io.grindallday_production.endrone_mobile_app.layouts.MainLayout.TransactionsLayout.Adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.grindallday_production.endrone_mobile_app.R;
import io.grindallday_production.endrone_mobile_app.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductAdapter() {
        this.productList = new ArrayList<>();
        Log.d("ProductAdapter", "Adapter called");
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.trans_product_list_item, parent, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("ZMW %,.2f", product.getPrice()));
        holder.tvPump.setText(product.getPump_no());
        holder.tvQuantity.setText(String.format("%,.2f", product.getQuantity()));
        holder.tvTotal.setText(String.format("ZMW %,.2f", (product.getPrice()*product.getQuantity())));

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

        productList = newProductList;

        //productList.addAll(newProductList);
        notifyDataSetChanged();
        Log.d("ProductAdapter", "Values Updated");

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvPrice, tvPump, tvQuantity, tvTotal;
        public CardView cardView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.materialCardView);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPump = itemView.findViewById(R.id.tv_product_pump);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotal = itemView.findViewById(R.id.tv_total);

        }
    }

}
