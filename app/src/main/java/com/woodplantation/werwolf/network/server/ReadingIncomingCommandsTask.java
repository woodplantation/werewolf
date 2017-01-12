package com.woodplantation.werwolf.network.server;

import com.woodplantation.werwolf.network.NetworkCommand;
import com.woodplantation.werwolf.network.NetworkingService;

import java.io.BufferedReader;

/**
 * Created by Sebu on 12.01.2017.
 */

public class ReadingIncomingCommandsTask extends com.woodplantation.werwolf.network.ReadingIncomingCommandsTask {

    public ReadingIncomingCommandsTask(NetworkingService server, BufferedReader in) {
        super(server, in);
    }

    @Override
    protected void selfRestart() {
        ReadingIncomingCommandsTask task = new ReadingIncomingCommandsTask(networkingService, in);
        task.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void handleCommand(NetworkCommand command) {
        switch (command.type) {
            case CLIENT_SERVER_DISPLAYNAME:
        }
    }
}
