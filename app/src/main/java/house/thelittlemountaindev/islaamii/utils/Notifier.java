package house.thelittlemountaindev.islaamii.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import house.thelittlemountaindev.islaamii.MainActivity;
import house.thelittlemountaindev.islaamii.R;

public class Notifier extends BroadcastReceiver {

    private Context mContext;

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        Intent notifIntent = new Intent(context, MainActivity.class);
        notifIntent.putExtra("fragmentToOpen", "prayerFragment");

        //Adding an intent to allow the user to share/send the notification
        Intent sharing = new Intent(android.content.Intent.ACTION_SEND);
        sharing.setType("text/plain");

        //This line is for removing 'just once' 'always' button from sharing chooser.
        Intent openInChooser = Intent.createChooser(sharing, "Send with..");
        String shareBody = context.getResources().getString(R.string.notif_title);
        sharing.putExtra(android.content.Intent.EXTRA_SUBJECT, "Remind Someone");
        sharing.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + "\nhttps://goo.gl/RU8Dvf");

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, notifIntent, 0);
        PendingIntent pIntentShare = PendingIntent.getActivity(context, 0, openInChooser, 0);

        //Running the notification
        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.notif_title))
                .setContentText(context.getResources().getString(R.string.notif_text))
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_share, "Remind someone", pIntentShare)
                .setSmallIcon(R.drawable.ic_launcher)
                .setVibrate(new long[] { 1000, 1000, 1000,})
                .build();
        notification.sound = Uri.parse("android.resource://"
                + context.getPackageName() + "/" + R.raw.azan1);
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(0, notification);
        mutePhone();
    }

    //Method to mute the phone after azaan & turn it back to previous state 30min laters
    private void mutePhone(){

        int muteDelay = 30; //The length of the azaan audio file (2min 13sec)
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //MUTE the PHONE
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean silentMode = preferences.getBoolean("silen_status", true);
                if (silentMode) {
                    int restoreDelay = 60; // 30min

                    //Get the current ring mode, save it to be restored
                    //Set the ring mode to silent and call the runnable to reactivate previous state;
                    AudioManager aMngr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                    final int currentRingMode = aMngr.getRingerMode();
                    aMngr.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            AudioManager aMngr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                            aMngr.setRingerMode(currentRingMode);
                            //Toast.makeText(mContext, "Your phone status has been restored", duration).show();
                        }
                    }, restoreDelay * 1000);

                }

            }
        }, muteDelay * 1000);

    }

    private void restoreRinger(){

    }
}
