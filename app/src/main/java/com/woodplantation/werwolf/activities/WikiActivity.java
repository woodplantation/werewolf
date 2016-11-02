package com.woodplantation.werwolf.activities;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.woodplantation.werwolf.R;

import java.util.ArrayList;

public class WikiActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void showPopUp(View view) {
        Log.d("WikiActivity", "button geklickt");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (view.getId()){
            case R.id.wiki_villager:
                builder.setMessage(R.string.villager_info);
                break;
            case R.id.wiki_werewolf:
                builder.setMessage(R.string.werewolf_info);
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}





