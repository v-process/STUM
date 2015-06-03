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

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeAlarm extends BroadcastReceiver {

    NotificationManager nm;
    int comparedrink = 0;// 기본값 선언

    Context mContext;//receive에 있는 콘테스트 값 받기.

    //UserDataAnalysis userDataAnalysis = new UserDataAnalysis();
    ParseObject userAnalysis = new ParseObject("UserDataAnalysis");

    Calendar now = Calendar.getInstance();
    int date = now.get(Calendar.DATE);
    int hour = now.get(Calendar.HOUR_OF_DAY);


    int total;
    int i = 0;
    int time;
    ArrayList<Integer> arrayAverage = new ArrayList<Integer>();


    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        time = timecheck();
        averageflag = 0;
        hour = now.get(Calendar.HOUR_OF_DAY);
        date = now.get(Calendar.DATE);

        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DrunkTable");
        query.whereEqualTo("User", user);
        time = timecheck();
        int hour_1= hour - time;
        query.whereEqualTo("day", 3);
        query.addDescendingOrder("createdAt");
        query.whereEqualTo("hour", 4);

        //query.whereLessThanOrEqualTo("hour", 4);//최대 현재 시간 까지
        //query.whereGreaterThanOrEqualTo("hour", hour-1);//최소 현재시간-1 까지
        query.setLimit(3);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> object, ParseException e) {
                if (object == null) {
                    //         ParseUser user = ParseUser.getCurrentUser();
//                    userAnalysis.put("User", user);
//                    userAnalysis.saveInBackground();

                } else {
                    for (int i = 0; i < object.size(); i++) {
                        ParseObject course = object.get(i);

                        total = (int) course.get("drunk");
                        sum(total);
                        flagfunc();

                    }
                    int averageresult = sum(total);

                    time = timecheck();

                    //int divide = flagfunc();
                    int divide =  object.size();

//                    total = (int) object.get("drunk");
//                    total += total;
                    ParseUser user = ParseUser.getCurrentUser();
                    Compare();
                    time = timecheck();
                    int hour_1= hour - time;

                    userAnalysis.put("User", user);
                    userAnalysis.put("averagedrunk", averageresult);
                    userAnalysis.saveInBackground();
                    Compare2();
                }
            }
        });        /*
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
        //  userDataAnalysis.da();
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> notiquery = ParseQuery.getQuery("DrunkTable");
        notiquery.addDescendingOrder("createdAt");
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
                    // int averagedrinkcheck;
                    currentdrinkcheck = (int) object.get("drunk");
                    //averagedrinkcheck = (int) object.get("averagedrink");

                    //int k =  compare2(averagedrinkcheck, currentdrinkcheck);

//                    if(currentdrinkcheck<=userDataAnalysis.da()){
//                        noti(currentdrinkcheck, userDataAnalysis.da());
//                    }

                    userAnalysis.put("todaywater", currentdrinkcheck);
                    userAnalysis.saveInBackground();


                }
            }
        });
    }

    void Compare2() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> notiquery = ParseQuery.getQuery("UserDataAnalysis");
        notiquery.addDescendingOrder("createdAt");
        notiquery.whereEqualTo("User", user);
        notiquery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("score", "The getFirst request failed.");
                    //Toast.makeText(TimeAlarm.this., "값 안받아져", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("score", "Retrieved the object.");
                    //Toast.makeText(ViewPagerFragment1.this.getActivity(), "값 받기 성공", Toast.LENGTH_SHORT).show();
                    int todaywater1;
                    int averagewater;

                    if(null == object.get("todaywater")) {
                        Compare2();
                    }
                    else {
                        averagewater = (int) object.get("averagedrunk");
                        todaywater1 = (int) object.get("todaywater");

                        //averagedrinkcheck = (int) object.get("averagedrink");

                        //int k =  compare2(averagedrinkcheck, currentdrinkcheck);

                        if (todaywater1 <= averagewater) {

                            noti(todaywater1, averagewater/(3));
                        }
                    }

                }
            }
        });
    }


    void noti(int currentdrinkcheck, int averagedrinkcheck){
        nm = (NotificationManager) this.mContext.getSystemService(this.mContext.NOTIFICATION_SERVICE);//노티 등록
        // nm.cancel(1);//누르고 없애기
        CharSequence from = date+ "일 " + hour +"시 알람입니다.";//제목
        CharSequence message = "오늘 마신물 : " + currentdrinkcheck +  "  평균 마시는 물 : " + averagedrinkcheck;
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);

        @SuppressWarnings("deprecation")
        Notification notif = new Notification(R.drawable.status3,"STUM 알람입니다." , System.currentTimeMillis());

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
    public void da() {
        time = timecheck();

        hour = now.get(Calendar.HOUR_OF_DAY);
        date = now.get(Calendar.DATE);

        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DrunkTable");
        //query.whereEqualTo("User", user);
        query.whereEqualTo("day", date-1);

        //query.whereGreaterThan("hour", hour);
        query.whereLessThan("hour",hour-time);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {

                } else {
                    time = timecheck();
                    total = (int) object.get("drunk");
                    total += total;

                    ParseUser user = ParseUser.getCurrentUser();
                    userAnalysis.put("User", user);
                    userAnalysis.put("averagedrunk", total);
                    userAnalysis.saveInBackground();
                    Compare2();
                }
            }
        });
        // return total;
    }

    public int timecheck(){
        if(hour <= 3) {
            i = 3;
        }
        else if(hour <= 5){
            i = 5;
        }
        else if(hour <= 7){
            i = 7;
        }
        else if(hour <= 11){
            i = 4;
        }
        else if(hour <= 21){
            i = 5;
        }
        else if(hour <= 23){
            i = 2;
        }
        else{
            i = 2;
        }
        return i;
    }
    int averagesum;
    int averageflag = 0;
    public int sum(int total){
        averageflag++;
        averagesum += total;
        return averagesum;
    }

    public int flagfunc(){
        return averageflag;
    }


//    int compare2(int averagedrinkcheck, int currentdrinkcheck) {
//        if (userDataAnalysis.da() >= currentdrinkcheck) {
//            comparedrink = 1;
//        } else{
//            comparedrink = 0;
//        }
//        return comparedrink;
//    }
//


}