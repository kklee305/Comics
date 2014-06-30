package ca.kklee.comics;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import ca.kklee.comics.loaders.AbstractComicLoaderFactory;
import ca.kklee.util.Logger;

/**
 * Created by Keith on 28/06/2014.
 */
public class ScheduleTaskReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        downloadFiles(context);
        fireNotification(context);
    }

    private void downloadFiles(Context context) {
        Logger.d("", "SilentDownload");
        Comic[] comics = ComicCollection.getInstance().getComics();
        if (comics == null) {
            ComicCollection.getInstance().setComics(context);
            comics = ComicCollection.getInstance().getComics();
        }
        for (Comic c : comics) c.clearBitmap();
        for (int i = 0; i < comics.length; i++) {
            Bitmap bitmap = ComicCollection.getInstance().getComics()[i].getBitmap();
            if (bitmap == null)
                AbstractComicLoaderFactory.getLoader(null, i).execute(comics[i].getImgSrc());
        }
    }

    private void fireNotification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("New Comics Downloaded")
                        .setContentText("")
                        .setContentIntent(startAppIntent(context))
                        .setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private PendingIntent startAppIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        return PendingIntent.getActivity(context, 0, i, 0);
    }

    public static void startScheduledTask(Context context) {
        Intent intent = new Intent(context, ScheduleTaskReceiver.class);
        if (checkAlarmSet(context, intent)) {
            Logger.d("", "Alarm already set");
            return;
        }
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        Logger.d("", "Alarm set");
    }

    private static boolean checkAlarmSet(Context context, Intent intent) {
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    public static void cancelAlarm(Context context) {
        Logger.d("", "Alarm Cancelled");
        Intent intent = new Intent(context, ScheduleTaskReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(alarmIntent);
    }

}
