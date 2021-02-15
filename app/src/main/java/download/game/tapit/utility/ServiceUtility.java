package download.game.tapit.utility;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import download.game.tapit.R;

public class ServiceUtility {

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Build notification and call {@link Service#startForeground(int, Notification)} which is required
     * for a foreground service to run
     */
    public static void buildServiceNotification(Service service, Class<?> parentClass) {
        Intent notificationIntent = new Intent(service, parentClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(service, "notif_channel_0")
                .setContentTitle(service.getResources().getString(R.string.app_name))
                .setContentText(service.getString(R.string.app_name) + " is running")
                .setTicker(service.getResources().getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notif_channel_0", "notif_channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("NA");
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.enableVibration(false);
            NotificationManager notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        service.startForeground(101, notification);
    }

}
