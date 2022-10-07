package io.grindallday.endrone_mobile_app.layouts.EndShiftLayout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.grindallday.endrone_mobile_app.R;

public class EndShiftFragment extends Fragment {

    private EndShiftViewModel mViewModel;

    public static EndShiftFragment newInstance() {
        return new EndShiftFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_end_shift, container, false);
    }

}