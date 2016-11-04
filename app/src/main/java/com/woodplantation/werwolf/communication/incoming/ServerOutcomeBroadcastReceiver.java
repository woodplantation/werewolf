package com.woodplantation.werwolf.communication.incoming;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.woodplantation.werwolf.activities.LobbyActivity;

/**
 * Created by Sebu on 02.11.2016.
 */

public class ServerOutcomeBroadcastReceiver extends OutcomeBroadcastReceiver{

    public static final String LOBBY_CREATE = "lobby_create";
    public static final String EXTRA_LOBBY_CREATE_SUCESS = "extra_" + LOBBY_CREATE + "_sucess";
    public static final String EXTRA_LOBBY_CREATE_ADDRESS = "extra_" + LOBBY_CREATE + "_address";
    public static final String EXTRA_LOBBY_CREATE_PORT = "extra_" + LOBBY_CREATE + "_port";

    public ServerOutcomeBroadcastReceiver(Activity activity) {
        super(activity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("ServerOutcomeBR","on receive. action: " + action);
        if (action == null) {
            return;
        }
        switch(action) {
            case LOBBY_CREATE: {
                if (activity instanceof LobbyActivity) {
                    boolean success = intent.getBooleanExtra(EXTRA_LOBBY_CREATE_SUCESS, false);
                    String address = intent.getStringExtra(EXTRA_LOBBY_CREATE_ADDRESS);
                    int port = intent.getIntExtra(EXTRA_LOBBY_CREATE_PORT, -1);
                    ((LobbyActivity) activity).lobbyCreate(success, address, port);
                }
                break;
            }
            default:
                super.onReceive(context, intent);
        }

    }
}
