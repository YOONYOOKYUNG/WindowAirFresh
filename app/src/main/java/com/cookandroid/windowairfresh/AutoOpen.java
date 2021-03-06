package com.cookandroid.windowairfresh;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AutoOpen extends Thread {
    //자동으로 창문이 열리는 모드
    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    WindowListAdapter adapter = new WindowListAdapter();

    public void run() {
        //DatabaseManager 객체 얻어오기
        DatabaseManager databaseManager = DatabaseManager.getInstance(MainActivity.mContext);

        while (true) {
            //Main에서 받아온 API 수치와 사용자 설정수치 가져와 비교
            SharedPreferences sf = (MainActivity.mContext).getSharedPreferences("autoset", 0);
            int hottemp = Integer.parseInt(sf.getString("high_temp", "30"));
            int coldtemp = Integer.parseInt(sf.getString("low_temp", "0"));
            int comparedust = Integer.parseInt(sf.getString("compare_dust", "20"));
            Boolean modestate = sf.getBoolean("modestate", false);
            float insidedust = ((MainActivity) MainActivity.mContext).insidedust;
            float outsidedust = ((MainActivity) MainActivity.mContext).outsidedust;
            float outsidetemp = ((MainActivity) MainActivity.mContext).outsidetemp;
            int outsiderain = ((MainActivity) MainActivity.mContext).outsiderain;

            adapter = new WindowListAdapter();
            adapter.setDatabaseManager(databaseManager);
            adapter.initialiseList();

            float dustresult = outsidedust - insidedust;
            int windownumber = adapter.getCount();

            //모드 구분 코드
            if (modestate) {
                if (outsiderain == 0 && coldtemp < outsidetemp && outsidetemp < hottemp && dustresult + comparedust < 0) {
                    Log.d("자동모드", "자동모드:창문 열었어요");

                    boolean windowsOpened = false;
                    //창문 자동 설정 - 모두 열기
                    for (int i = 0; i < windownumber; i++) {
                        {
                            if (!adapter.listViewItemList.get(i).getState()) {
                                ((MainActivity) MainActivity.mContext).openwindow(i);
                                windowsOpened = true;
                                adapter.listViewItemList.get(i).setState(true);
                                if (databaseManager != null) {
                                    ContentValues updateRowValue = new ContentValues();
                                    updateRowValue.put("state", "true");
                                    databaseManager.update(updateRowValue, adapter.listViewItemList.get(i).getName());
                                }
                            }
                        }
                    }

                    //창문이 자동으로 열릴 시
                    if (windowsOpened) {
                        //메세지 보내기
                        Message message = ((MainActivity) MainActivity.mContext).autohandler.obtainMessage(1);
                        message.sendToTarget();

                        //Timeline 추가
                        //현재 날짜, 시간 불러오기
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf_date = new SimpleDateFormat("MM/dd E");
                        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");

                        String date = sdf_date.format(cal.getTime());
                        String time = sdf_time.format(cal.getTime());
                        String content = "사용자가 설정한 수치에 따라 창문이 열렸습니다.";

                        //table에 넣기
                        databaseManager.timeline_insert(date, time, content, "열림");

                        //알림 띄우기
                        openNoty();

                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        }
    }

    //창문이 자동으로 열렸을 시 띄우는 알림
    private void openNoty() {
        //알림 클릭 시 넘어가는 페이지
        Intent intent = new Intent(MainActivity.mContext, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.mContext, 0, intent, 0);

        //Builder 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.mContext, "channel_open");
        //아이콘, 제목, 내용, 탭 클릭 시 자동 제거
        builder.setSmallIcon(R.drawable.window_open)
                .setContentTitle("자동 개폐 알림")
                .setContentText("내부 미세먼지 수치가 외부보다 높아 모든 창문을 열였습니다.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //Notification 관리자 객체를 Context로부터 소환
        NotificationManager manager = (NotificationManager) MainActivity.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Notification 채널 생성
            manager.createNotificationChannel(new NotificationChannel("channel_open",
                    "자동 열림 ", NotificationManager.IMPORTANCE_HIGH));
        }

        manager.notify(1, builder.build());
    }
}