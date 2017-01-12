package com.woodplantation.werwolf.network.client;

import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.network.Client;
import com.woodplantation.werwolf.network.NetworkCommand;
import com.woodplantation.werwolf.network.NetworkCommandType;

/**
 * Created by Sebu on 10.01.2017.
 */

class SendDisplaynameTask extends AsyncTask<Void, Void, Void> {

    private Client client;

    SendDisplaynameTask(Client client) {
        this.client = client;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        client.getTasks().add(this);

        Log.d("Client", "send displayname task");

        NetworkCommand command = new NetworkCommand();
        command.type = NetworkCommandType.CLIENT_SERVER_DISPLAYNAME;
        command.string = client.getDisplayName();
        client.getOut().println(command.toJsonString());
        Log.d("Client", "writing: " + command.toJsonString());

        client.getTasks().remove(this);
        return null;
    }
}