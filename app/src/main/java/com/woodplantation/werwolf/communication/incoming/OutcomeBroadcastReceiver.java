package com.woodplantation.werwolf.communication.incoming;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.woodplantation.werwolf.R;
import com.woodplantation.werwolf.activities.LobbyActivity;

import java.util.ArrayList;

/**
 * Created by Sebu on 03.11.2016.
 */

public abstract class OutcomeBroadcastReceiver extends BroadcastReceiver {

    public static final String PLAYER_LIST_CHANGED = "player_list_changed";
    public static final String EXTRA_PLAYER_LIST_CHANGED = "extra_" + PLAYER_LIST_CHANGED;
    public static final String SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY = "service_stopped";
    public static final String EXTRA_SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY_ERROR = "extra_" + SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY + "_error";

    protected Activity activity;

    public OutcomeBroadcastReceiver(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d("ClientOutcomeBR","on receive. action: " + action);
        if (action == null) {
            return;
        }
        switch (action) {
            case PLAYER_LIST_CHANGED: {
                if (activity instanceof LobbyActivity) {
                    ArrayList<String> list = intent.getStringArrayListExtra(EXTRA_PLAYER_LIST_CHANGED);
                    if (list != null) {
                        ((LobbyActivity) activity).playerListChanged(list);
                    }
                }
                break;
            }
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
