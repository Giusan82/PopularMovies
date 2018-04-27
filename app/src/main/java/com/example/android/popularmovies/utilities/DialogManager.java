package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.android.popularmovies.R;

public class DialogManager {
    private Context mContext;
    public final AlertDialogAction mAction;
    private int mDialogId;

    public interface AlertDialogAction {
        void negativeAction(int dialog_id);

        void positiveAction(int dialog_id);
    }

    //Constructor
    public DialogManager(Context context, int dialog_id, AlertDialogAction dialog_action) {
        this.mContext = context;
        this.mDialogId = dialog_id;
        this.mAction = dialog_action;
    }

    //build an alert dialog message
    public void showMessage(int icon, String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setIcon(icon);
        builder.setMessage(message);
        builder.setNegativeButton(mContext.getString(R.string.close_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAction.negativeAction(mDialogId);
            }
        });
        builder.setPositiveButton(mContext.getString(R.string.refresh_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAction.positiveAction(mDialogId);
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
