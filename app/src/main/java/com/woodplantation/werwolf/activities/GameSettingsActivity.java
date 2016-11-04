package com.woodplantation.werwolf.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.woodplantation.werwolf.R;

public class GameSettingsActivity extends AppCompatActivity {

    private int playerCount;
    private int villagerCount;
    private int werewolfCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamesettings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        playerCount =0;
        villagerCount=0;
        werewolfCount =0;
        initListener();
    }


    private void initListener() {
        final EditText players = (EditText) findViewById(R.id.edit_text_count_players);
        final EditText villager = (EditText) findViewById(R.id.edit_text_count_villager);
        final EditText werewolf = (EditText) findViewById(R.id.edit_text_count_werewolf);

        players.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("Ausgabe", "player: " + players.getText());
                int count = Integer.parseInt("0" + players.getText().toString()); //add 0 to make sure its an valid int.
                Log.d("Game Settings", "Anzahl Spieler: " + count);
                if (count <= 10 && count >= 6) {
                    villager.setHint(String.valueOf(count - 2));
                    werewolf.setHint(String.valueOf(2));
                } else if (count > 10 && count <= 15) {
                    villager.setHint(String.valueOf(count - 3));
                    werewolf.setHint(String.valueOf(3));
                } else if (count > 15) {
                    villager.setHint(String.valueOf(count - 4));
                    werewolf.setHint(String.valueOf(4));
                } else if (count <6) {
                    villager.setHint("");
                    werewolf.setHint("");
                }
                playerCount =count;
            }
        });

    }

}
