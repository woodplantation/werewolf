package com.woodplantation.werwolf.communication.outgoing;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.woodplantation.werwolf.communication.incoming.ClientOutcomeBroadcastReceiver;
import com.woodplantation.werwolf.network.Client;

/**
 * Created by Sebu on 03.11.2016.
 */

public class ClientOutcomeBroadcastSender extends OutcomeBroadcastSender {

    public ClientOutcomeBroadcastSender(Client client) {
        super(client);
    }

    @Override
    public void playerListChanged() {

    }
}
