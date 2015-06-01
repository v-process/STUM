package com.example.administrator.STUM;

/**
 * Created by Administrator on 2015-05-27.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class TimeAlarm extends BroadcastReceiver {

    NotificationManager nm;
    int comparedrink = 0;// 기본값 선언

    Context mContext;//receive에 있는 콘테스트 값 받기.



    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        Compare();
        /*
        //if(comparedrink == 1) {//이애를 함수에 넣어서 쿼리에서 호출해보자..........................
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);//노티 등록
            // nm.cancel(1);//누르고 없애기
            CharSequence from = "스텀 알람";//제목
            CharSequence message = "등록 시간에 알람!";
            //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);

            @SuppressWarnings("deprecation")
            Notification notif = new Notification(R.drawable.status3, "성공입니다.", System.currentTimeMillis());

            Intent notificationIntent = new Intent(context, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.defaults |= Notification.DEFAULT_VIBRATE;
            notif.defaults |= Notification.DEFAULT_SOUND;

            notif.setLatestEventInfo(context, from, message, pendingIntent);
            nm.notify(1, notif);
        //}
       // compare = 0;
       */
    }
    void Compare() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> notiquery = ParseQuery.getQuery("Notification");
        //notiquery.addDescendingOrder("createdAt");
        notiquery.whereEqualTo("User", user);
        notiquery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("score", "The getFirst request failed.");
                    //Toast.makeText(TimeAlarm.this., "값 안받아져", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("score", "Retrieved the object.");
                    //Toast.makeText(ViewPagerFragment1.this.getActivity(), "값 받기 성공", Toast.LENGTH_SHORT).show();
                    int currentdrinkcheck;
                    int averagedrinkcheck;
                    currentdrinkcheck = (int) object.get("currentdrink");
                    averagedrinkcheck = (int) object.get("averagedrink");

                    int k =  compare2(averagedrinkcheck, currentdrinkcheck);
                    if(k == 1){
                        noti();
                    }

                }
            }
        });
    }
    void noti(){
        nm = (NotificationManager) this.mContext.getSystemService(this.mContext.NOTIFICATION_SERVICE);//노티 등록
        // nm.cancel(1);//누르고 없애기
        CharSequence from = "스텀 알람";//제목
        CharSequence message = "등록 시간에 알람!";
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);

        @SuppressWarnings("deprecation")
        Notification notif = new Notification(R.drawable.status3, "성공입니다.", System.currentTimeMillis());

        Intent notificationIntent = new Intent(this.mContext, SplashActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this.mContext, 0, notificationIntent, 0);
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.defaults |= Notification.DEFAULT_VIBRATE;
        notif.defaults |= Notification.DEFAULT_SOUND;

        notif.setLatestEventInfo(mContext, from, message, pendingIntent);
        nm.notify(1, notif);
    }



    int compare2(int averagedrinkcheck, int currentdrinkcheck) {
        if (averagedrinkcheck >= currentdrinkcheck) {
            comparedrink = 1;
        } else{
            comparedrink = 0;
        }
        return comparedrink;
    }



}
