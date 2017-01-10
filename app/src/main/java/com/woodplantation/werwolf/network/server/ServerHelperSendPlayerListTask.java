package com.woodplantation.werwolf.network.server;

import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.network.NetworkCommand;
import com.woodplantation.werwolf.network.NetworkCommandType;
import com.woodplantation.werwolf.network.Server;

import java.util.ArrayList;

/**
 * Created by Sebu on 10.01.2017.
 */

public class ServerHelperSendPlayerListTask extends AsyncTask<ArrayList<String>,Void,Void> {

    private Server server;

    public ServerHelperSendPlayerListTask(Server server) {
        this.server = server;
    }

    @Override
    protected Void doInBackground(ArrayList<String>... params) {
        server.getTasks().add(this);
        Log.d("Server","sendplayerlisttask.");

        NetworkCommand command = new NetworkCommand();
        command.type = NetworkCommandType.SERVER_CLIENT_DISPLAYNAMES;
        command.string = params[0].toString();
        for (ServerHelperClientInfo client : server.getClients()) {
            Log.d("Server","sending : " + command.toJsonString());
            client.out.println(command.toJsonString());
        }

        server.getTasks().remove(this);
        return null;
    }
}