package com.woodplantation.werwolf.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pGroup;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.woodplantation.werwolf.R;
import com.woodplantation.werwolf.graphics.MyTextView;
import com.woodplantation.werwolf.network.Server;

/**
 * Created by Sebu on 02.11.2016.
 */

public class LobbyActivity extends AppCompatActivity {

    private Server mService;
    private boolean mBound = false;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.d("LobbyActivity","on service connected");
            Server.LocalBinder binder = (Server.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setActivity(LobbyActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LobbyActivity","create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
    }

    @Override
    protected void onStart() {
        Log.d("LobbyActivity","start");
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, Server.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LobbyActivity","onstop");
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     *
     * @param group null if failed
     */
    public void onCreateGroupResult(WifiP2pGroup group) {
        if (group == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.create_group_failed_title);
            builder.setMessage(R.string.create_group_failed_text);
            builder.setCancelable(false);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    LobbyActivity.this.finish();
                }
            });
            builder.show();
        } else {
            Toast.makeText(this, R.string.create_group_success, Toast.LENGTH_LONG).show();
            MyTextView ipTextView = (MyTextView) findViewById(R.id.text_view_group_address);
            ipTextView.setText(getString(R.string.adress_x, group.getNetworkName()));
            MyTextView passwordTextView = (MyTextView) findViewById(R.id.text_view_group_password);
            passwordTextView.setText(getString(R.string.password_x, group.getPassphrase()));
        }
    }

}
