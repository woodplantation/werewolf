package com.woodplantation.werwolf.network.client;

import com.woodplantation.werwolf.network.Client;
import com.woodplantation.werwolf.network.NetworkCommand;
import com.woodplantation.werwolf.network.NetworkingService;
import com.woodplantation.werwolf.network.objects.DisplaynameAndIdList;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Sebu on 10.01.2017.
 */

public class ReadingIncomingCommandsTask extends com.woodplantation.werwolf.network.ReadingIncomingCommandsTask {

    public ReadingIncomingCommandsTask(NetworkingService client) {
        super(client, ((Client) client).getIn());
    }

    @Override
    protected void selfRestart() {
        ReadingIncomingCommandsTask task = new ReadingIncomingCommandsTask(networkingService);
        task.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void handleCommand(NetworkCommand networkCommand) {
        switch (networkCommand.type) {
            case SERVER_CLIENT_DISPLAYNAMES: {
                ((Client) networkingService).setDisplaynameAndIdList((DisplaynameAndIdList) networkCommand.command);
                (networkingService).getOutcomeBroadcastSender().playerListChanged(((Client) networkingService).getDisplaynameAndIdList());
                break;
            }
            case SERVER_CLIENT_SHUTDOWN: {
                ((Client) networkingService).getOutcomeBroadcastSender().serviceStoppedShowDialogFinishActivity("Spiel wurde beendet.");
                networkingService.stopSelf();
                break;
            }
            case SERVER_CLIENT_KICK: {
                //TODO
            }
        }
    }
}
