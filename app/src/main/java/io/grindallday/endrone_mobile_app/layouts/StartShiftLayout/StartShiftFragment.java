package io.grindallday.endrone_mobile_app.layouts.StartShiftLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.databinding.FragmentLoginBinding;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeActivity;
import io.grindallday.endrone_mobile_app.model.User;

public class StartShiftFragment extends Fragment {

    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private final String TAG = "LoginFragment";
    private User currentUser = new User();
    SharedPreferences.Editor editor;

    public StartShiftFragment() {
        // Required empty public constructor
    }

    public static StartShiftFragment newInstance() {
        StartShiftFragment fragment = new StartShiftFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //initialize shared preference
        sharedPref = requireActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
        editor = sharedPref.edit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
            NavHostFragment.findNavController(StartShiftFragment.this).navigate(R.id.homeFragment);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.etEmail.getText().toString();
                String password = binding.etPass.getText().toString();
                signIn(email, password);
            }
        });

    }

    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Reload successful!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "reload", task.getException());
                    Toast.makeText(getContext(),
                            "Failed to reload user.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = binding.etEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Required.");
            valid = false;
        } else {
            binding.etEmail.setError(null);
        }

        String password = binding.etPass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            binding.etPass.setError("Required.");
            valid = false;
        } else {
            binding.etPass.setError(null);
        }

        return valid;
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressBar();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            //Set user id for future use
                            firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null){
                                Log.d(TAG, " User Id has been set: " + firebaseUser.getUid());
                                getUser(firebaseUser.getUid());
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressBar();
                    }
                });
    }

    public void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.etPass.setEnabled(false);
        binding.etEmail.setEnabled(false);
    }

    public void hideProgressBar() {
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.etPass.setEnabled(true);
        binding.etEmail.setEnabled(true);
    }

    public void setUserVariables(User user){
        Date date = new Date(System.currentTimeMillis());

        Log.d(TAG, String.format("Retrieved user details: \n\tUser Id: %s \n\tStation Id: %s",user.getUid(),user.getStationId()));
        //Set Preference
        editor.putString("clientId",user.getUid());
        editor.putString("stationId",user.getStationId());
        editor.putLong("loginMills",date.getTime());
        editor.apply();

        startHomeActivity();

    }

    private void startHomeActivity() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
    }

    public void getUser(String userId){
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                User user = new User();
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                        user = new User(
                                documentSnapshot.getString("uid"),
                                documentSnapshot.getString("firstName"),
                                documentSnapshot.getString("secondName"),
                                documentSnapshot.getString("email"),
                                documentSnapshot.getString("date"),
                                documentSnapshot.getString("nrc"),
                                documentSnapshot.getString("stationId"),
                                documentSnapshot.getString("stationName"),
                                documentSnapshot.getString("stationAddress"),
                                documentSnapshot.getString("role"));

                        setUserVariables(user);
                    } else {
                        Log.d(TAG, "No such document");
                    }

                    currentUser = user;
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }
        });
    }


}