package com.woodplantation.werwolf.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.woodplantation.werwolf.R;
import com.woodplantation.werwolf.Wiki.RegelnActivity;
import com.woodplantation.werwolf.Wiki.RollenActivity;
import com.woodplantation.werwolf.graphics.MyButton;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initGraphics();
    }

    private void initGraphics() {
        LobbyStartButtonListener lobbyStartButtonListener = new LobbyStartButtonListener();

        MyButton createButton = (MyButton) findViewById(R.id.button_create_lobby);
        createButton.setOnClickListener(lobbyStartButtonListener);

        MyButton joinButton = (MyButton) findViewById(R.id.button_join_lobby);
        joinButton.setOnClickListener(lobbyStartButtonListener);
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
            case R.id.menu_settings:
                intent = new Intent(this, GameSettingsActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private class LobbyStartButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            boolean server = view.getId() == R.id.button_create_lobby;
            Intent intent = new Intent(StartActivity.this, LobbyActivity.class);
            intent.putExtra(LobbyActivity.EXTRA_IS_SERVER, server);
            if (!server) {
                String address = ((EditText) findViewById(R.id.edit_text_join_lobby_name)).getText().toString();
                intent.putExtra(LobbyActivity.EXTRA_ADDRESS, address);
            }
            startActivity(intent);
        }
    }
}
