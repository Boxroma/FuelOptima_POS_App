package io.grindallday_production.endrone_mobile_app.layouts.StartShiftLayout;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import io.grindallday_production.endrone_mobile_app.R;
import io.grindallday_production.endrone_mobile_app.databinding.ActivityStartShiftBinding;


public class StartShiftActivity extends AppCompatActivity {

    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        io.grindallday_production.endrone_mobile_app.databinding.ActivityStartShiftBinding binding = ActivityStartShiftBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContentView = binding.frameLayout;
        setFullScreen();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, StartShiftFragment.class, null)
                .commit();
    }

    private void setFullScreen (){
        mContentView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

    }



}