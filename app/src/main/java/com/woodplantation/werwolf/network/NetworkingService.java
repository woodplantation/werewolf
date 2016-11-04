package com.woodplantation.werwolf.network;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.woodplantation.werwolf.communication.outgoing.ClientOutcomeBroadcastSender;
import com.woodplantation.werwolf.communication.outgoing.OutcomeBroadcastSender;
import com.woodplantation.werwolf.communication.outgoing.ServerOutcomeBroadcastSender;


/**
 * Created by Sebu on 04.11.2016.
 */

public abstract class NetworkingService extends Service {

    //Commands that can be sent as intents
    public static final String COMMAND_INITIALIZE = "initialize";
    public static final String EXTRA_INITIALIZE_DISPLAYNAME = "extra_" + COMMAND_INITIALIZE + "_displayname";

    protected WifiP2pManager mManager;
    protected WifiP2pManager.Channel mChannel;
    protected BroadcastReceiver receiver;

    protected boolean running = false;
    protected boolean firstRun = true;
    protected boolean connected = false;
    protected String action;

    protected OutcomeBroadcastSender outcomeBroadcastSender;

    protected String displayName;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.v("Networking","onDestroy");
        unregisterReceiver(receiver);
        mManager.cancelConnect(mChannel, null);
        mManager.removeGroup(mChannel, null);
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId, boolean server) {
        super.onStartCommand(intent, flags, startId);
        if (!running) {
            if (server) {
                outcomeBroadcastSender = new ServerOutcomeBroadcastSender((Server) this);
                receiver = ((Server) this).new WifiP2pBroadcastReceiver();
            } else {
                outcomeBroadcastSender = new ClientOutcomeBroadcastSender((Client) this);
                receiver = ((Client) this).new WifiP2pBroadcastReceiver();
            }
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

        action = intent.getAction();
        Log.v("Client", "onstartcommand. action" + action);
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


}
