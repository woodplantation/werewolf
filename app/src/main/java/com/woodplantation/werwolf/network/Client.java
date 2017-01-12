package com.woodplantation.werwolf.network;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.communication.outgoing.ClientOutcomeBroadcastSender;
import com.woodplantation.werwolf.network.client.InitSocketTask;
import com.woodplantation.werwolf.network.client.PeerListListener;
import com.woodplantation.werwolf.network.client.WifiP2pBroadcastReceiver;
import com.woodplantation.werwolf.network.objects.DisplaynameAndId;
import com.woodplantation.werwolf.network.objects.DisplaynameAndIdList;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Sebu on 02.11.2016.
 */

public class Client extends NetworkingService {

    //Commands/extras that can be sent via intents
    public static final String EXTRA_INITIALIZE_ADDRESS = "extra_" + COMMAND_INITIALIZE + "_address";
    public static final String EXTRA_INITIALIZE_PORT = "extra_" + COMMAND_INITIALIZE + "_port";

    private String groupOwnerMacAddress;
    private int groupOwnerPort;
    private InetAddress groupOwnerAddress;

    private DisplaynameAndIdList displaynameAndIdList = new DisplaynameAndIdList();

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //start with initializing important stuff before calling super
        if (!running) {
            outcomeBroadcastSender = new ClientOutcomeBroadcastSender(this);
            receiver = new WifiP2pBroadcastReceiver(this);
            peerListListener = new PeerListListener(this);
        }

        //call super now after important stuff initialized
        int superReturn = super.onStartCommand(intent, flags, startId, false);
        if (superReturn == START_STICKY) {
            return START_STICKY;
        }

        //if super call didnt satisfy us, we continue to do stuff
        switch(action) {
            case COMMAND_INITIALIZE: {
                groupOwnerMacAddress = intent.getStringExtra(EXTRA_INITIALIZE_ADDRESS);
                groupOwnerPort = intent.getIntExtra(EXTRA_INITIALIZE_PORT, -1);
                if (groupOwnerMacAddress == null || groupOwnerPort < 1) {
                    outcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler beim Lesen der Paremeter.");
                    stopSelf();
                    break;
                }

                mManager.discoverPeers(mChannel, new OnDiscoveringListener());
                break;
            }
        }
        return START_STICKY;
    }

    private class OnDiscoveringListener implements WifiP2pManager.ActionListener {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onFailure(int i) {
            outcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler beim Suchen: " + i);
            stopSelf();
        }
    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {

        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {
            Log.d("Client","connection info listener on connection info available");
            // InetAddress from WifiP2pInfo struct.
            groupOwnerAddress = info.groupOwnerAddress;
            InitSocketTask initSocketTask = new InitSocketTask(Client.this);
            initSocketTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    public String getGroupOwnerMacAddress() {
        return groupOwnerMacAddress;
    }

    public void setGroupOwnerMacAddress(String groupOwnerMacAddress) {
        this.groupOwnerMacAddress = groupOwnerMacAddress;
    }

    public int getGroupOwnerPort() {
        return groupOwnerPort;
    }

    public void setGroupOwnerPort(int groupOwnerPort) {
        this.groupOwnerPort = groupOwnerPort;
    }

    public InetAddress getGroupOwnerAddress() {
        return groupOwnerAddress;
    }

    public void setGroupOwnerAddress(InetAddress groupOwnerAddress) {
        this.groupOwnerAddress = groupOwnerAddress;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public DisplaynameAndIdList getDisplaynameAndIdList() {
        return displaynameAndIdList;
    }

    public void setDisplaynameAndIdList(DisplaynameAndIdList displaynameAndIdList) {
        this.displaynameAndIdList = displaynameAndIdList;
    }
}
