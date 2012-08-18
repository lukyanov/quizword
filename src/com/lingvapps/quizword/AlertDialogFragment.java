package com.lingvapps.quizword;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

// TODO:
// http://blog.blundell-apps.com/tut-generic-fragment-dialog/

public class AlertDialogFragment extends DialogFragment implements OnClickListener {

    public static AlertDialogFragment newInstance(int title, int message) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        int message = getArguments().getInt("message");

        return new AlertDialog.Builder(getActivity())
                //.setIcon(R.drawable.alert_dialog_icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, this)
                .create();
    }
    
    public void onClick(DialogInterface dialog, int which) {
        
    }
}