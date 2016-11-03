package com.woodplantation.werwolf.activities;

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
import com.woodplantation.werwolf.graphics.MyTextView;
import com.woodplantation.werwolf.network.Server;
import com.woodplantation.werwolf.network.ServerOutcomeBroadcastReceiver;

import java.util.ArrayList;

/**
 * Created by Sebu on 02.11.2016.
 */

public class LobbyActivity extends AppCompatActivity {

    private LocalBroadcastManager localBroadcastManager;
    private ServerOutcomeBroadcastReceiver serverOutcomeBroadcastReceiver;
    private IntentFilter intentFilter;

    private ListView listViewPlayers;
    private ArrayList<String> players = new ArrayList<>();

    private Intent serviceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LobbyActivity","create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        listViewPlayers = (ListView) findViewById(R.id.list_view_players);
        listViewPlayers.setAdapter(new ArrayAdapter<String>(this, R.layout.textview, players));

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        serverOutcomeBroadcastReceiver = new ServerOutcomeBroadcastReceiver(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(ServerOutcomeBroadcastReceiver.LOBBY_CREATE);
        intentFilter.addAction(ServerOutcomeBroadcastReceiver.PLAYER_LIST_CHANGED);

        serviceIntent = new Intent(this, Server.class);
        startService(serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listfragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_wiki:
                Intent intent = new Intent(this, WikiActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        localBroadcastManager.registerReceiver(serverOutcomeBroadcastReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        localBroadcastManager.unregisterReceiver(serverOutcomeBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        Log.d("LobbyActivity","onDestroy");
        stopService(serviceIntent);
        super.onDestroy();
    }

    public void lobbyCreate(boolean success, String address, String password) {
        if (success) {
            Toast.makeText(this, R.string.create_group_success, Toast.LENGTH_LONG).show();
            MyTextView ipTextView = (MyTextView) findViewById(R.id.text_view_group_address);
            ipTextView.setText(getString(R.string.adress_x, address));
            MyTextView passwordTextView = (MyTextView) findViewById(R.id.text_view_group_password);
            passwordTextView.setText(getString(R.string.password_x, password));
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
