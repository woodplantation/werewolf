package com.woodplantation.werwolf.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.communication.outgoing.ServerOutcomeBroadcastSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebu on 02.11.2016.
 */

public class Server extends NetworkingService {

    private class ClientInfo {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String displayname;
    }

    //Commands that can be sent as intents
    public static final String COMMAND_KICK_PLAYER = "kick_player";
    public static final String EXTRA_KICK_PLAYER_PLAYER = "kick_player_player";
    public static final String COMMAND_START = "start";

    private String mac;

    private ServerSocket serverSocket;
    private ArrayList<ClientInfo> clients = new ArrayList<>();

    private boolean started = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int superReturn = super.onStartCommand(intent, flags, startId, true);
        if (superReturn == START_STICKY) {
            return START_STICKY;
        }

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
                    ((ServerOutcomeBroadcastSender) outcomeBroadcastSender).createLobby(null, 0);
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                if (mManager != null && connected) {
                    mManager.requestPeers(mChannel, peerListListener);
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                if (networkInfo != null && networkInfo.isConnected() && wifiP2pInfo != null && wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner && !connected) {
                    if (mac == null) {
                        ((ServerOutcomeBroadcastSender) outcomeBroadcastSender).createLobby(null, 0);
                        stopSelf();
                        return;
                    }
                    connected = true;
                    initServerSocket();
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                if (wifiP2pDevice != null && mac == null) {
                    mac = wifiP2pDevice.deviceAddress;
                }
            }

        }
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {

        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            //TODO eventuell wegmachen, falls nur alle verfügbaren angezeigt werden; oder speichern, falls alle in der gruppe angezegit werden
            Log.v("Server","peer list listener. peerlist: " + peerList.getDeviceList());
        }
    };

    private void initServerSocket() {
        try {
            serverSocket = new ServerSocket(0);
            ((ServerOutcomeBroadcastSender) outcomeBroadcastSender).createLobby(mac, serverSocket.getLocalPort());

            CollectingClientsTask collectingClientsTask = new CollectingClientsTask();
            collectingClientsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class CollectingClientsTask extends AsyncTask<Void, Void, Socket> {
        @Override
        protected Socket doInBackground(Void... voids) {
            Log.d("Server","collecting clients task");
            tasks.add(this);
            try {
                return serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Socket result) {
            Log.d("Server","collecting clients task on postexecute");
            tasks.remove(this);
            if (result == null) {
                return;
            }

            //if we started already we dont want new clients
            if (started) {
                try {
                    result.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            //start new task for collecting clients
            CollectingClientsTask collectingClientsTask = new CollectingClientsTask();
            collectingClientsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //get Input
            InputStream inputStream;
            OutputStream outputStream;
            try {
                inputStream = result.getInputStream();
                outputStream = result.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            //save client, input and output in list
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter out = new PrintWriter(outputStream, true);
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.socket = result;
            clientInfo.in = in;
            clientInfo.out = out;
            clients.add(clientInfo);

            //start task for getting displayname from client
            GetDisplaynameTask getDisplaynameTask = new GetDisplaynameTask();
            getDisplaynameTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, clients.indexOf(clientInfo));
        }
    };

    private class GetDisplaynameTask extends AsyncTask<Integer,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            Log.d("Server","get Displayname Task, index: " + params[0] + " clientinfo: " + clients.get(params[0]).in);
            tasks.add(this);
            try {
                String line = clients.get(params[0]).in.readLine();
                Log.d("Server","get displayname task read: " + line);
                NetworkCommand command = new NetworkCommand(line);
                clients.get(params[0]).displayname = command.string;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            tasks.remove(this);
            Log.d("Server","get Displayname Task on postexecute. result: " + result);
            if (!result) {
                return;
            }
            //TODO send notification to all clients that player list changed.

            //send communication to activity that we got new playerlist.
            playerListChanged();
        }
    }

    private void playerListChanged() {
        ArrayList<String> list = new ArrayList<>();
        list.add(displayName);
        for (ClientInfo client : clients) {
            if (client.displayname != null) {
                list.add(client.displayname);
            }
        }
        outcomeBroadcastSender.playerListChanged(list);
        SendPlayerListTask task = new SendPlayerListTask();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list);
    }

    private class SendPlayerListTask extends AsyncTask<ArrayList<String>,Void,Void> {

        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            tasks.add(this);

            NetworkCommand command = new NetworkCommand();
            command.type = NetworkCommandType.SERVER_CLIENT_DISPLAYNAMES;
            command.string = params[0].toString();
            for (ClientInfo client : clients) {
                Log.d("Server","sending : " + command.toJsonString());
                client.out.println(command.toJsonString());
            }

            tasks.remove(this);
            return null;
        }
    }
}