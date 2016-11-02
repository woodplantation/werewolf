package com.woodplantation.werwolf.network;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by Sebu on 02.11.2016.
 */

public class Client extends Service {

    public static final String COMMAND_START = "start";
    public static final String EXTRA_START_ADDRESS = "extra_" + COMMAND_START + "_address";
    public static final String EXTRA_START_PASSWORD = "extra_" + COMMAND_START + "_password";

    private boolean running = false;
    private boolean firstRun = true;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pBroadcastReceiver receiver;

    private ClientOutcomeCommunication clientOutcomeCommunication;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!running) {
            running = true;
        } else {
            firstRun = false;
        }

        String action = intent.getAction();
        Log.d("Client", "onstartcommand. action" + action);
        if (action == null) {
            clientOutcomeCommunication.serviceStoppedShowDialogFinishActivity("Fehler bei der Übertragung.");
            stopSelf();
            return START_STICKY;
        }

        if (firstRun && !action.equals(COMMAND_START)) {
            clientOutcomeCommunication.serviceStoppedShowDialogFinishActivity("Fehler bei der Ersten Verbindung.");
            stopSelf();
            return START_STICKY;
        } else if (action.equals(COMMAND_START)) {
            clientOutcomeCommunication = new ClientOutcomeCommunication();

            String address = intent.getStringExtra(EXTRA_START_ADDRESS);
            String password = intent.getStringExtra(EXTRA_START_PASSWORD);
            if (address == null || password == null) {
                clientOutcomeCommunication.serviceStoppedShowDialogFinishActivity("Fehler beim Lesen der Paremeter.");
                stopSelf();
                return START_STICKY;
            }

            mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(this, getMainLooper(), null);

            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            receiver = new WifiP2pBroadcastReceiver();
            registerReceiver(receiver, mIntentFilter);

            WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
            wifiP2pConfig.deviceAddress = address;
            mManager.connect(mChannel, wifiP2pConfig, new OnConnectingListener());
            return START_STICKY;
        }
        return START_STICKY;
    }

    private class OnConnectingListener implements WifiP2pManager.ActionListener {

        @Override
        public void onSuccess() {

        }

        @Override
        public void onFailure(int i) {
            Log.d("Client","connecting listener. on failure. reason: " + i);
            clientOutcomeCommunication.serviceStoppedShowDialogFinishActivity("Fehler beim Verbinden: " + i);
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
                    //serverOutcomeCommunication.createLobby(null);
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                if (networkInfo != null && networkInfo.isConnected() && wifiP2pInfo != null && wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                    //mManager.requestGroupInfo(mChannel, new CreateGroupInfoListener());
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
            }

        }
    }

    private class ClientOutcomeCommunication {

        private LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(Client.this);

        private void serviceStoppedShowDialogFinishActivity(String error) {
            Log.d("ServerOutcome","create lobby. " + error);
            Intent intent = new Intent(ClientOutcomeBroadcastReceiver.SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY);
            intent.setClass(Client.this, ClientOutcomeBroadcastReceiver.class);
            intent.putExtra(ClientOutcomeBroadcastReceiver.EXTRA_SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY_ERROR, error);
            localBroadcastManager.sendBroadcast(intent);
        }

        private void playerListChanged() {

        }
    }

}
