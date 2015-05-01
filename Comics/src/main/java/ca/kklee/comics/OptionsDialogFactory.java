package ca.kklee.comics;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import ca.kklee.comics.scheduletask.ScheduleTaskReceiver;

/**
 * Created by Keith on 30/06/2014.
 */
public class OptionsDialogFactory {

    public static Dialog createDialog(final HomeActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle); //2 is for theme
        builder.setTitle("Settings")
                .setItems(enumToStringList(activity), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i) {
                            case 0:
                                if (ScheduleTaskReceiver.isAlarmSet(activity)) {
                                    ScheduleTaskReceiver.cancelAlarm(activity);
                                } else {
                                    ScheduleTaskReceiver.startScheduledTask(activity);
                                }
                                break;
                            case 1:
                                Toast.makeText(activity, "Keith made this", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                BitmapLoader.clearBitmap();
                                Intent intent = new Intent(activity, HomeActivity.class);
                                activity.finish();
                                activity.startActivity(intent);
                                break;
                            case 4:
                                debugging(activity);
                                break;
                        }
                    }
                });
        Dialog dialog = builder.create();
        return dialog;
    }

    private static void debugging(Activity activity) {
        ScheduleTaskReceiver.cancelAlarm(activity);
        ScheduleTaskReceiver.startDebugging(activity);
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
            return "Set Auto-Refresh OFF";
        } else {
            return "Set Auto-Refresh ON";
        }
    }

    private enum MenuItems {ALARM, ABOUT}

}
