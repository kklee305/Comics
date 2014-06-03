package ca.kklee.comics;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Keith on 02/06/2014.
 */
public class TitleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.title_fragment, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);

        Button test = (Button) rootView.findViewById(R.id.btn_test_notification);
        test.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("DEBUGGING", "test notifications");
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getActivity().getApplicationContext())
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle("My notification")
                                        .setContentText("Hello World!");
                        NotificationManager mNotificationManager =
                                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(1, mBuilder.build());
                    }
                }
        );

        return rootView;
    }
}
