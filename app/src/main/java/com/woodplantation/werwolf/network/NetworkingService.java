package com.woodplantation.werwolf.network;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.woodplantation.werwolf.Notification;
import com.woodplantation.werwolf.communication.outgoing.ClientOutcomeBroadcastSender;
import com.woodplantation.werwolf.communication.outgoing.OutcomeBroadcastSender;
import com.woodplantation.werwolf.communication.outgoing.ServerOutcomeBroadcastSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Sebu on 04.11.2016.
 */

public abstract class NetworkingService extends Service {

    //Commands that can be sent as intents
    public static final String COMMAND_INITIALIZE = "initialize";
    public static final String EXTRA_INITIALIZE_DISPLAYNAME = "extra_" + COMMAND_INITIALIZE + "_displayname";

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver receiver;

    WifiP2pManager.PeerListListener peerListListener;

    boolean running = false;
    boolean firstRun = true;
    boolean connected = false;
    boolean finish = false;
    String action;

    OutcomeBroadcastSender outcomeBroadcastSender;

    String displayName;

    //List of all tasks, that this service executes. should all be cancelled when service stops
    ArrayList<AsyncTask> tasks = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.v("Networking","onDestroy");
        finish = true;
        for (AsyncTask task : tasks) {
            task.cancel(true);
        }
        unregisterReceiver(receiver);
        mManager.cancelConnect(mChannel, null);
        mManager.removeGroup(mChannel, null);
        Notification.deleteNotification(this);
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId, boolean server) {
        super.onStartCommand(intent, flags, startId);
        if (!running) {
            Notification.createNotification(this);

            running = true;

            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            registerReceiver(receiver, mIntentFilter);

            mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(this, getMainLooper(), null);
        } else {
            firstRun = false;
        }

        if (intent == null) {
            Log.d("NetworkingService","onstartcommand intent is null. flags: " + flags);
            return START_STICKY;
        }
        action = intent.getAction();
        Log.v("NetworkingService", "onstartcommand. action" + action);
        if (action == null) {
            outcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler bei der Übertragung.");
            stopSelf();
            return START_STICKY;
        }

        if (firstRun && !action.equals(COMMAND_INITIALIZE)) {
            outcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler bei der Ersten Verbindung.");
            stopSelf();
            return START_STICKY;
        }

        if (action.equals(COMMAND_INITIALIZE)) {
            displayName = intent.getStringExtra(EXTRA_INITIALIZE_DISPLAYNAME);
            if (displayName == null) {
                outcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler beim Einlesen des Anzeigenamens.");
                stopSelf();
                return START_STICKY;
            }
        }
        return -1;
    }

    public OutcomeBroadcastSender getOutcomeBroadcastSender() {
        return outcomeBroadcastSender;
    }

    public void setOutcomeBroadcastSender(OutcomeBroadcastSender outcomeBroadcastSender) {
        this.outcomeBroadcastSender = outcomeBroadcastSender;
    }

    public WifiP2pManager getmManager() {
        return mManager;
    }

    public void setmManager(WifiP2pManager mManager) {
        this.mManager = mManager;
    }

    public WifiP2pManager.Channel getmChannel() {
        return mChannel;
    }

    public void setmChannel(WifiP2pManager.Channel mChannel) {
        this.mChannel = mChannel;
    }

    public ArrayList<AsyncTask> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<AsyncTask> tasks) {
        this.tasks = tasks;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
