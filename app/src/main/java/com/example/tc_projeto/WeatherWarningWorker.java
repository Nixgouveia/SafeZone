package com.example.tc_projeto;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.tc_projeto.model.WeatherWarning;
import com.example.tc_projeto.repository.WeatherWarningRepository;

import java.util.ArrayList;
import java.util.List;

public class WeatherWarningWorker extends Worker {

    private static final String CHANNEL_ID = "weather_warning_channel";
    private List<WeatherWarning> lastWarnings = new ArrayList<>();
    // Recuperar o valor da SharedPreferences


    public WeatherWarningWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        createNotificationChannel(context);
    }

    @SuppressLint("WrongThread")
    @NonNull
    @Override
    public Result doWork() {

        WeatherWarningRepository repository = new WeatherWarningRepository();
        repository.getWeatherWarnings().observeForever(warnings -> {
            if (warnings != null && !warnings.isEmpty()) {
                // Verificar novos avisos
                List<WeatherWarning> newWarnings = getNewWarnings(warnings);
                if (!newWarnings.isEmpty()) {
                    for (WeatherWarning warning : newWarnings) {
                        sendNotification("Aviso Meteorológico", formatWarningMessage(warning));
                    }
                }
                // Atualizar a lista de avisos anteriores
                lastWarnings = warnings;
            }
        });
        return Result.success();
    }

    private List<WeatherWarning> getNewWarnings(List<WeatherWarning> currentWarnings) {
        List<WeatherWarning> newWarnings = new ArrayList<>();
        String tipo="green";
        for (WeatherWarning warning : currentWarnings) {
            if (!lastWarnings.contains(warning) &&  !tipo.equals(warning.getAwarenessLevelID())) {
                newWarnings.add(warning);
            }
        }
        return newWarnings;
    }

    private String formatWarningMessage(WeatherWarning warning) {
        return warning.getAwarenessTypeName() + " - Risco " + warning.getAwarenessLevelID() +
                " em " + warning.getIdAreaAviso() +
                ". Início: " + warning.getStartTime() +
                ", Fim: " + warning.getEndTime();
    }

    private void sendNotification(String title, String message) {
        Context context = getApplicationContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build()); // ID único para cada notificação
    }

    private void createNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Weather Warnings";
            String description = "Channel for weather warning notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

