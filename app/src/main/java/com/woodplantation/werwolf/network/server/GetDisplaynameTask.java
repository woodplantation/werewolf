package com.woodplantation.werwolf.network.server;

import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.network.NetworkCommand;
import com.woodplantation.werwolf.network.Server;

import java.io.IOException;

/**
 * Created by Sebu on 10.01.2017.
 */

class GetDisplaynameTask extends AsyncTask<Integer,Void,Boolean> {

    private Server server;

    GetDisplaynameTask(Server server) {
        this.server = server;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        Log.d("Server","get Displayname Task, index: " + params[0] + " clientinfo: " + server.getClients().get(params[0]).in);
        server.getTasks().add(this);
        try {
            String line = server.getClients().get(params[0]).in.readLine();
            Log.d("Server","get displayname task read: " + line);
            NetworkCommand command = new NetworkCommand(line);
            server.getClients().get(params[0]).displayname = command.string;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        server.getTasks().remove(this);
        Log.d("Server","get Displayname Task on postexecute. result: " + result);
        if (!result) {
            return;
        }

        server.playerListChanged();
    }
}
