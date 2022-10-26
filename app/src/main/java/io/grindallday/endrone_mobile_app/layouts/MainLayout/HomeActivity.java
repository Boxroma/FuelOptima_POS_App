package io.grindallday.endrone_mobile_app.layouts.MainLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.TargetOrBuilder;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.databinding.ActivityHomeBinding;
import io.grindallday.endrone_mobile_app.databinding.DialogFragmentFuelSaleBinding;
import io.grindallday.endrone_mobile_app.layouts.EndShiftLayout.EndShiftActivity;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.EndShiftDialogListener;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.Shift;
import io.grindallday.endrone_mobile_app.model.User;
import io.grindallday.endrone_mobile_app.repository.FireStoreRepository;
import timber.log.Timber;

public class HomeActivity extends AppCompatActivity implements EndShiftDialogListener {

    private View mContentView;
    private ActivityHomeBinding binding;
    private final String TAG = "HomeActivity";
    private FirebaseFirestore firebaseFirestore;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        preferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.endShift) {
                    showEndShiftDialog();
                } else {
                    NavigationUI.onNavDestinationSelected(item, navController);
                }
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            }
        });


    }

    private void setFullScreen (){
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void setToolbarText(String text){
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(text);
        }
    }

    @SuppressLint("DefaultLocale")
    public void setNavigationDrawer(User user, List<Sale> saleList){
        View header = binding.navView.getHeaderView(0);

        double currentSales = 0.0;
        double expectedCash = 0.0;

        if (saleList != null ){
            for (Sale sale : saleList){
                if(Objects.equals(sale.getClientType(), "Cash")){
                    expectedCash = expectedCash + sale.getTotal();
                    Log.d(TAG, "Cash Sale Item Added: " + sale.getUid());
                }
                currentSales = currentSales + sale.getTotal();
                Log.d(TAG, "Sale Item Added: " + sale.getUid());
            }
        }

        //Set Variables
        TextView userName = header.findViewById(R.id.tv_user_name);
        TextView position = header.findViewById(R.id.tv_position);
        TextView tvCurrentSales = header.findViewById(R.id.tv_current_total_sales);
        TextView tvExpectedCash = header.findViewById(R.id.tv_current_total_cash);

        //Set
        userName.setText(String.format("%s %s", user.getFirstName(),user.getSecondName()));
        position.setText("Fuel Attendant");
        //
        tvCurrentSales.setText(String.format("ZMW %,.2f", currentSales));
        tvExpectedCash.setText(String.format("ZMW %,.2f", expectedCash));

    }

    public void showEndShiftDialog(){
        EndShiftDialog dialogFragment = EndShiftDialog.newInstance();
        dialogFragment.setListener(this);
        dialogFragment.show(getSupportFragmentManager(),"End Shift Dialog");
    }

    @Override
    public void onEndShift(Timestamp timestamp) {
        updateShiftInfo(timestamp, false);
    }

    public void updateShiftInfo(Timestamp timestamp, boolean active){

        Shift currentShift = new Shift(
                preferences.getString("shiftId",""), //shiftId,
                preferences.getString("stationId",""), // stationId,
                preferences.getString("clientId",""), //userId,
                "",
                new Timestamp(new Date(preferences.getLong("loginMills",0))),
                timestamp,
                active
        );

        Timber.tag(TAG).d("Shift stationId: %s",currentShift.getStation_id());
        Timber.tag(TAG).d("Shift userId: %s",currentShift.getUser_id());
        Timber.tag(TAG).d("Shift active status: %s",currentShift.isActive());

        firebaseFirestore.collection("shifts").document(preferences.getString("shiftId",""))
                .set(currentShift)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Timber.tag(TAG).d("Shift Successfully Updated");
                        if (timestamp != null){
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
    }

    public void startEndShiftActivity(){
        // START THE ACTIVITY!
        Intent intent = new Intent(this, EndShiftActivity.class);
        startActivity(intent);
    }



}