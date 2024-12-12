package com.example.tc_projeto;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String CHANNEL_ID = "my_channel_id";
    private static final String PREFS_NAME = "notification_prefs";
    private static final String PREF_NOTIFICATION_ENABLED = "notification_enabled";
    private static final String PREF_INCENDIO_ENABLED = "incendio_enabled";
    private static final String PREF_INUNDACAO_ENABLED = "inundacao_enabled";
    private static final String PREF_TEMPESTADE_ENABLED = "tempestade_enabled";

    private Switch noti;
    private Switch incendio;
    private Switch inundacao;
    private Switch tempestade;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton homeButton = findViewById(R.id.button_home);
        ImageButton notificationsButton = findViewById(R.id.button_notifications);
        ImageButton settingsButton = findViewById(R.id.button_settings);
        noti = findViewById(R.id.notification_switch);
        incendio = findViewById(R.id.notification_switch3);
        inundacao = findViewById(R.id.notification_switch2);
        tempestade = findViewById(R.id.notification_switch4);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Carregar o estado salvo dos switches
        boolean isNotificationEnabled = sharedPreferences.getBoolean(PREF_NOTIFICATION_ENABLED, false);
        boolean isIncendioEnabled = sharedPreferences.getBoolean(PREF_INCENDIO_ENABLED, false);
        boolean isInundacaoEnabled = sharedPreferences.getBoolean(PREF_INUNDACAO_ENABLED, false);
        boolean isTempestadeEnabled = sharedPreferences.getBoolean(PREF_TEMPESTADE_ENABLED, false);

        noti.setChecked(isNotificationEnabled);
        incendio.setChecked(isIncendioEnabled);
        inundacao.setChecked(isInundacaoEnabled);
        tempestade.setChecked(isTempestadeEnabled);

        // Verificar permissão ao inicializar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isNotificationEnabled) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                noti.setChecked(false);  // Desabilitar o switch se a permissão não estiver concedida
            }
        }

        noti.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestNotificationPermission();
            } else {
                saveNotificationPreference(PREF_NOTIFICATION_ENABLED, false);
            }
        });

        incendio.setOnCheckedChangeListener((buttonView, isChecked) -> saveNotificationPreference(PREF_INCENDIO_ENABLED, isChecked));
        inundacao.setOnCheckedChangeListener((buttonView, isChecked) -> saveNotificationPreference(PREF_INUNDACAO_ENABLED, isChecked));
        tempestade.setOnCheckedChangeListener((buttonView, isChecked) -> saveNotificationPreference(PREF_TEMPESTADE_ENABLED, isChecked));

        // Barra de navegação
        homeButton.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, MainActivity.class)));

        notificationsButton.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, NotificationsActivity.class)));

        settingsButton.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, SettingsActivity.class)));


    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            } else {
                saveNotificationPreference(PREF_NOTIFICATION_ENABLED, true);
                sendNotification();
            }
        } else {
            saveNotificationPreference(PREF_NOTIFICATION_ENABLED, true);
            sendNotification();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveNotificationPreference(PREF_NOTIFICATION_ENABLED, true);
                sendNotification();
            } else {
                noti.setChecked(false);  // Desmarcar o switch se a permissão for negada
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveNotificationPreference(String key, boolean isEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isEnabled);
        editor.apply();
    }

    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("SafeZone")
                .setContentText("Ativou as notificações!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

}
