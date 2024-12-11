package com.example.tc_projeto;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.tc_projeto.model.WeatherWarning;
import com.example.tc_projeto.repository.WeatherWarningRepository;
import java.util.List;

public class WeatherWarningWorker extends Worker {

    private static final String CHANNEL_ID = "weather_warning_channel";

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
                // Enviar notificação se houver novos avisos
                sendNotification("Novo aviso do IPMA", "Há novos avisos meteorológicos disponíveis.");
            }
        });
        return Result.success();
    }

    private void sendNotification(String title, String message) {
        Context context = getApplicationContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                /*.setSmallIcon(R.drawable.ic_notification)*/
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
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
