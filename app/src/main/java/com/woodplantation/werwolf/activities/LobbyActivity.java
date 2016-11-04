package com.woodplantation.werwolf.activities;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.woodplantation.werwolf.R;
import com.woodplantation.werwolf.Wiki.RegelnActivity;
import com.woodplantation.werwolf.Wiki.RollenActivity;
import com.woodplantation.werwolf.graphics.MyTextView;
import com.woodplantation.werwolf.network.Client;
import com.woodplantation.werwolf.communication.incoming.ClientOutcomeBroadcastReceiver;
import com.woodplantation.werwolf.network.NetworkingService;
import com.woodplantation.werwolf.network.Server;
import com.woodplantation.werwolf.communication.incoming.ServerOutcomeBroadcastReceiver;

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
    private BroadcastReceiver outcomeBroadcastReceiver;
    private IntentFilter intentFilter;

    private ListView listViewPlayers;
    private ArrayList<String> players = new ArrayList<>();

    private Intent serviceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LobbyActivity","create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        server = getIntent().getBooleanExtra(EXTRA_IS_SERVER, false);

        initGraphics();

        initNetworkingService();
    }

    private void initGraphics() {
        listViewPlayers = (ListView) findViewById(R.id.list_view_players);
        listViewPlayers.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, players));
    }

    private void initNetworkingService() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        Intent intent = getIntent();
        if (server) {
            initServer();
        } else {
            initClient(intent);
        }
        if (serviceIntent != null) {
            String displayName = intent.getStringExtra(EXTRA_DISPLAYNAME);
            serviceIntent.putExtra(NetworkingService.EXTRA_INITIALIZE_DISPLAYNAME, displayName);
            serviceIntent.setAction(NetworkingService.COMMAND_INITIALIZE);

            Log.d("Lobby","startin intent.");
            startService(serviceIntent);
        }
    }

    private void initServer() {
        Log.d("Lobby","init server.");
        outcomeBroadcastReceiver = new ServerOutcomeBroadcastReceiver(this);
        intentFilter.addAction(ServerOutcomeBroadcastReceiver.LOBBY_CREATE);
        intentFilter.addAction(ServerOutcomeBroadcastReceiver.PLAYER_LIST_CHANGED);

        serviceIntent = new Intent(this, Server.class);
    }

    private void initClient(Intent intent) {
        Log.d("Lobby","init client.");
        outcomeBroadcastReceiver = new ClientOutcomeBroadcastReceiver(this);
        intentFilter.addAction(ClientOutcomeBroadcastReceiver.SERVICE_STOPPED_SHOW_DIALOG_FINISH_ACTIVITY);

        String address = intent.getStringExtra(LobbyActivity.EXTRA_ADDRESS);
        int port = intent.getIntExtra(LobbyActivity.EXTRA_ADDRESS, -1);
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
        super.onResume();
        localBroadcastManager.registerReceiver(outcomeBroadcastReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        localBroadcastManager.unregisterReceiver(outcomeBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        Log.d("LobbyActivity","onDestroy");
        stopService(serviceIntent);
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

    public void playerListChanged(ArrayList<String> list) {
        players = list;
        ((ArrayAdapter<String>) listViewPlayers.getAdapter()).notifyDataSetChanged();
    }

}
