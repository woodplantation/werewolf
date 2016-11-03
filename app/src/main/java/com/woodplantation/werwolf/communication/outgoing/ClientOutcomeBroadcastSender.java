package com.woodplantation.werwolf.communication.outgoing;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.woodplantation.werwolf.communication.incoming.ClientOutcomeBroadcastReceiver;

/**
 * Created by Sebu on 03.11.2016.
 */

public class ClientOutcomeBroadcastSender extends OutcomeBroadcastSender {

    public ClientOutcomeBroadcastSender(Service service) {
        super(service);
    }

    @Override
    public void playerListChanged() {

    }
}
