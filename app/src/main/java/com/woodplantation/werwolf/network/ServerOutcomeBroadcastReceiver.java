package com.woodplantation.werwolf.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.woodplantation.werwolf.activities.LobbyActivity;

import java.util.ArrayList;

/**
 * Created by Sebu on 02.11.2016.
 */

public class ServerOutcomeBroadcastReceiver extends BroadcastReceiver{

    public static final String LOBBY_CREATE = "lobby_create";
    public static final String EXTRA_LOBBY_CREATE_SUCESS = "extra_" + LOBBY_CREATE + "_sucess";
    public static final String EXTRA_LOBBY_CREATE_PASSWORD = "extra_" + LOBBY_CREATE + "_password";
    public static final String EXTRA_LOBBY_CREATE_ADDRESS = "extra_" + LOBBY_CREATE + "_address";
    public static final String PLAYER_LIST_CHANGED = "player_list_changed";
    public static final String EXTRA_PLAYER_LIST_CHANGED = "extra_" + PLAYER_LIST_CHANGED;

    private Activity activity;

    public ServerOutcomeBroadcastReceiver(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("ServerOutcomeBR","on receive. action: " + action);
        if (action == null) {
            return;
        }
        switch(action) {
            case PLAYER_LIST_CHANGED: {
                if (activity instanceof LobbyActivity) {
                    ArrayList<String> list = intent.getStringArrayListExtra(EXTRA_PLAYER_LIST_CHANGED);
                    if (list != null) {
                        ((LobbyActivity) activity).playerListChanged(list);
                    }
                }
                break;
            }
            case LOBBY_CREATE: {
                if (activity instanceof LobbyActivity) {
                    boolean success = intent.getBooleanExtra(EXTRA_LOBBY_CREATE_SUCESS, false);
                    String address = intent.getStringExtra(EXTRA_LOBBY_CREATE_ADDRESS);
                    String password = intent.getStringExtra(EXTRA_LOBBY_CREATE_PASSWORD);
                    ((LobbyActivity) activity).lobbyCreate(success, address, password);
                }
                break;
            }
        }

    }
}
