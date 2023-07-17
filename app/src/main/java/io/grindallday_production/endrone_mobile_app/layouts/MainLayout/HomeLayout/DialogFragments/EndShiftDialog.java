package io.grindallday_production.endrone_mobile_app.layouts.MainLayout.HomeLayout.DialogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;

import java.util.Date;

import io.grindallday_production.endrone_mobile_app.layouts.MainLayout.HomeLayout.interfaces.EndShiftDialogListener;

public class EndShiftDialog extends DialogFragment {

    EndShiftDialogListener endShiftDialogListener;

    public static EndShiftDialog newInstance(){
        return new EndShiftDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to end Shift?")
                .setPositiveButton("End Shift", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // END SHIFT
                        Date date = new Date();
                        endShiftDialogListener.onEndShift(new Timestamp(date));
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setListener(EndShiftDialogListener callback){
        try{
            endShiftDialogListener = callback;
        }catch (ClassCastException e){
            throw new ClassCastException(callback.toString() + "Must implement AddProductDialogListener");
        }
    }
}