package com.cookandroid.windowairfresh;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

class DeviceBootReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent){
        if(Objects.equals(intent.getAction(),"android.intent.action.BOOT_COMPLETED")){
            //디바이스 부팅이 완료되면 알람을 쉬게함.
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0 ,alarmIntent,0);
            AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            SharedPreferences sharedPreferences = context.getSharedPreferences("daily",Context.MODE_PRIVATE);
            long millis = sharedPreferences.getLong("nextNotifiy", Calendar.getInstance().getTimeInMillis());

            Calendar current_calendar = Calendar.getInstance();
            Calendar nextNotifyTime = new GregorianCalendar();
            nextNotifyTime.setTimeInMillis(sharedPreferences.getLong("nextNotifyTime",millis));

            if(current_calendar.after(nextNotifyTime)){
                nextNotifyTime.add(Calendar.DATE,1);
            }
            Date currentDateTime = nextNotifyTime.getTime();
            String date_txt = new SimpleDateFormat("hh시 mm분", Locale.getDefault()).format(currentDateTime);

            Toast.makeText(context.getApplicationContext(),"다음 알림은"+date_txt+"으로 알림이 설정되었습니다!",Toast.LENGTH_LONG).show();

            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP,nextNotifyTime.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,pendingIntent);
            }

        }
    }
}
