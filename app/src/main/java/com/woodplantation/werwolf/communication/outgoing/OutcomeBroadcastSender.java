package com.woodplantation.werwolf.communication.outgoing;

import android.app.Service;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.woodplantation.werwolf.communication.incoming.OutcomeBroadcastReceiver;

import java.util.ArrayList;

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

    public void playerListChanged(ArrayList<String> list) {
        Log.d("OutcomeBS","playerlist changed. list : " + list);
        Intent intent = new Intent(OutcomeBroadcastReceiver.PLAYER_LIST_CHANGED);
        intent.setClass(service, OutcomeBroadcastReceiver.class);
        intent.putStringArrayListExtra(OutcomeBroadcastReceiver.EXTRA_PLAYER_LIST_CHANGED, list);
        localBroadcastManager.sendBroadcast(intent);
    }

    public void serviceStoppedShowDialogFinishActivity(String error) {
        Log.d("OutcomeBS","service stopped. " + error);
        Intent intent = new Intent(OutcomeBroadcastReceiver.SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY);
        intent.setClass(service, OutcomeBroadcastReceiver.class);
        intent.putExtra(OutcomeBroadcastReceiver.EXTRA_SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY_ERROR, error);
        localBroadcastManager.sendBroadcast(intent);
    }

}
