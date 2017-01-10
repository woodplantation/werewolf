package com.woodplantation.werwolf.network.server;

import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.woodplantation.werwolf.network.Server;

/**
 * Created by Sebu on 10.01.2017.
 */

public class ServerHelperPeerListListener implements WifiP2pManager.PeerListListener {

    private Server server;

    public ServerHelperPeerListListener(Server server) {
        this.server = server;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        //TODO eventuell wegmachen, falls nur alle verfügbaren angezeigt werden; oder speichern, falls alle in der gruppe angezegit werden
        Log.v("Server", "peer list listener. peerlist: " + peerList.getDeviceList());
    }
}