package com.woodplantation.werwolf.network.client;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.network.Client;

/**
 * Created by Sebu on 10.01.2017.
 */

public class ConnectionInfoListener implements WifiP2pManager.ConnectionInfoListener {

    private Client client;

    public ConnectionInfoListener(Client client) {
        this.client = client;
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        Log.d("Client", "connection info listener on connection info available");
        // InetAddress from WifiP2pInfo struct.
        client.setGroupOwnerAddress(info.groupOwnerAddress);
        InitSocketTask initSocketTask = new InitSocketTask(client);
        initSocketTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}