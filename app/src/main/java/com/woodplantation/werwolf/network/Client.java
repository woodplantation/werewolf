package com.woodplantation.werwolf.network;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.woodplantation.werwolf.communication.outgoing.ClientOutcomeBroadcastSender;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Sebu on 02.11.2016.
 */

public class Client extends Service {

    //Commands that can be sent as intents
    public static final String COMMAND_START = "start";
    public static final String EXTRA_START_ADDRESS = "extra_" + COMMAND_START + "_address";

    private boolean running = false;
    private boolean firstRun = true;
    private boolean connected = false;

    private String groupOwnerMacAddress;
    private InetAddress groupOwnerAddress;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pBroadcastReceiver receiver;

    private ClientOutcomeBroadcastSender clientOutcomeBroadcastSender;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("Client","onDestroy");
        unregisterReceiver(receiver);
        mManager.cancelConnect(mChannel, null);
        mManager.removeGroup(mChannel, null);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!running) {
            running = true;
            clientOutcomeBroadcastSender = new ClientOutcomeBroadcastSender(this);
        } else {
            firstRun = false;
        }

        String action = intent.getAction();
        Log.d("Client", "onstartcommand. action" + action);
        if (action == null) {
            clientOutcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler bei der Übertragung.");
            stopSelf();
            return START_STICKY;
        }

        if (firstRun && !action.equals(COMMAND_START)) {
            clientOutcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler bei der Ersten Verbindung.");
            stopSelf();
            return START_STICKY;
        } else if (action.equals(COMMAND_START)) {
            clientOutcomeBroadcastSender = new ClientOutcomeBroadcastSender(this);

            groupOwnerMacAddress = intent.getStringExtra(EXTRA_START_ADDRESS);
            if (groupOwnerMacAddress == null) {
                clientOutcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler beim Lesen der Paremeter.");
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

            mManager.discoverPeers(mChannel, new OnDiscoveringListener());
            return START_STICKY;
        }
        return START_STICKY;
    }

    private class OnDiscoveringListener implements WifiP2pManager.ActionListener {

        @Override
        public void onSuccess() {
            // Code for when the discovery initiation is successful goes here.
            // No services have actually been discovered yet, so this method
            // can often be left blank.  Code for peer discovery goes in the
            // onReceive method, detailed below.
        }

        @Override
        public void onFailure(int i) {
            Log.d("Client","discovering listener. on failure. reason: " + i);
            clientOutcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler beim Suchen: " + i);
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
                // Request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (mManager != null && !connected) {
                    mManager.requestPeers(mChannel, peerListListener);
                }
                Log.d("Test", "P2P peers changed");
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                if (mManager == null) {
                    return;
                }
                if (networkInfo.isConnected() && !connected) {
                    connected = true;
                    // We are connected with the other device, request connection
                    // info to find group owner IP
                    mManager.requestConnectionInfo(mChannel, connectionInfoListener);
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
            }

        }
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            boolean flag = false;
            WifiP2pConfig config = new WifiP2pConfig();

            for (WifiP2pDevice peer : peerList.getDeviceList()) {
                Log.d("testclient","going peers. peer: " + peer);
                if (peer.deviceAddress.equals(groupOwnerMacAddress)) {
                    config.deviceAddress = peer.deviceAddress;
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                Log.d("testclient","flag false. returning");
                clientOutcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Keine Lobby gefunden.");
                return;
            }

            config.wps.setup = WpsInfo.PBC;

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("Test","Connect failed. Retry.");
                }
            });


        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {
            // InetAddress from WifiP2pInfo struct.
            groupOwnerAddress = info.groupOwnerAddress;
            //TODO we got IP address here.

            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner) {
                Log.d("Test","onconnectioninfoavailable. WIR SIND SERVER");
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a server thread and accepting
                // incoming connections.
            } else if (info.groupFormed) {
                Log.d("Test","onconnectioninfoavailable. WIR SIND CLIENT");
                // The other device acts as the client. In this case,
                // you'll want to create a client thread that connects to the group
                // owner.
            }
        }
    };

}
