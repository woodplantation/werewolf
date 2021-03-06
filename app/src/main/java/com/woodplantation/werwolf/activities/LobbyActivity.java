package com.woodplantation.werwolf.activities;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.woodplantation.werwolf.Installation;
import com.woodplantation.werwolf.Notification;
import com.woodplantation.werwolf.R;
import com.woodplantation.werwolf.Wiki.RegelnActivity;
import com.woodplantation.werwolf.Wiki.RollenActivity;
import com.woodplantation.werwolf.communication.incoming.OutcomeBroadcastReceiver;
import com.woodplantation.werwolf.graphics.MyButton;
import com.woodplantation.werwolf.graphics.MyTextView;
import com.woodplantation.werwolf.network.Client;
import com.woodplantation.werwolf.communication.incoming.ClientOutcomeBroadcastReceiver;
import com.woodplantation.werwolf.network.NetworkingService;
import com.woodplantation.werwolf.network.Server;
import com.woodplantation.werwolf.communication.incoming.ServerOutcomeBroadcastReceiver;
import com.woodplantation.werwolf.network.objects.DisplaynameAndId;
import com.woodplantation.werwolf.network.objects.DisplaynameAndIdList;

import java.util.ArrayList;

/**
 * Created by Sebu on 02.11.2016.
 */

public class LobbyActivity extends AppCompatActivity {

    public static final String EXTRA_IS_SERVER = "is_server";
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_DISPLAYNAME = "displayname";

    private boolean server;

    private LocalBroadcastManager localBroadcastManager;
    private OutcomeBroadcastReceiver outcomeBroadcastReceiver;
    private IntentFilter intentFilter;

    private ListView listViewPlayers;
    private ArrayList<DisplaynameAndId> players = new ArrayList<>();

    private Intent serviceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LobbyActivity","create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Intent gameClassIntent = new Intent(this, GameSettingsActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(gameClassIntent);
            }
        });

        initGraphics();

        if (getIntent().getBooleanExtra(Notification.INTENT_COMING_FROM_NOTIFICATION, false)) {
            //TODO coming from notification. 
        } else {
            server = getIntent().getBooleanExtra(EXTRA_IS_SERVER, false);

            initNetworkingService();
        }
    }

    private void initGraphics() {
        //TODO server can click on client and kick him
        listViewPlayers = (ListView) findViewById(R.id.list_view_players);
        listViewPlayers.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, new ArrayList<String>()));

        MyButton leaveButton = (MyButton) findViewById(R.id.button_leave_lobby);
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(serviceIntent);
                finish();
            }
        });
    }

    private void initNetworkingService() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = getIntent();
        if (server) {
            initServer();
        } else {
            initClient(intent);
        }

        intentFilter = outcomeBroadcastReceiver.getIntentFilter();

        String displayName = intent.getStringExtra(EXTRA_DISPLAYNAME);
        serviceIntent.putExtra(NetworkingService.EXTRA_INITIALIZE_DISPLAYNAME, displayName);
        serviceIntent.putExtra(NetworkingService.EXTRA_INITIALIZE_ID, Installation.id(this));
        serviceIntent.setAction(NetworkingService.COMMAND_INITIALIZE);

        Log.d("Lobby","startin intent.");
        startService(serviceIntent);
    }

    private void initServer() {
        Log.d("Lobby","init server.");
        outcomeBroadcastReceiver = new ServerOutcomeBroadcastReceiver(this);

        serviceIntent = new Intent(this, Server.class);
    }

    private void initClient(Intent intent) {
        Log.d("Lobby","init client.");
        outcomeBroadcastReceiver = new ClientOutcomeBroadcastReceiver(this);

        String address = intent.getStringExtra(LobbyActivity.EXTRA_ADDRESS);
        int port = intent.getIntExtra(LobbyActivity.EXTRA_PORT, -1);
        Log.d("Startactivity","add: " + address + " port: " + port);
        serviceIntent = new Intent(this, Client.class);
        serviceIntent.putExtra(Client.EXTRA_INITIALIZE_ADDRESS, address);
        serviceIntent.putExtra(Client.EXTRA_INITIALIZE_PORT, port);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listfragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.wiki_rollen:
                intent = new Intent(this, RollenActivity.class);
                startActivity(intent);
                break;
            case R.id.wiki_regeln:
                intent = new Intent(this, RegelnActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onResume() {
        Log.d("LobbyActivity","onresume");
        super.onResume();
        localBroadcastManager.registerReceiver(outcomeBroadcastReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        Log.d("LobbyActivity","onstop");
        super.onStop();
        localBroadcastManager.unregisterReceiver(outcomeBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        Log.d("LobbyActivity","onDestroy");
        super.onDestroy();
    }

    public void lobbyCreate(boolean success, String address, int port) {
        if (success) {
            Toast.makeText(this, R.string.create_group_success, Toast.LENGTH_LONG).show();
            MyTextView addressTextView = (MyTextView) findViewById(R.id.text_view_group_address);
            addressTextView.setText(getString(R.string.adress_x, address));
            MyTextView portTextView = (MyTextView) findViewById(R.id.text_view_group_port);
            portTextView.setText(getString(R.string.port_x, port));
        } else {
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
        }
    }

    public void playerListChanged(DisplaynameAndIdList list) {
        players = list.list;
        ArrayList<String> names = new ArrayList<String>();
        for (DisplaynameAndId player : players) {
            names.add(player.displayname);
        }
        ((ArrayAdapter<String>) listViewPlayers.getAdapter()).clear();
        ((ArrayAdapter<String>) listViewPlayers.getAdapter()).addAll(names);
        ((ArrayAdapter<String>) listViewPlayers.getAdapter()).notifyDataSetChanged();
    }

}
