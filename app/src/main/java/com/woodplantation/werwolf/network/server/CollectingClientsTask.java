package com.woodplantation.werwolf.network.server;

import android.os.AsyncTask;
import android.util.Log;

import com.woodplantation.werwolf.network.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Sebu on 10.01.2017.
 */

public class CollectingClientsTask extends AsyncTask<Void, Void, Socket> {

    private Server server;

    public CollectingClientsTask(Server server) {
        this.server = server;
    }

    @Override
    protected Socket doInBackground(Void... voids) {
        Log.d("Server","collecting clients task");
        server.getTasks().add(this);
        try {
            return server.getServerSocket().accept();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

        @Override
        protected void onPostExecute(Socket result) {
            Log.d("Server","collecting clients task on postexecute");
            server.getTasks().remove(this);
            if (result == null) {
                return;
            }

            //if we started already we dont want new clients
            if (server.isStarted()) {
                try {
                    result.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            //start new task for collecting clients
            CollectingClientsTask collectingClientsTask = new CollectingClientsTask(server);
            collectingClientsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //get Input
            InputStream inputStream;
            OutputStream outputStream;
            try {
                inputStream = result.getInputStream();
                outputStream = result.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            //save client, input and output in list
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter out = new PrintWriter(outputStream, true);
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.socket = result;
            clientInfo.in = in;
            clientInfo.out = out;
            server.getClients().add(clientInfo);

            //start task for getting displayname from client
            GetDisplaynameTask getDisplaynameTask = new GetDisplaynameTask(server);
            getDisplaynameTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, server.getClients().indexOf(clientInfo));
        }
}
