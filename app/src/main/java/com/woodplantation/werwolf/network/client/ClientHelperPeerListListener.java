package com.woodplantation.werwolf.network.client;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.woodplantation.werwolf.network.Client;

/**
 * Created by Sebu on 10.01.2017.
 */

public class ClientHelperPeerListListener implements WifiP2pManager.PeerListListener {

    private Client client;

    public ClientHelperPeerListListener(Client client) {
        this.client = client;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        boolean flag = false;
        WifiP2pConfig config = new WifiP2pConfig();

        for (WifiP2pDevice peer : peerList.getDeviceList()) {
            if (peer.deviceAddress.equals(client.getGroupOwnerMacAddress())) {
                config.deviceAddress = peer.deviceAddress;
                flag = true;
                break;
            }
        }
        if (!flag) {
            client.getOutcomeBroadcastSender().serviceStoppedShowDialogFinishActivity("Keine Lobby gefunden.");
            client.stopSelf();
            return;
        }

        config.wps.setup = WpsInfo.PBC;

        client.getmManager().connect(client.getmChannel(), config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Log.v("ClientPeerListListener", "Connect failed. Retry.");
            }
        });
    }
}
