package io.grindallday.endrone_mobile_app.layouts.StartShiftLayout;

import static com.google.firebase.firestore.Query.Direction.DESCENDING;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import io.grindallday.endrone_mobile_app.databinding.FragmentLoginBinding;
import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeActivity;
import io.grindallday.endrone_mobile_app.model.Shift;
import io.grindallday.endrone_mobile_app.model.ShiftStaff;
import io.grindallday.endrone_mobile_app.model.User;
import timber.log.Timber;

public class StartShiftFragment extends Fragment {

    private final String TAG = "LoginFragment";
    SharedPreferences.Editor editor;
    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private User currentUser = new User();
    private final Shift lastShift = null;
    private String shiftId;

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
        sharedPref = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //reload();
            //Toast.makeText(getContext(),"User Already Logged In", Toast.LENGTH_SHORT).show();
            //NavHostFragment.findNavController(StartShiftFragment.this).navigate(R.id.homeFragment);
            startHomeActivity();
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
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
                    Timber.tag(TAG).e(task.getException(), "reload");
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
        Timber.tag(TAG).d("signIn: %s", email);
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
                            Timber.tag(TAG).d("signInWithEmail:success");

                            //Set user id for future use
                            firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                Timber.tag(TAG).d(" User Id has been set: %s", firebaseUser.getUid());
                                getUser(firebaseUser.getUid());

                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.tag(TAG).w(task.getException(), "signInWithEmail:failure");
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideProgressBar();
                        }

                        if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // hideProgressBar();
                    }
                });
    }

    public void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.etPass.setEnabled(false);
        binding.etEmail.setEnabled(false);
        binding.button.setEnabled(false);
    }

    public void hideProgressBar() {
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.etPass.setEnabled(true);
        binding.etEmail.setEnabled(true);
        binding.button.setEnabled(true);
    }

    public void setUserVariables(User user) {
        Date date = new Date(System.currentTimeMillis());


        Timber.tag(TAG).d("Retrieved user details: \n\tUser Id: %s \n\tStation Id: %s", user.getUid(), user.getStationId());

        //Set Preference
        editor.putString("userId", user.getUid());
        editor.putString("userName", String.format("%s %s", user.getFirstName(), user.getSecondName()));
        editor.putString("stationId", user.getStationId());
        editor.putLong("loginMills", date.getTime());
        editor.apply();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
    }

    public void getUser(String userId) {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = new User();
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Timber.tag(TAG).d("DocumentSnapshot data: %s", documentSnapshot.getData());
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
                        // getShiftData(user.getStationId());
                        getShiftInfo();

                    } else {
                        Timber.tag(TAG).d("No such document");
                        mAuth.signOut();
                        hideProgressBar();
                    }
                    currentUser = user;
                } else {
                    Timber.tag(TAG).d(task.getException(), "get failed with ");
                    mAuth.signOut();
                    hideProgressBar();
                }
            }
        });
    }

    public void getShiftInfo() {
        CollectionReference collectionReference = firebaseFirestore.collection("shifts");
        Query query = collectionReference.whereEqualTo("stationId", sharedPref.getString("stationId", "")).orderBy("start", DESCENDING).limit(1);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Shift> shifts = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Timber.tag(TAG).d("Shift Data => %s", document.getData());
                    Timber.tag(TAG).d("Shift ID => %s", document.getId());
                    Timber.tag(TAG).d("Shift Status => %s", document.getString("status"));
                    shifts.add(new Shift(
                            document.getId(),
                            document.getString("name"),
                            document.getTimestamp("start"),
                            document.getTimestamp("stop"),
                            document.getString("stationId"),
                            document.getString("status")
                    ));
                }
                if (Objects.equals(shifts.get(0).getStatus(), "active")) {
                    Timber.tag(TAG).d("Returned Shift ID: %s", shifts.get(0).getId());
                    editor.putString("shiftId", shifts.get(0).getId());
                    editor.apply();
                    // hideProgressBar();
                    Toast.makeText(getContext(), "Active shift found", Toast.LENGTH_SHORT).show();
                    // mAuth.signOut();
                    // startHomeActivity();
                    checkStaffList();
                } else {
                    mAuth.signOut();
                    hideProgressBar();
                    Toast.makeText(getContext(), "No active shift", Toast.LENGTH_SHORT).show();
                }
            } else {
                Timber.tag(TAG).d(task.getException(), "Failed to get shift");
                mAuth.signOut();
                hideProgressBar();
            }
        });
    }

    /*If shift is active we check to see if the staff member already has an active shift object*/
    public void checkStaffList() {
        CollectionReference collectionReference = firebaseFirestore.collection("shiftStaff");
        Query query = collectionReference.whereEqualTo("shiftId", sharedPref.getString("shiftId", ""));

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                QuerySnapshot documentSnapshot = task.getResult();
                boolean create = true;
                if (documentSnapshot.size() != 0) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        // Check if user already has a shift session
                        if (Objects.equals(document.getString("userId"), sharedPref.getString("userId", ""))) {

                            // Check if session has already been reconciled
                            if (Boolean.FALSE.equals(document.getBoolean("reconciled"))){
                                /* make shift active*/
                                DocumentReference documentReference = firebaseFirestore.collection("shiftStaff").document(document.getId());

                                documentReference.update("active", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        editor.putString("shiftStaffId", document.getId());
                                        editor.apply();
                                        hideProgressBar();
                                        startHomeActivity();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error Starting Shift", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        hideProgressBar();
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Staff has already been reconciled by manager.", Toast.LENGTH_LONG).show();
                                Timber.tag(TAG).w("Staff member has been reconciled by manager");
                                mAuth.signOut();
                                hideProgressBar();
                            }
                            create = false;
                            break;
                        }
                    }
                }

                if (create) {
                    String shiftStaffId = UUID.randomUUID().toString();
                    Timber.tag(TAG).d("Generated ShiftStaffID: %s", shiftStaffId);
                    editor.putString("shiftStaffId", shiftStaffId);
                    editor.apply();

                    /*Create new shift object*/
                    collectionReference.document(shiftStaffId).set(
                                    new ShiftStaff(
                                            shiftStaffId,
                                            sharedPref.getString("shiftId", ""),
                                            currentUser.getStationId(),
                                            currentUser.getUid(),
                                            currentUser.getFirstName() + " " + currentUser.getSecondName(),
                                            new Timestamp(new Date()),
                                            null,
                                            true,
                                            0.0,
                                            0.0
                                    )
                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Timber.tag(TAG).d("DocumentSnapshot successfully written!");
                                    startHomeActivity();
                                    hideProgressBar();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Timber.tag(TAG).w(e);
                                    Toast.makeText(getContext(), "Failure Creating Staff Entry in Shift", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                    hideProgressBar();
                                }
                            });
                }

            } else {
                Toast.makeText(getContext(), "Error Reading Staff List", Toast.LENGTH_SHORT).show();
                Timber.tag(TAG).w("Error Reading Staff List");
                mAuth.signOut();
                hideProgressBar();
            }
        });

    }
}