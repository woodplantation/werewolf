package com.woodplantation.werwolf.network.client;

import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.network.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Sebu on 10.01.2017.
 */

public class ClientHelperInitSocketTask extends AsyncTask<Void, Void, Boolean> {

    private Client client;

    public ClientHelperInitSocketTask(Client client) {
        this.client = client;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            //initalizing stuff
            client.setSocket(new Socket(client.getGroupOwnerAddress(), client.getGroupOwnerPort()));
            client.setOut(new PrintWriter(client.getSocket().getOutputStream(), true));
            client.setIn(new BufferedReader(new InputStreamReader(client.getSocket().getInputStream())));

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d("Client", "initializing socket. on post execute. restul: " + result);
        if (!result) {
            client.getOutcomeBroadcastSender().serviceStoppedShowDialogFinishActivity("Fehler beim Erstellen der Verbindung");
            client.stopSelf();
        } else {
            //start reading
            ClientHelperReadingIncomingCommandsTask readingIncomingCommandsTask = new ClientHelperReadingIncomingCommandsTask(client);
            readingIncomingCommandsTask.executeOnExecutor(THREAD_POOL_EXECUTOR);

            //send displayname
            try {
                //TODO build in that server send command for client to send displayname
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ClientHelperSendDisplaynameTask clientHelperSendDisplaynameTask = new ClientHelperSendDisplaynameTask(client);
            clientHelperSendDisplaynameTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
        }
    }
}