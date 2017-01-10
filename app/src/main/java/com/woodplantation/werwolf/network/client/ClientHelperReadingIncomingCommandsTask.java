package com.woodplantation.werwolf.network.client;

import com.woodplantation.werwolf.network.Client;
import com.woodplantation.werwolf.network.NetworkCommand;
import com.woodplantation.werwolf.network.NetworkingService;
import com.woodplantation.werwolf.network.ReadingIncomingCommandsTask;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Sebu on 10.01.2017.
 */

public class ClientHelperReadingIncomingCommandsTask extends ReadingIncomingCommandsTask {

    public ClientHelperReadingIncomingCommandsTask(NetworkingService client) {
        super(client, ((Client) client).getIn(), ClientHelperReadingIncomingCommandsTask.class);
    }

    //used for self restart in superclass
    ClientHelperReadingIncomingCommandsTask(NetworkingService client, BufferedReader in, Class<? extends ReadingIncomingCommandsTask> taskClass) {
        super(client, in, taskClass);
    }

    @Override
    protected void handleCommand(NetworkCommand command) {
        switch (command.type) {
            case SERVER_CLIENT_DISPLAYNAMES: {
                if (command.string.startsWith("[")) {
                    command.string = command.string.substring(1);
                }
                if (command.string.endsWith("]")) {
                    command.string = command.string.substring(0, command.string.length()-1);
                }
                ((Client) networkingService).setDisplayNames(new ArrayList<String>(Arrays.asList(command.string.split("\\s*,\\s*"))));
                (networkingService).getOutcomeBroadcastSender().playerListChanged(((Client) networkingService).getDisplayNames());
                break;
            }
            case SERVER_CLIENT_SHUTDOWN: {
                ((Client) networkingService).getOutcomeBroadcastSender().serviceStoppedShowDialogFinishActivity("Spiel wurde beendet.");
                networkingService.stopSelf();
                break;
            }
        }
    }
}
