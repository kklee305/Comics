package ca.kklee.comics;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import ca.kklee.util.Logger;

/**
 * Created by Keith on 30/06/2014.
 */
public class OptionsDialogFactory {

    public static Dialog createDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, 2); //2 is for theme
        builder.setTitle("Options")
                .setItems(enumToStringList(activity), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i) {
                            case 0:
                                BitmapLoader.clearBitmap();
                                Intent intent = new Intent(activity, HomeActivity.class);
                                activity.finish();
                                activity.startActivity(intent);
                                break;
                            case 1:
                                if (ScheduleTaskReceiver.isAlarmSet(activity)) {
                                    ScheduleTaskReceiver.cancelAlarm(activity);
                                } else {
                                    ScheduleTaskReceiver.startScheduledTask(activity);
                                }
                                break;
                            case 2:
                                Toast.makeText(activity, "Keith made this", Toast.LENGTH_SHORT).show();
                                break;
//                            case 3:
//                                debugging(activity);
//                                break;
                        }
                    }
                });
        Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM | Gravity.LEFT;
        window.setAttributes(wlp);

        return dialog;
    }

    private static void debugging(Activity activity) {
        Logger.d("DEBUGGING", "test notifications");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        NotificationManager mNotificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

        Logger.d("DEBUGGING", "testing parsing");
        Comic[] comics = ComicCollection.getInstance().getComics();
        Logger.d("test parsing", "Number of comics: " + comics.length);
        for (int i = 0; i < comics.length; i++) {
            Logger.d("test parsing", comics[i].toString());
        }
//        Logger.d("DEBUGGING", "testing image to binary");
//        Bitmap bitmap = ComicCollection.getInstance().getComics()[0].getBitmap();
//        BitmapLoader.saveBitmap("testing2",bitmap);

//        Logger.d("DEBUGGING", "testing bianary to image");
//        Bitmap bitmap = BitmapLoader.loadBitmap("testing");
//        if (bitmap != null) {
//            Logger.d("","Yay worked");
//        }
    }

    private static String[] enumToStringList(Activity activity) {
        String[] menuList = new String[MenuItems.values().length];
        int i = 0;
        for (MenuItems m : MenuItems.values()) {
            menuList[i] = m.name();
            if (menuList[i].equals(MenuItems.ALARM.name())) {
                menuList[i] = getAlarmStateString(activity);
            }
            i++;
        }
        return menuList;
    }

    private static String getAlarmStateString(Activity activity) {
        if (ScheduleTaskReceiver.isAlarmSet(activity)) {
            return "Set Auto-DL OFF";
        } else {
            return "Set Auto-DL ON";
        }
    }

    private enum MenuItems {CLEAR, ALARM, ABOUT}

}
