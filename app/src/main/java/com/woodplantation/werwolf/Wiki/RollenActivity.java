package com.woodplantation.werwolf.Wiki;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.woodplantation.werwolf.R;

public class RollenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rollen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void showPopUp(View view) {
        Log.d("RollenActivity", "button geklickt");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (view.getId()){
            case R.id.wiki_villager:
                builder.setMessage(R.string.villager_info);
                break;
            case R.id.wiki_werewolf:
                builder.setMessage(R.string.werewolf_info);
                break;
            case R.id.wiki_witch:
                builder.setMessage(R.string.witch_info);
                break;
            case R.id.wiki_hunter:
                builder.setMessage(R.string.hunter_info);
                break;
            case R.id.wiki_oracle:
                builder.setMessage(R.string.oracle_info);
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}





