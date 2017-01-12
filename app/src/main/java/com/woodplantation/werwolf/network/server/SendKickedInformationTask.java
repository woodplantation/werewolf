package com.woodplantation.werwolf.network.server;

import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.network.NetworkCommand;
import com.woodplantation.werwolf.network.NetworkCommandType;
import com.woodplantation.werwolf.network.Server;
import com.woodplantation.werwolf.network.objects.KickedInformation;

/**
 * Created by Sebu on 12.01.2017.
 */

public class SendKickedInformationTask extends AsyncTask<KickedInformation, Void, Void> {

    private Server server;
    private ClientInfo clientInfo;

    public SendKickedInformationTask(Server server, ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    @Override
    protected Void doInBackground(KickedInformation... params) {
        server.getTasks().add(this);

        NetworkCommand networkCommand = new NetworkCommand();
        networkCommand.type = NetworkCommandType.SERVER_CLIENT_KICK;
        networkCommand.command = params[0];

        Log.d("Server","sending : " + networkCommand.toJsonString());
        clientInfo.out.println(networkCommand.toJsonString());

        server.getTasks().remove(this);
        return null;
    }
}
