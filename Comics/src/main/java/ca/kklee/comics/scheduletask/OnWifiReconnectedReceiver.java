package ca.kklee.comics.scheduletask;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.kklee.utilities.ConnectionUtil;
import com.kklee.utilities.Logger;

/**
 * Created by Keith on 07/08/2014.
 */
public class OnWifiReconnectedReceiver extends BroadcastReceiver {

    public static void registerMe(Context context) {
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, OnWifiReconnectedReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Logger.i("OnWifiConnReceiver enabled");
    }

    public static void unregisterMe(Context context) {
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, OnBootCompletedReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Logger.i("OnWifiConnReceiver disabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("Wifi br onReceived");
        if (ConnectionUtil.isOnline(context)) {
            Logger.i("Resuming Silent Download");
            new SilentDownload(context, null).startSilentDownload();
            unregisterMe(context);
        }
    }

}
