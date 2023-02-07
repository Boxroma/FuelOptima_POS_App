package io.grindallday.endrone_mobile_app.layouts.MainLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.databinding.ActivityHomeBinding;
import io.grindallday.endrone_mobile_app.layouts.EndShiftLayout.EndShiftActivity;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.EndShiftDialog;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments.EndShiftRemoteDialog;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.HomeViewModel;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.EndShiftDialogListener;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.EndShiftRemoteDialogListener;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.Shift;
import io.grindallday.endrone_mobile_app.model.ShiftStaff;
import io.grindallday.endrone_mobile_app.model.User;
import timber.log.Timber;

public class HomeActivity extends AppCompatActivity implements EndShiftDialogListener, EndShiftRemoteDialogListener {

    private final String TAG = "HomeActivity";
    SharedPreferences.Editor editor;
    private View mContentView;
    private ActivityHomeBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private SharedPreferences preferences;
    private List<Sale> saleList;
    private Shift currentShift;
    private String shiftStaffId;
    private ShiftStaff currentShiftStaff;
    private User currentUser;
    private double currentSales = 0.0;
    private double expectedCash = 0.0;

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        preferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = preferences.edit();

        shiftStaffId = preferences.getString("shiftStaffId", "");

        firebaseFirestore = FirebaseFirestore.getInstance();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeViewModel homeViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(this.getApplication()))
                .get(HomeViewModel.class);

        homeViewModel.getShiftMutableLiveData().observe(this, shift -> {
            if (shift != null) {
                currentShift = shift;
                setShiftDetailsInNavDrawer();
                Timber.tag(TAG).d("Shift Updated");
                if (!Objects.equals(currentShift.getStatus(), "active")){
                    showEndShiftRemoteDialog();
                }
            }
        });

        homeViewModel.getShiftStaffMutableLiveData().observe(this, shiftStaff -> {
            if (shiftStaff != null){
                currentShiftStaff = shiftStaff;
            }
        });

        homeViewModel.getUserMutableLiveData().observe(this, user -> {
            if (user != null){
                currentUser = user;
                Timber.tag(TAG).d("Current User Info: %s", currentUser.getFirstName());
            }
        });

        homeViewModel.getSaleList().observe(this, sales -> {
            if (sales != null) {
                saleList = sales;
                setCurrentSales();
                updateTotal();
                updateShiftInfo(null, true);
                Timber.tag(TAG).d("Sales List Updated");
            }
        });



        mContentView = binding.drawerLayout;
        setFullScreen();

        Toolbar toolbar = findViewById(R.id.toolbar);

        //configureHomeToolBar();
        setSupportActionBar(toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationView navigationView = binding.navView;
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.endShift) {
                showEndShiftDialog();
            } else {
                NavigationUI.onNavDestinationSelected(item, navController);
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;

        });

        // updateShiftInfo(null, true);
    }

    private void setFullScreen() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }

    public void setToolbarText(String text) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(text);
        }
    }

    public void setShiftDetailsInNavDrawer() {
        View header = binding.navView.getHeaderView(0);

        TextView tvShiftName = header.findViewById(R.id.tv_shift_name);
        TextView tvShiftStart = header.findViewById(R.id.tv_shift_start);
        TextView tvShiftEnd = header.findViewById(R.id.tv_shift_end);

        tvShiftName.setText(String.format("%s", currentShift.getName()));
        tvShiftStart.setText(String.format("Start: %s", new SimpleDateFormat("dd/MM/yyyy, k:ma").format(currentShift.getStart().toDate())));
        tvShiftEnd.setText(String.format("End: %s", new SimpleDateFormat("dd/MM/yyyy, k:ma").format(currentShift.getStop().toDate())));

        // updateShiftInfo(null, true);
    }

    public void setCurrentSales() {
        if (saleList != null) {
            expectedCash = 0;
            currentSales = 0;
            for (Sale sale : saleList) {
                if (Objects.equals(sale.getClientType(), "Cash")) {
                    expectedCash = expectedCash + sale.getTotal();
                    Timber.tag(TAG).d("Cash Sale Item Added: %s", sale.getUid());
                }
                currentSales = currentSales + sale.getTotal();
                Timber.tag(TAG).d("Sale Item Added: %s", sale.getUid());
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateTotal() {
        double total = 0.0;
        if (saleList!=null){
            for(Sale sale : saleList){
                total = total + sale.getTotal();
            }
        }
        setToolbarText(String.format("ZMW %,.2f",total));
    }

    @SuppressLint("DefaultLocale")
    public void setNavigationDrawer() {
        View header = binding.navView.getHeaderView(0);

        //Set Variables
        TextView userName = header.findViewById(R.id.tv_user_name);
        TextView position = header.findViewById(R.id.tv_position);
        TextView tvCurrentSales = header.findViewById(R.id.tv_current_total_sales);
        TextView tvExpectedCash = header.findViewById(R.id.tv_current_total_cash);

        if (currentUser != null){
            //Set
            userName.setText(String.format("%s %s", currentUser.getFirstName(), currentUser.getSecondName()));
            position.setText(String.format("%s", currentUser.getRole()));
        }

        //
        tvCurrentSales.setText(String.format("ZMW %,.2f", currentSales));
        tvExpectedCash.setText(String.format("ZMW %,.2f", expectedCash));
        // updateShiftInfo(null, true );

    }

    public void showEndShiftDialog() {
        EndShiftDialog dialogFragment = EndShiftDialog.newInstance();
        dialogFragment.setCancelable(false);
        dialogFragment.setListener(this);
        dialogFragment.show(getSupportFragmentManager(), "End Shift Dialog");
    }

    public void showEndShiftRemoteDialog() {
        EndShiftRemoteDialog dialogFragment = EndShiftRemoteDialog.newInstance();
        dialogFragment.setCancelable(false);
        dialogFragment.setListener(this);
        dialogFragment.show(getSupportFragmentManager(), "End Shift Dialog");
    }

    @Override
    public void onEndShift(Timestamp timestamp) {
        updateShiftInfo(timestamp, false);
        //startEndShiftActivity();
    }

    @Override
    public void onEndShiftRemotely(Timestamp timestamp) {
        updateShiftInfo(timestamp, false);
        //startEndShiftActivity();
    }

    /* Method to check for a matching StaffShiftId and return its index */
    public Pair<Integer, Boolean> getIndex(ArrayList<ShiftStaff> shiftStaffs) {
        boolean state = false;
        int index = 0;
        for (int counter = 0; counter < shiftStaffs.size(); counter++) {
            ShiftStaff shiftStaff = (shiftStaffs.get(counter));
            Timber.tag(TAG).d("Shift ID #1: %s ID#2: %s", shiftStaff.getId(),shiftStaffId);
            if (Objects.equals(shiftStaff.getId(), shiftStaffId)){
                index = counter;
                state = true;
            }
        }
        Timber.tag(TAG).d("Index: %s", index);
        return new Pair<>(index, state);
    }

    public void updateShiftInfo(Timestamp timestamp, boolean active) {

        Timber.tag(TAG).d("Shift Update Called");
        String shiftStaffId = preferences.getString("shiftStaffId", "");

        if (!Objects.equals(shiftStaffId, "")){
            if(currentShift != null) {

                DocumentReference reference = firebaseFirestore.collection("shiftStaff").document(shiftStaffId);

                /*Get latest shift doc*/
                reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Timber.tag(TAG).d("DocumentSnapshot data: %s ", document.getData());
                                try {

                                    Map<String, Object> data = new HashMap<>();
                                    if (!active) {
                                        data.put("active", active);
                                        data.put("endTime", timestamp);
                                    }
                                    data.put("totalSales", currentSales);
                                    data.put("expectedCash", expectedCash);

                                    /* Make Firebase Update Request */
                                    reference.set(data, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Timber.tag(TAG).d("Shift Successfully Updated");
                                                    setNavigationDrawer();
                                                    if (timestamp != null) {
                                                        startEndShiftActivity();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Timber.tag(TAG).w(e);

                                                }
                                            });
                                }catch (Exception e){
                                    Timber.tag(TAG).e(e);
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

            }
        }
    }

    public void startEndShiftActivity() {
        // START THE ACTIVITY!
        Intent intent = new Intent(this, EndShiftActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}