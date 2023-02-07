package io.grindallday.endrone_mobile_app.layouts.EndShiftLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.databinding.FragmentEndShiftBinding;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.TransactionsLayout.Adapters.TransactionAdapter;
import io.grindallday.endrone_mobile_app.layouts.StartShiftLayout.StartShiftActivity;
import io.grindallday.endrone_mobile_app.model.Sale;

public class EndShiftFragment extends Fragment {

    private FragmentEndShiftBinding binding;
    private EndShiftViewModel viewModel;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public static EndShiftFragment newInstance() {
        return new EndShiftFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Sign User Out
        mAuth.signOut();
        Toast.makeText(getContext(),
                "Your shift has been successfully ended",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentEndShiftBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sharedPref = requireActivity().getApplication().getSharedPreferences("pref", Context.MODE_PRIVATE);

        //Logic Things
        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(requireActivity().getApplication()))
                .get(EndShiftViewModel.class);

        viewModel.getSales().observe(getViewLifecycleOwner(), sales -> {
            if(sales!=null){
                setUi(sales);
            }
        });

        return view;
    }

    @SuppressLint("DefaultLocale")
    private void setUi(List<Sale> sales) {
        double totalSales = 0.0;
        double expectedCash = 0.0;
        Date startTime = new Date(sharedPref.getLong("loginMills",0));
        Date endTime = new Date();
        int numberOfSales = 0;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm");


        for(Sale sale : sales){
            if(Objects.equals(sale.getClientType(), "Cash")){
                expectedCash = expectedCash + sale.getTotal();
            }
            totalSales = totalSales + sale.getTotal();
            numberOfSales ++;
        }

        binding.tvSellCount.setText(String.valueOf(numberOfSales));
        binding.tvTotalSales.setText(String.format("ZMW %,.2f",totalSales));
        binding.tvStartTime.setText(dateFormat.format(startTime));
        binding.tvEndTime.setText(dateFormat.format(endTime));
        binding.tvExpectedCash.setText(String.format("ZMW %,.2f",expectedCash));


        //Set Recycler View
        RecyclerView recyclerView = binding.rvSaleHistory;
        TransactionAdapter transactionAdapter = new TransactionAdapter(recyclerView,getContext());
        transactionAdapter.setSaleList(sales);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(transactionAdapter);

        binding.btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate to Start Shift Activity
                Intent intent = new Intent(getContext(), StartShiftActivity.class);
                startActivity(intent);
            }
        });

    }

    public void clearShiftData () {
            /*Clear Shift Data in Shared Pref*/
            editor.putString("shiftId", "");
            editor.putString("shiftName", "");
            editor.putString("shiftStart", "");
            editor.putString("shiftStop", "");
            editor.putString("shiftStaffId","");
            editor.apply();

    }

}