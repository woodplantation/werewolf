package com.woodplantation.werwolf.network.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.woodplantation.werwolf.communication.outgoing.ServerOutcomeBroadcastSender;
import com.woodplantation.werwolf.network.Server;
import com.woodplantation.werwolf.network.WifiP2pBroadcastReceiver;

/**
 * Created by Sebu on 10.01.2017.
 */

public class ServerHelperWifiP2pBroadcastReceiver extends WifiP2pBroadcastReceiver {

    public ServerHelperWifiP2pBroadcastReceiver(Server server) {
        networkingService = server;
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
                ((ServerOutcomeBroadcastSender) networkingService.getOutcomeBroadcastSender()).createLobby(null, 0);
            }
   /*     } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (networkingService.mManager != null && networkingService.connected) {
                networkingService.mManager.requestPeers(networkingService.mChannel, networkingService.peerListListener);
            }*/
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if (networkInfo != null && networkInfo.isConnected() && wifiP2pInfo != null && wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner && !networkingService.isConnected()) {
                if (((Server) networkingService).getMac() == null) {
                    ((ServerOutcomeBroadcastSender) networkingService.getOutcomeBroadcastSender()).createLobby(null, 0);
                    networkingService.stopSelf();
                    return;
                }
                networkingService.setConnected(true);
                ((Server) networkingService).initServerSocket();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            if (wifiP2pDevice != null && ((Server) networkingService).getMac() == null) {
                ((Server) networkingService).setMac(wifiP2pDevice.deviceAddress);
            }
        }

    }
}
