package com.woodplantation.werwolf.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by Sebu on 10.01.2017.
 */

public abstract class WifiP2pBroadcastReceiver extends BroadcastReceiver {

    protected NetworkInfo networkInfo;
    protected WifiP2pDevice wifiP2pDevice;
    protected WifiP2pInfo wifiP2pInfo;
    protected String action;

    protected NetworkingService networkingService;

    @Override
    public void onReceive(Context context, Intent intent) {
        action = intent.getAction();
        Log.v("Wifip2pBR","on receive. action: " + action);

        networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
        wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        Log.v("Wifip2pBR","on receive. networkinfo: " + networkInfo);
        Log.v("Wifip2pBR","on receive. wifiP2pInfo: " + wifiP2pInfo);
        Log.v("Wifip2pBR","on receive. wifiP2pDevice: " + wifiP2pDevice);

        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (networkingService.mManager != null && !networkingService.connected) {
                networkingService.mManager.requestPeers(networkingService.mChannel, networkingService.peerListListener);
            }
        }
    }
}
