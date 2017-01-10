package com.woodplantation.werwolf.network;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;

import com.woodplantation.werwolf.communication.outgoing.ServerOutcomeBroadcastSender;
import com.woodplantation.werwolf.network.server.ServerHelperClientInfo;
import com.woodplantation.werwolf.network.server.ServerHelperCollectingClientsTask;
import com.woodplantation.werwolf.network.server.ServerHelperPeerListListener;
import com.woodplantation.werwolf.network.server.ServerHelperSendPlayerListTask;
import com.woodplantation.werwolf.network.server.ServerHelperWifiP2pBroadcastReceiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by Sebu on 02.11.2016.
 */

public class Server extends NetworkingService {

    //Commands that can be sent as intents
    public static final String COMMAND_KICK_PLAYER = "kick_player";
    public static final String EXTRA_KICK_PLAYER_PLAYER = "kick_player_player";
    public static final String COMMAND_START = "start";

    private String mac;

    private ServerSocket serverSocket;
    private ArrayList<ServerHelperClientInfo> clients = new ArrayList<>();

    private boolean started = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //start with initializing important stuff before calling super
        if (!running) {
            outcomeBroadcastSender = new ServerOutcomeBroadcastSender(this);
            receiver = new ServerHelperWifiP2pBroadcastReceiver(this);
            peerListListener = new ServerHelperPeerListListener(this);
        }

        //call super now after important stuff initialized
        int superReturn = super.onStartCommand(intent, flags, startId, true);
        if (superReturn == START_STICKY) {
            return START_STICKY;
        }

        //if super call didnt satisfy us, we continue to do stuff
        switch (action) {
            case COMMAND_INITIALIZE: {
                mManager.createGroup(mChannel, new OnCreateGroupListener());
                break;
            }
            case COMMAND_START: {
                started = true;
            }
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
            ((ServerOutcomeBroadcastSender) outcomeBroadcastSender).createLobby(null, 0);
            stopSelf();
        }
    }

    public void initServerSocket() {
        try {
            serverSocket = new ServerSocket(0);
            ((ServerOutcomeBroadcastSender) outcomeBroadcastSender).createLobby(mac, serverSocket.getLocalPort());

            ServerHelperCollectingClientsTask serverHelperCollectingClientsTask = new ServerHelperCollectingClientsTask(this);
            serverHelperCollectingClientsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void playerListChanged() {
        ArrayList<String> list = new ArrayList<>();
        list.add(displayName);
        for (ServerHelperClientInfo client : clients) {
            if (client.displayname != null) {
                list.add(client.displayname);
            }
        }
        outcomeBroadcastSender.playerListChanged(list);
        ServerHelperSendPlayerListTask task = new ServerHelperSendPlayerListTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list);
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ArrayList<ServerHelperClientInfo> getClients() {
        return clients;
    }

    public void setClients(ArrayList<ServerHelperClientInfo> clients) {
        this.clients = clients;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}