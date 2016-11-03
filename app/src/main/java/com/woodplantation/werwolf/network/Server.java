package com.woodplantation.werwolf.network;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.woodplantation.werwolf.communication.outgoing.ServerOutcomeBroadcastSender;

/**
 * Created by Sebu on 02.11.2016.
 */

public class Server extends Service {

    //Commands that can be sent as intents
    public static final String COMMAND_KICK_PLAYER = "kick_player";
    public static final String EXTRA_KICK_PLAYER_PLAYER = "kick_player_player";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pBroadcastReceiver receiver;

    private ServerOutcomeBroadcastSender serverOutcomeBroadcastSender;

    private boolean running = false;
    private String mac;

    @Override
    public void onCreate() {
        super.onCreate();
        serverOutcomeBroadcastSender = new ServerOutcomeBroadcastSender(this);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        receiver = new WifiP2pBroadcastReceiver();
        registerReceiver(receiver, mIntentFilter);

        mManager.createGroup(mChannel, new OnCreateGroupListener());
    }

    @Override
    public void onDestroy() {
        Log.d("Server","onDestroy");
        unregisterReceiver(receiver);
        mManager.removeGroup(mChannel, null);
        mManager.cancelConnect(mChannel, null);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == null) {
            serverOutcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler beim Einlesen von Parametern.");
            return START_STICKY;
        }
        Log.d("Server","onHandleIntent. action: " + action);
        switch (action) {
            case COMMAND_KICK_PLAYER: {

                break;
            }
        }
        return START_STICKY;
    }

    private class OnCreateGroupListener implements WifiP2pManager.ActionListener {

        @Override
        public void onSuccess() {

        }

        @Override
        public void onFailure(int i) {
            serverOutcomeBroadcastSender.createLobby(null);
            stopSelf();
        }
    }

    private class WifiP2pBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("ServerService","on receive. action: " + action);

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pInfo wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            Log.d("ServerService","on receive. networkinfo: " + networkInfo);
            Log.d("ServerService","on receive. wifiP2pInfo: " + wifiP2pInfo);
            Log.d("ServerService","on receive. wifiP2pDevice: " + wifiP2pDevice);


            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi P2P is enabled
                    Log.d("ServerService","on receive. wifi p2p is enabled");
                } else {
                    // Wi-Fi P2P is not enabled
                    Log.d("ServerService","on receive. wifi p2p is disabled");
                    serverOutcomeBroadcastSender.createLobby(null);
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                if (mManager != null && running) {
                    mManager.requestPeers(mChannel, peerListListener);
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                if (networkInfo != null && networkInfo.isConnected() && wifiP2pInfo != null && wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner && !running) {
                    if (mac == null) {
                        serverOutcomeBroadcastSender.createLobby(null);
                        stopSelf();
                        return;
                    }
                    running = true;
                    serverOutcomeBroadcastSender.createLobby(mac);
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                if (wifiP2pDevice != null && mac == null) {
                    mac = wifiP2pDevice.deviceAddress;
                }
            }

        }
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            //TODO eventuell wegmachen, falls nur alle verfügbaren angezeigt werden; oder speichern, falls alle in der gruppe angezegit werden
            Log.d("Server","peer list listener. peerlist: " + peerList.getDeviceList());
        }
    };
}