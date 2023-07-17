package io.grindallday_production.endrone_mobile_app.layouts.MainLayout.TransactionsLayout.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.grindallday_production.endrone_mobile_app.R;
import io.grindallday_production.endrone_mobile_app.model.Sale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private static final String TAG = "TransactionAdapter";
    private final RecyclerView recyclerView;
    private final Context context;

    private static final int UNSELECTED = -1;
    private ArrayList<Sale> saleArrayList;

    private int selectedItem = UNSELECTED;

    public TransactionAdapter(RecyclerView recyclerView, Context context){
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expandable_sale_item, parent, false);

        return new ViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        Sale sale = saleArrayList.get(position);

        holder.bind();

        //Set Adapter
        Log.d(TAG, "Product Count " + sale.getProductList().size());
        ProductAdapter productAdapter = new ProductAdapter();
        productAdapter.setProductList(sale.getProductList());
        RecyclerView expandableRecyclerView = holder.expandableRecyclerView;
        expandableRecyclerView.setLayoutManager( new LinearLayoutManager(context));
        expandableRecyclerView.setAdapter(productAdapter);

        //Fixed view
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm");
        holder.timeStamp.setText(dateFormat.format(sale.getTime().toDate()));
        holder.amount.setText(String.format("%,.2f",sale.getTotal()));
        holder.saleId.setText(sale.getUid());

        //Expandable View
        holder.tvExpandSaleId.setText(sale.getUid());
        holder.tvExpandClientName.setText(sale.getClientName());
        holder.tvExpandClientType.setText(sale.getClientType());

    }

    @Override
    public int getItemCount() {
        if (saleArrayList != null){
            return saleArrayList.size();
        }
        else {
            return 0;
        }
    }

    public void setSaleList(List<Sale> sales) {
        if (saleArrayList!= null){
            saleArrayList.clear();
        }

        Log.d(TAG, "updated sale count = "+ sales.size());
        saleArrayList = (ArrayList<Sale>) sales;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {

        private final ExpandableLayout expandableLayout;
        private final LinearLayout fixedLayout;
        private final TextView timeStamp;
        private final TextView amount;
        private final TextView saleId;
        private TextView tvExpandClientName, tvExpandSaleId, tvExpandClientType;
        private final RecyclerView expandableRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            expandableLayout = itemView.findViewById(R.id.expandable_layout);
            expandableLayout.setInterpolator(new OvershootInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);

            fixedLayout = itemView.findViewById(R.id.ll_fixed_view);
            fixedLayout.setOnClickListener(this);

            expandableRecyclerView = itemView.findViewById(R.id.expand_recyclerView);

            timeStamp = itemView.findViewById(R.id.tv_timestamp);
            amount = itemView.findViewById(R.id.tv_amount);
            saleId = itemView.findViewById(R.id.tv_sale_id);

            tvExpandClientName = itemView.findViewById(R.id.tv_expand_client_name);
            tvExpandSaleId = itemView.findViewById(R.id.tv_expand_sale_id);
            tvExpandClientType = itemView.findViewById(R.id.tv_expand_client_type);
        }

        public void bind(){
            int position = getAdapterPosition();
            boolean isSelected = position == selectedItem;

            expandableLayout.setSelected(isSelected);
            expandableLayout.setExpanded(isSelected, false);
        }

        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
                holder.expandableLayout.setSelected(false);
                holder.expandableLayout.collapse();
            }

            int position = getAdapterPosition();
            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                expandableLayout.setSelected(true);
                expandableLayout.expand();
                selectedItem = position;
            }
        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            Log.d("ExpandableLayout", "State: " + state);
            if (state == ExpandableLayout.State.EXPANDING) {
                recyclerView.smoothScrollToPosition(getAdapterPosition());
            }
        }
    }
}
