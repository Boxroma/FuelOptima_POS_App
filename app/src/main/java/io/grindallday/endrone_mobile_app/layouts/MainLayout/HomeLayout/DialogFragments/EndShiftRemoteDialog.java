package io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;

import java.util.Date;

import io.grindallday.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.EndShiftRemoteDialogListener;

public class EndShiftRemoteDialog extends DialogFragment {

    EndShiftRemoteDialogListener endShiftRemoteDialogListener;

    public static EndShiftRemoteDialog newInstance(){
        return new EndShiftRemoteDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This shift has been ended remotely")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // END SHIFT
                        Date date = new Date();
                        endShiftRemoteDialogListener.onEndShiftRemotely(new Timestamp(date));
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setListener(EndShiftRemoteDialogListener callback){
        try{
            endShiftRemoteDialogListener = callback;
        }catch (ClassCastException e){
            throw new ClassCastException(callback.toString() + "Must implement AddProductDialogListener");
        }
    }
}