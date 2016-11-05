package com.woodplantation.werwolf.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private ArrayList<String> displayNames = new ArrayList<>();

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int superReturn = super.onStartCommand(intent, flags, startId, false);
        if (superReturn == START_STICKY) {
            return START_STICKY;
        }

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

    class WifiP2pBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("ServerService","on receive. action: " + action);

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pInfo wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            Log.v("ServerService","on receive. networkinfo: " + networkInfo);
            Log.v("ServerService","on receive. wifiP2pInfo: " + wifiP2pInfo);
            Log.v("ServerService","on receive. wifiP2pDevice: " + wifiP2pDevice);


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
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (mManager != null && !connected) {
                    mManager.requestPeers(mChannel, peerListListener);
                }
                Log.v("Test", "P2P peers changed");
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
                if (peer.deviceAddress.equals(groupOwnerMacAddress)) {
                    config.deviceAddress = peer.deviceAddress;
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                outcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Keine Lobby gefunden.");
                stopSelf();
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
                    Log.v("Test","Connect failed. Retry.");
                }
            });


        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {

        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {
            // InetAddress from WifiP2pInfo struct.
            groupOwnerAddress = info.groupOwnerAddress;
            initSocket();
            //TODO we got IP address here.
        }
    };

    private void initSocket() {
        try {
            //initalizing stuff
            socket = new Socket(groupOwnerAddress, groupOwnerPort);
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //start reading
            ReadingIncomingCommandsTask readingIncomingCommandsTask = new ReadingIncomingCommandsTask();
            readingIncomingCommandsTask.execute();

            //send displayname
            sendDisplaynameTask.execute();
        } catch (IOException e) {
            e.printStackTrace();
            outcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler beim Erstellen der Verbindung");
            stopSelf();
        }
    }

    private AsyncTask<Void,Void,Void> sendDisplaynameTask = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... voids) {
            NetworkCommand command = new NetworkCommand();
            command.type = NetworkCommandType.CLIENT_SERVER_DISPLAYNAME;
            command.string = displayName;
            out.write(command.toJsonString());
            return null;
        }
    };

    //TODO maybe put a class like this into superclass NetworkingService because the same thing needs to be done for Server
    private class ReadingIncomingCommandsTask extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            tasks.add(this);
            try {
                return in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String networkCommand) {
            tasks.remove(this);
            if (networkCommand == null) {
                outcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler beim Einlesen");
                stopSelf();
                return;
            }

            //if we finished already we dont want to handle stuff anymore
            if (finish) {
                outcomeBroadcastSender.serviceStoppedShowDialogFinishActivity("Fehler beim Einlesen: bereits beendet");
                stopSelf();
                return;
            }

            //start new Incoming Command
            ReadingIncomingCommandsTask task = new ReadingIncomingCommandsTask();
            task.execute();

            //handle this command
            NetworkCommand command = new NetworkCommand(networkCommand);

            switch (command.type) {
                //TODO handle command
            }

        }
    }

}
