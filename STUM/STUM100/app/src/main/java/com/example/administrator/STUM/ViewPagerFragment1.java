package com.example.administrator.STUM;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015-03-25.
 */

public class ViewPagerFragment1 extends Fragment implements View.OnClickListener {

    TextView temp;
    TextView drink;
    TextView drink2;

    ParseObject usercalculate = new ParseObject("Calculate");
    ImageView imageview;
    ImageView imageview2;
    int imageArray[] = {R.drawable.water0, R.drawable.water1,
            R.drawable.water2, R.drawable.water3, R.drawable.water4,
            R.drawable.water5, R.drawable.water6, R.drawable.water7,
            R.drawable.water8, R.drawable.water9, R.drawable.water10};

    int imageArray2[] = {R.drawable.circle_red, R.drawable.circle_green,
            R.drawable.circle_sky};

    private TimerTask mTask;
    private Timer mTimer;

    UserDataAnalysis userDataAnalysis = new UserDataAnalysis();

    AlarmManager am1;

    public static ViewPagerFragment1 newInstance() {
        ViewPagerFragment1 fragment = new ViewPagerFragment1();
        return fragment;
    }


    public ViewPagerFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.viewpager_fragment1, container, false);
        temp = (TextView) v.findViewById(R.id.temp_view);
        drink = (TextView) v.findViewById(R.id.drink_view);
        drink2 = (TextView) v.findViewById(R.id.drink_view2);
        imageview = (ImageView) v.findViewById(R.id.water_size);
        imageview2 = (ImageView) v.findViewById(R.id.temp_image);

        am1 = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        setOneTimeAlarm();
        setOneTimeAlarm2();
        setOneTimeAlarm3();
        setOneTimeAlarm4();

        //새로고침버튼
        Button refreshButton = (Button) v.findViewById(R.id.refresh_btn);
        refreshButton.setOnClickListener(this);
        /*
        getuserdrink();
        getuserdata();
        Calculate();
        */

        mTask = new TimerTask() {
            @Override
            public void run() {
                getuserdrink();
                getuserdata();
                Calculate();

            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 0, 3000);//0초 후에 Task를 실행하고 3초마다 반복 해라.

        return v;
    }

    @Override
    public void onClick(View v) {
        getuserdrink();
        getuserdata();
        Calculate();
    }

    void Calculate() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Calculate");
        query.addDescendingOrder("createdAt");
        query.whereEqualTo("User", user);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    //Log.d("score", "The getFirst request failed.");
                    //Toast.makeText(ViewPagerFragment1.this.getActivity(), "calculate 테이블 없네", Toast.LENGTH_SHORT).show();

                } else {
                    //Log.d("score", "Retrieved the object.");
                    //Toast.makeText(ViewPagerFragment1.this.getActivity(), "calculate 테이블 있네", Toast.LENGTH_SHORT).show();
                    int currentdrinkwater;
                    int target;
                    if(null == object.get("userdrink")) {
                        target = (int) object.get("userdrink");
                        Toast.makeText(getActivity(), "잠시 기다려 주세요", Toast.LENGTH_SHORT).show();
                        getuserdrink();
                        getuserdata();
                        Calculate();
                        return;
                    }
                    else {
                        target = (int) object.get("userdrink");
                        currentdrinkwater = (int) object.get("currentdrink");

                        String ml = String.valueOf(target);
                        drink2.setText(ml);
                        divide(target, currentdrinkwater);

                    }

                }
            }
        });
    }

    //물의 온도에 따른 이미지 변경
    void tempimagechange(double temp_1) {
        if (temp_1 >= 40) {//물온도가 95도 이상일때
            imageview2.setImageResource(imageArray2[0]);
        } else if (temp_1 <= 15) {//물의 온도가 7도 이하일때
            imageview2.setImageResource(imageArray2[2]);
        } else {
            imageview2.setImageResource(imageArray2[1]);
        }

    }

    //물양 이미지 지정 함수
    void divide(int target, int current) {
        if (current >= target) {
            imageview.setImageResource(imageArray[10]);
//            Notify("오늘의 마실물 달성", "축하합니다.");

        } else {
            for (int i = 10; i > 0; i--) {
                if (target * (i * 0.1) >= current && current >= target * ((i * 0.1) - 0.1)) {
                    imageview.setImageResource(imageArray[i - 1]);
                }
            }
        }
    }

    void getuserdrink() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserDrink");
        query.addDescendingOrder("createdAt");
        query.whereEqualTo("User", user);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Intent intent1 = new Intent(getActivity(), UserDrink.class);
                    startActivity(intent1);
                    getActivity().finish();
                } else {
                    int userdrink;
                    userdrink = (int) object.get("Drink");

                    //drink2.setText(String.valueOf(userdrink));
                    ParseUser user = ParseUser.getCurrentUser();
                    usercalculate.put("User", user);
                    usercalculate.put("userdrink", userdrink);
                    usercalculate.saveInBackground();
                }
            }
        });
    }
    public void setOneTimeAlarm() {
        Intent intent = new Intent(getActivity(), TimeAlarm.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getActivity(), 1,intent, PendingIntent.FLAG_UPDATE_CURRENT );

        Calendar calendar1 = Calendar.getInstance(); //7시
        calendar1.set(Calendar.HOUR_OF_DAY, 17);
        calendar1.set(Calendar.MINUTE, 11);
        calendar1.set(Calendar.SECOND, 30);
        long current_time = System.currentTimeMillis();
        long limit_time1 = calendar1.getTimeInMillis();
        if (current_time <= limit_time1){
            am1.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), 1000*60*60*4,  pendingIntent1);
        }

    }
    public void setOneTimeAlarm2() {
        Intent intent2 = new Intent(getActivity(), TimeAlarm.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getActivity(), 2,intent2, PendingIntent.FLAG_UPDATE_CURRENT );

        Calendar calendar2 = Calendar.getInstance(); //11시
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 43);
        calendar2.set(Calendar.SECOND, 30);
        long current_time = System.currentTimeMillis();
        long limit_time2 = calendar2.getTimeInMillis();
        if (current_time <= limit_time2){
            am1.setRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), 1000*60*60*4,  pendingIntent2);
        }
    }

    public void setOneTimeAlarm3() {
        Intent intent = new Intent(getActivity(), TimeAlarm.class);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(getActivity(), 3,intent, PendingIntent.FLAG_UPDATE_CURRENT );

        Calendar calendar3 = Calendar.getInstance(); //16시
        calendar3.set(Calendar.HOUR_OF_DAY, 0);
        calendar3.set(Calendar.MINUTE, 44);
        calendar3.set(Calendar.SECOND, 0);
        long current_time = System.currentTimeMillis();
        long limit_time3 = calendar3.getTimeInMillis();
        if (current_time <= limit_time3){
            am1.setRepeating(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(), 1000*60*60*4,  pendingIntent3);
        }

    }
    public void setOneTimeAlarm4() {
        Intent intent = new Intent(getActivity(), TimeAlarm.class);
        PendingIntent pendingIntent4 = PendingIntent.getBroadcast(getActivity(), 4,intent, PendingIntent.FLAG_UPDATE_CURRENT );

        Calendar calendar4 = Calendar.getInstance(); //21시
        calendar4.set(Calendar.HOUR_OF_DAY, 0);
        calendar4.set(Calendar.MINUTE, 45);
        calendar4.set(Calendar.SECOND, 0);
        long current_time = System.currentTimeMillis();
        long limit_time4 = calendar4.getTimeInMillis();
        if (current_time <= limit_time4){
            am1.setRepeating(AlarmManager.RTC_WAKEUP, calendar4.getTimeInMillis(), 1000*60*60*4,  pendingIntent4);
        }

    }


    void getuserdata() {
        ParseUser user = ParseUser.getCurrentUser();
        //서버에서 이미지 받아오는 곳.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("testDrunk3");//3아닌거에 했었지
        query.whereEqualTo("User", user);
        //query.whereEqualTo("day",4);
        //query.whereEqualTo("currentflag","T");
        //query.orderByAscending("hour");

        query.addDescendingOrder("createdAt");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("score", "The getFirst request failed.");
                    Toast.makeText(ViewPagerFragment1.this.getActivity(), "온도와 마신물 없나바", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("score", "Retrieved the object.");
                    //Toast.makeText(ViewPagerFragment1.this.getActivity(), "물양이랑 온도 있나바 : ", Toast.LENGTH_SHORT).show();

                    int drink_1;
                    drink_1 = (int) object.get("drunk");
                    usercalculate.put("currentdrink", drink_1);
                    usercalculate.saveInBackground();

                    drink.setText(String.valueOf(drink_1));

                    if(drink_1 < userDataAnalysis.da() ) {
                        Notify("STUM", "물 마실 시간이에요!!!!");
                    }

                }
            }
        });

        //서버에서 이미지 받아오는 곳.
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Character");//3아닌거에 했었지
        //query2.whereEqualTo("User", user);

        query2.addDescendingOrder("createdAt");
        query2.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("score", "The getFirst request failed.");
                    Toast.makeText(ViewPagerFragment1.this.getActivity(), "온도와 마신물 없나바222", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("score", "Retrieved the object.");
                    //Toast.makeText(ViewPagerFragment1.this.getActivity(), "물양이랑 온도 있나바 : ", Toast.LENGTH_SHORT).show();

                    Number temp_2;
                    double temp_1;
                    temp_2 =  object.getNumber("temp");
                    temp_1 = temp_2.doubleValue();
                    tempimagechange(temp_1);
                    usercalculate.saveInBackground();

                    temp.setText(String.valueOf(temp_1) + "°C");

                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void Notify(String notificationTitle, String notificationMessage) {
        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

        @SuppressWarnings("deprecation")
        Notification notification = new Notification(R.drawable.status3, "새로운 메시지입니다.", System.currentTimeMillis());
        //notification.vibrate = new long[]{100, 200, 100, 500};
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        Intent notificationIntent = new Intent(getActivity(), MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(getActivity(), notificationTitle, notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);
    }
}
