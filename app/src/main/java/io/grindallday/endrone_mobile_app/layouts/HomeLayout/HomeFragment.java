package io.grindallday.endrone_mobile_app.layouts.HomeLayout;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;
import java.util.Observable;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.databinding.FragmentHomeBinding;
import io.grindallday.endrone_mobile_app.databinding.FragmentLoginBinding;
import io.grindallday.endrone_mobile_app.layouts.LoginLayout.LoginFragment;
import io.grindallday.endrone_mobile_app.model.Product;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
    private FirebaseAuth mAuth;
    public RecyclerView recyclerView;
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            //reload();
            NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_loginFragment);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container, false);
        View view = binding.getRoot();

        recyclerView = binding.recyclerView;

        productAdapter = new ProductAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(productAdapter);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.getProductList().observe(getViewLifecycleOwner(), productList -> {

            if (productList != null){
                productAdapter.setProductList(productList);
            } else {
                Log.d("HomeFragment", "Waiting For info");
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Some Dumb Stuff

    }

    private void productListObserver(){
        homeViewModel.getProductList().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                recyclerView.setAdapter(productAdapter);
                productAdapter.setProductList(products);
                productList = products;
                Log.d("HomeFragment", "Onchange" + products);
            }
        });
    }

}