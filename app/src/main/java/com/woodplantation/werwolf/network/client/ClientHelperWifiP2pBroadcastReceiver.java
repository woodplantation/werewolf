package com.woodplantation.werwolf.network.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.woodplantation.werwolf.network.Client;
import com.woodplantation.werwolf.network.WifiP2pBroadcastReceiver;

/**
 * Created by Sebu on 10.01.2017.
 */

public class ClientHelperWifiP2pBroadcastReceiver extends WifiP2pBroadcastReceiver {

    public ClientHelperWifiP2pBroadcastReceiver(Client client) {
        networkingService = client;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                Log.v("ServerService","on receive. wifi p2p is enabled");
            } else {
                // Wi-Fi P2P is not enabled
                Log.v("ServerService","on receive. wifi p2p is disabled");
                //serverOutcomeCommunication.createLobby(null);
            }
       /* } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (networkingService.mManager != null && !networkingService.connected) {
                networkingService.mManager.requestPeers(networkingService.mChannel, networkingService.peerListListener);
            }
            Log.v("Test", "P2P peers changed");*/
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (networkingService.getmManager() == null) {
                return;
            }
            Log.d("Client","on receive. connection changed action, connected: " + networkingService.isConnected());
            if (networkInfo.isConnected() && !networkingService.isConnected()) {
                networkingService.setConnected(true);
                // We are connected with the other device, request connection
                // info to find group owner IP
                ConnectionInfoListener connectionInfoListener = new ConnectionInfoListener((Client) networkingService);
                networkingService.getmManager().requestConnectionInfo(networkingService.getmChannel(), connectionInfoListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }

    }
}