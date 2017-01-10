package com.woodplantation.werwolf.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Sebu on 10.01.2017.
 */

public abstract class ReadingIncomingCommandsTask  extends AsyncTask<Void,Void,String> {

    protected NetworkingService networkingService;
    private BufferedReader in;
    private Class<? extends ReadingIncomingCommandsTask> taskClass;

    public ReadingIncomingCommandsTask(NetworkingService networkingService, BufferedReader in, Class<? extends ReadingIncomingCommandsTask> taskClass) {
        this.networkingService = networkingService;
        this.in = in;
        this.taskClass = taskClass;
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
        networkingService.tasks.remove(this);

        //if we finished already we dont want to handle stuff anymore
        if (networkingService.finish) {
            return;
        }

        //start new Incoming Command
        try {
            Constructor<? extends ReadingIncomingCommandsTask> taskConstructor = taskClass.getConstructor(NetworkingService.class, BufferedReader.class, taskClass.getClass());
            ReadingIncomingCommandsTask task = taskConstructor.newInstance(new Object[] {networkingService, in, taskClass});
            task.executeOnExecutor(THREAD_POOL_EXECUTOR);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (networkCommand == null || networkCommand.equals("")) {
            return;
        }

        //handle this command
        NetworkCommand command = new NetworkCommand(networkCommand);
        handleCommand(command);
    }

    protected abstract void handleCommand(NetworkCommand command);

}