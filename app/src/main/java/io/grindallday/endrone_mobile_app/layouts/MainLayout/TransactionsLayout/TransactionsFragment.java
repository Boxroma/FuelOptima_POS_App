package io.grindallday.endrone_mobile_app.layouts.MainLayout.TransactionsLayout;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.grindallday.endrone_mobile_app.databinding.FragmentTransactionsBinding;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.TransactionsLayout.Adapters.TransactionAdapter;

public class TransactionsFragment extends Fragment {

    private TransactionsViewModel transactionsViewModel;
    TransactionAdapter transactionAdapter;
    FragmentTransactionsBinding binding;
    RecyclerView recyclerView;

    public static TransactionsFragment newInstance() {
        return new TransactionsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentTransactionsBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        //Logic things
        recyclerView = binding.recyclerView;
        transactionAdapter = new TransactionAdapter(recyclerView,getContext());

        transactionsViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(requireActivity().getApplication())).get(TransactionsViewModel.class);

        transactionsViewModel.getSales().observe(getViewLifecycleOwner(), sales -> {
            if(sales!=null){
                //update adapter
                transactionAdapter.setSaleList(sales);
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            //linearLayoutManager.setReverseLayout(true);
            //linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(transactionAdapter);
        });




        return view;
    }



}