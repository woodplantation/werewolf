package com.woodplantation.werwolf.network;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.woodplantation.werwolf.activities.LobbyActivity;

/**
 * Created by Sebu on 02.11.2016.
 */

public class Server extends Service {

    public class LocalBinder extends Binder {
        public Server getService() {
            // Return this instance of LocalService so clients can call public methods
            return Server.this;
        }
    }
    private final IBinder mBinder = new Server.LocalBinder();

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    private Activity activity;

    @Override
    public void onCreate() {
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }

    @Override
    public void onDestroy() {
        mManager.cancelConnect(mChannel, null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new AsyncTask<Void,Void,Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        createGroup();
                        return null;
                    }
                }.execute();
            }
        }, 1000);

        return mBinder;
    }

    private void createGroup() {
        //wait to get activity
        synchronized (this) {
            try {
                while (activity == null) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mManager.createGroup(mChannel, new CreateGroupActionListener());
    }

    public void setActivity(Activity activity) {
        synchronized (this) {
            this.activity = activity;
            this.notify();
        }
    }

    private class CreateGroupActionListener implements WifiP2pManager.ActionListener {
        @Override
        public void onSuccess() {
            Log.d("Server2Service","successfully created Group");
            mManager.requestGroupInfo(mChannel, new CreateGroupInfoListener());
        }

        @Override
        public void onFailure(int i) {
            Log.d("Server2Service","failed create Group, reason: " + i);
            if (activity instanceof LobbyActivity) {
                ((LobbyActivity) activity).onCreateGroupResult(null);
            }
        }
    }

    private class CreateGroupInfoListener implements WifiP2pManager.GroupInfoListener {

        @Override
        public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
            if (activity instanceof LobbyActivity) {
                ((LobbyActivity) activity).onCreateGroupResult(wifiP2pGroup);
            }
        }
    }
}
