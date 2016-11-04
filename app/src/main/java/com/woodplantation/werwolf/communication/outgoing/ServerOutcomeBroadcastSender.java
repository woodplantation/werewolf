package com.woodplantation.werwolf.communication.outgoing;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pGroup;
import android.support.annotation.Nullable;
import android.util.Log;

import com.woodplantation.werwolf.communication.incoming.ServerOutcomeBroadcastReceiver;
import com.woodplantation.werwolf.network.Server;

/**
 * Created by Sebu on 03.11.2016.
 */

public class ServerOutcomeBroadcastSender extends OutcomeBroadcastSender {

    public ServerOutcomeBroadcastSender(Server server) {
        super(server);
    }

    @Override
    public void playerListChanged() {

    }

    /**
     *
     * @param address @Nullable
     */
    public void createLobby(String address, int port) {
        Log.d("ServerOutcome","create lobby. " + address);
        Intent intent = new Intent(ServerOutcomeBroadcastReceiver.LOBBY_CREATE);
        intent.setClass(service, ServerOutcomeBroadcastReceiver.class);
        if (address == null) {
            intent.putExtra(ServerOutcomeBroadcastReceiver.EXTRA_LOBBY_CREATE_SUCESS, false);
        } else {
            intent.putExtra(ServerOutcomeBroadcastReceiver.EXTRA_LOBBY_CREATE_SUCESS, true);
            intent.putExtra(ServerOutcomeBroadcastReceiver.EXTRA_LOBBY_CREATE_ADDRESS, address);
            intent.putExtra(ServerOutcomeBroadcastReceiver.EXTRA_LOBBY_CREATE_PORT, port);
        }
        localBroadcastManager.sendBroadcast(intent);
    }
}
