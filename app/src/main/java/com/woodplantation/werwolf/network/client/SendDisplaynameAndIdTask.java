package com.woodplantation.werwolf.network.client;

import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.Installation;
import com.woodplantation.werwolf.network.Client;
import com.woodplantation.werwolf.network.NetworkCommand;
import com.woodplantation.werwolf.network.NetworkCommandType;
import com.woodplantation.werwolf.network.objects.DisplaynameAndId;

/**
 * Created by Sebu on 10.01.2017.
 */

class SendDisplaynameAndIdTask extends AsyncTask<Void, Void, Void> {

    private Client client;

    SendDisplaynameAndIdTask(Client client) {
        this.client = client;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        client.getTasks().add(this);

        Log.d("Client", "send displayname task");

        NetworkCommand networkCommand = new NetworkCommand();
        networkCommand.type = NetworkCommandType.CLIENT_SERVER_DISPLAYNAME;
        networkCommand.command = client.getDisplaynameAndId();
        client.getOut().println(networkCommand.toJsonString());
        Log.d("Client", "writing: " + networkCommand.toJsonString());

        client.getTasks().remove(this);
        return null;
    }
}