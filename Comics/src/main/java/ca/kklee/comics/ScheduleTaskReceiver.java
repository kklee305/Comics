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

    public static void startScheduledTask(Context context) {
        Intent intent = new Intent(context, ScheduleTaskReceiver.class);
        Calendar cal = Calendar.getInstance();
        AlarmManager alarms;
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
//        cal.getTimeInMillis()
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
//        alarms.cancel(alarmIntent);
        Logger.d("", "Alarm set");
    }

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
}
