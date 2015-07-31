package com.starhorizon.prototype1;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class CountDown extends Service {
    private final int ID = 1;
    private Intent[] notificationIntent = new Intent[1];
    private PendingIntent pIntent;
    private final Handler handler = new Handler();
    private Runnable task;

    private final long second = 1000;
    private final long minute = second*60;
    private final long hour = minute*60;

    private String taskName ="";
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(this,"OnCreate()",Toast.LENGTH_SHORT).show();

        SharedPreferences sp = getSharedPreferences(InitialPageActivity.SP_string_data,0);
        taskName = sp.getString(InitialPageActivity.SP_string_taskName,"");

        notificationIntent[0] = new Intent(this,SecondPageActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pIntent = PendingIntent.getActivities(this,0, notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = CreateNotification("");
        startForeground(ID, notification);

        try {
            DateFormat format = DateFormat.getDateInstance();
            String target = format.format(new Date());
            final long targetTime = format.parse(target).getTime() + 24*hour;

            task = new Runnable() {
                Vibrator myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                @Override
                public void run() {
                    long nowTime = System.currentTimeMillis();
                    if ( nowTime >= targetTime){
                        Toast.makeText(CountDown.this,"TimeUp!",Toast.LENGTH_SHORT).show();
                        myVibrator.vibrate(new long[]{10, 100, 10, 200, 10, 300}, -1);
                        handler.removeCallbacks(this);
                        stopForeground(true);
                        ShowDialog();
                        stopSelf();
                    }
                    else {
                        mNotificationManager.notify(ID, CreateNotification(GetDeltaTime(targetTime - nowTime)));
                        handler.postDelayed(this, 999);
                    }
                }
            };
            handler.postDelayed(task, 999);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void ShowDialog(){

        AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle("任務截止")
                .setIcon(R.drawable.task)
                .setMessage("時間到！真可惜，您的任務未完成")
                .setPositiveButton("確認", null)
                .setCancelable(false)
                .create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    private String GetDeltaTime(long deltaTime){

        if (deltaTime/minute >= 1){
            return "距離今日任務結束還有"+deltaTime/hour+"小時"+(deltaTime%hour)/minute+"分";
        }
        else {
            return "距離今日任務結束還有" + deltaTime / second + "秒";
        }
    }

    private Notification CreateNotification(String content){

        return new Notification.Builder(this)
                .setContentTitle(taskName)
                .setContentText(content)
                .setSmallIcon(R.drawable.task)
                .setContentIntent(pIntent)
                .build();
    }

    @Override
    public void onDestroy(){
        stopForeground(true);
        handler.removeCallbacks(task);
        super.onDestroy();
    }
}
