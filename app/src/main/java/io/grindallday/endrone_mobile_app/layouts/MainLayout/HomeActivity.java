package io.grindallday.endrone_mobile_app.layouts.MainLayout;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;

import java.util.List;
import java.util.Objects;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.databinding.ActivityFullscreenBinding;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.User;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private View mContentView;
    private ActivityFullscreenBinding binding;
    private String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContentView = binding.drawerLayout;
        setFullScreen();

        toolbar = findViewById(R.id.toolbar);

        //configureHomeToolBar();
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationView navigationView = binding.navView;
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


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
                if(Objects.equals(sale.getClientType(), "WalkInClient")){
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

}