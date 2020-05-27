package e.asus.whitedoc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Application;
import android.os.Build;

public class NotificationChannel1 extends Application{

    public static final String CHANNEL_ID="channel1";

    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
    }


    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name="canal";
            String descripcion="descripcion canal";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(descripcion);

            NotificationManager notificationManager= getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
