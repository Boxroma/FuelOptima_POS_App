package io.grindallday.endrone_mobile_app.liveData;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import io.grindallday.endrone_mobile_app.model.Client;
import io.grindallday.endrone_mobile_app.model.User;

public class UserLiveData extends LiveData<User> implements EventListener<DocumentSnapshot> {

    private final String TAG = "UserLiveData";
    private DocumentReference documentReference;
    private User user;
    public MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    private ListenerRegistration listenerRegistration = () ->{};

    public UserLiveData(DocumentReference documentReference){
        this.documentReference = documentReference;
    }

    @Override
    protected void onActive() {
        listenerRegistration = documentReference.addSnapshotListener(this);
        super.onActive();
    }

    @Override
    protected void onInactive() {
        listenerRegistration.remove();
        super.onInactive();
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
        if(documentSnapshot != null && documentSnapshot.exists()){
            Log.d(TAG, "document contents: " + documentSnapshot.getData());
        }
    }
}
