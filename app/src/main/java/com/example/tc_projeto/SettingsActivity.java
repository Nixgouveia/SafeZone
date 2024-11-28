package com.example.tc_projeto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        ImageButton homeButton = findViewById(R.id.button_home);
        ImageButton notificationsButton = findViewById(R.id.button_notifications);
        ImageButton settingsButton = findViewById(R.id.button_settings);
        Switch noti = findViewById(R.id.notification_switch);
        Switch incendio = findViewById(R.id.notification_switch3);
        Switch inundacao = findViewById(R.id.notification_switch2);
        Switch tempestade = findViewById(R.id.notification_switch4);

        //Definições de Notificações

        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });





        // Barra de navegação
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }
}
