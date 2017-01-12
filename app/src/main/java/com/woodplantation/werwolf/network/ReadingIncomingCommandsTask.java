package com.woodplantation.werwolf.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Sebu on 10.01.2017.
 */

public abstract class ReadingIncomingCommandsTask  extends AsyncTask<Void,Void,String> {

    protected NetworkingService networkingService;
    protected BufferedReader in;

    public ReadingIncomingCommandsTask(NetworkingService networkingService, BufferedReader in) {
        this.networkingService = networkingService;
        this.in = in;
    }

    @Override
    protected String doInBackground(Void... voids) {
        Log.d("Client","Reading incoming commands.");
        networkingService.tasks.add(this);
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String networkCommand) {
        Log.d("Client","reading incoming command string: " + networkCommand);

        //if we finished already we dont want to handle stuff anymore
        if (networkingService.finish) {
            return;
        }

        //start new Incoming Command
        selfRestart();

        if (networkCommand == null || networkCommand.equals("")) {
            return;
        }

        networkingService.tasks.remove(this);

        //handle this command
        NetworkCommand command = new NetworkCommand(networkCommand);
        handleCommand(command);
    }

    //handle possible commands.
    protected abstract void handleCommand(NetworkCommand command);

    //execute a new object of its own class
    protected abstract void selfRestart();

}