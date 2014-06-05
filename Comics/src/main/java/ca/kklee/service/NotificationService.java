package ca.kklee.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Keith on 31/05/2014.
 */

public class NotificationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Intent i = new Intent(getBaseContext(), HomeActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
    }
}
