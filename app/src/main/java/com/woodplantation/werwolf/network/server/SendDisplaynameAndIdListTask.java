package com.woodplantation.werwolf.network.server;

import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.network.NetworkCommand;
import com.woodplantation.werwolf.network.NetworkCommandType;
import com.woodplantation.werwolf.network.Server;
import com.woodplantation.werwolf.network.objects.DisplaynameAndId;
import com.woodplantation.werwolf.network.objects.DisplaynameAndIdList;

import java.util.ArrayList;

/**
 * Created by Sebu on 10.01.2017.
 */

public class SendDisplaynameAndIdListTask extends AsyncTask<DisplaynameAndIdList,Void,Void> {

    private Server server;

    public SendDisplaynameAndIdListTask(Server server) {
        this.server = server;
    }

    @Override
    protected Void doInBackground(DisplaynameAndIdList... params) {
        server.getTasks().add(this);
        Log.d("Server","sendplayerlisttask.");

        NetworkCommand networkCommand = new NetworkCommand();
        networkCommand.type = NetworkCommandType.SERVER_CLIENT_DISPLAYNAMES;
        networkCommand.command = params[0];

        for (ClientInfo client : server.getClients()) {
            Log.d("Server","sending : " + networkCommand.toJsonString());
            client.out.println(networkCommand.toJsonString());
        }

        server.getTasks().remove(this);
        return null;
    }
}