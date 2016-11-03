package com.woodplantation.werwolf.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.woodplantation.werwolf.R;

/**
 * Created by Sebu on 02.11.2016.
 */

public class ClientOutcomeBroadcastReceiver extends BroadcastReceiver {

    public static final String SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY = "service_stopped";
    public static final String EXTRA_SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY_ERROR = "extra_" + SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY + "_error";

    private Activity activity;

    public ClientOutcomeBroadcastReceiver(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d("ClientOutcomeBR","on receive. action: " + action);
        if (action == null) {
            return;
        }
        switch(action) {
            case SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY: {
                String error = intent.getStringExtra(EXTRA_SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY_ERROR);
                if (error == null) {
                    error = "";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);
                builder.setTitle(R.string.service_stopped_title);
                builder.setMessage(activity.getString(R.string.service_stopped_text, error));
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.finish();
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                break;
            }
        }

    }
}
