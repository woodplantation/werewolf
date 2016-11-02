package com.woodplantation.werwolf.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.woodplantation.werwolf.R;
import com.woodplantation.werwolf.graphics.MyButton;
import com.woodplantation.werwolf.graphics.MySwitch;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initGraphics();
    }

    private void initGraphics() {
        MyButton createButton = (MyButton) findViewById(R.id.button_create_lobby);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, LobbyActivity.class);
                startActivity(intent);
            }
        });
    }
}
