package com.woodplantation.werwolf.communication.incoming;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Sebu on 02.11.2016.
 */

public class ClientOutcomeBroadcastReceiver extends OutcomeBroadcastReceiver {


    public ClientOutcomeBroadcastReceiver(Activity activity) {
        super(activity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d("ClientOutcomeBR","on receive. action: " + action);
        if (action == null) {
            return;
        }
        switch(action) {

            default:
                super.onReceive(context, intent);
        }

    }
}
