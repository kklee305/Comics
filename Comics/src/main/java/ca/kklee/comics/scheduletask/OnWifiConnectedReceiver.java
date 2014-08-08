package ca.kklee.comics.scheduletask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import ca.kklee.comics.SharedPrefConstants;
import ca.kklee.util.ConnectionUtil;

/**
 * Created by Keith on 07/08/2014.
 */
public class OnWifiConnectedReceiver extends ScheduleTaskReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectionUtil.isOnline(context)) {
            context.unregisterReceiver(this);
            SharedPreferences prefForNew = context.getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0);
            SharedPreferences.Editor editorForNew = prefForNew.edit();
            editorForNew.putBoolean(SharedPrefConstants.WIFIRECONNECT, false);
            editorForNew.apply();
            super.onReceive(context, intent);
        }
    }
}
