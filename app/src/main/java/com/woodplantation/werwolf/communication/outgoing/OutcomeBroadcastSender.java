package com.woodplantation.werwolf.communication.outgoing;

import android.app.Service;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.woodplantation.werwolf.communication.incoming.OutcomeBroadcastReceiver;

/**
 * Created by Sebu on 03.11.2016.
 */

public abstract class OutcomeBroadcastSender {

    protected LocalBroadcastManager localBroadcastManager;
    protected Service service;

    public OutcomeBroadcastSender(Service service) {
        localBroadcastManager = LocalBroadcastManager.getInstance(service);
        this.service = service;
    }

    public abstract void playerListChanged();

    public void serviceStoppedShowDialogFinishActivity(String error) {
        Log.d("OutcomeBS","service stopped. " + error);
        Intent intent = new Intent(OutcomeBroadcastReceiver.SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY);
        intent.setClass(service, OutcomeBroadcastReceiver.class);
        intent.putExtra(OutcomeBroadcastReceiver.EXTRA_SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY_ERROR, error);
        localBroadcastManager.sendBroadcast(intent);
    }

}
