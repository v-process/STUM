package com.example.administrator.STUM;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    TextView temp;
    TextView drink;
    TextView drink2;
    ImageView image1;
    ImageView image2;
    ViewPager viewPager;

    EditText weight;
    CheckBox sports, weather;

    ParseObject usercalculate = new ParseObject("Calculate");
    ImageView imageview;
    ImageView imageview2;
    int imageArray[] = {R.drawable.water0, R.drawable.water1,
            R.drawable.water2, R.drawable.water3, R.drawable.water4,
            R.drawable.water5, R.drawable.water6, R.drawable.water7,
            R.drawable.water8, R.drawable.water9, R.drawable.water10};

    int imageArray2[] = {R.drawable.circle_red, R.drawable.circle_yellow,
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
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml
        //NavBaseActivity.set(navMenuTitles, navMenuIcons);
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

        //현재 물의 온도 및 마신양 가져오는 함수 호출.
        getuserdata();
        //사용자 별 마실 목표 가져오기
        getuserdrink();
        //마신물과 목표 한번에 가져오기
        Calculate();

        return v;
    }

    @Override
    public void onClick(View v) {

                //현재 물의 온도 및 마신양 가져오는 함수 호출.
                getuserdata();
                //사용자 별 마실 목표 가져오기
                getuserdrink();
                //마신물과 목표 한번에 가져오기
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
                        //현재 물의 온도 및 마신양 가져오는 함수 호출.
                        getuserdata();
                        //사용자 별 마실 목표 가져오기
                        getuserdrink();
                        //마신물과 목표 한번에 가져오기
                        Calculate();
                        return;
                    }
                    target = (int) object.get("userdrink");
                    currentdrinkwater = (int) object.get("currentdrink");

                    String ml = String.valueOf(target);
                    drink2.setText(ml);
                    divide(target, currentdrinkwater);
                }
            }
        });
    }

    //물의 온도에 따른 이미지 변경
    void tempimagechange(double temp_1) {
        if (temp_1 >= 95) {//물온도가 95도 이상일때
            imageview2.setImageResource(imageArray2[0]);
        } else if (temp_1 <= 7) {//물의 온도가 7도 이하일때
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

                    drink2.setText(String.valueOf(userdrink));
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
        calendar1.set(Calendar.HOUR_OF_DAY, 18);
        calendar1.set(Calendar.MINUTE, 58);
        calendar1.set(Calendar.SECOND, 0);
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
    //현재 물의 온도 및 마신양 가져오는 함수 정의.
    void getuserdata() {
        ParseUser user = ParseUser.getCurrentUser();
        //서버에서 이미지 받아오는 곳.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("testDrunk");
        // query.whereEqualTo("User", user);
        // query.orderByAscending("createdAt");

        query.addDescendingOrder("createdAt");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("score", "The getFirst request failed.");
                    //Toast.makeText(ViewPagerFragment1.this.getActivity(), "온도와 마신물 없나바", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("score", "Retrieved the object.");
                    //Toast.makeText(ViewPagerFragment1.this.getActivity(), "물양이랑 온도 있나바 : ", Toast.LENGTH_SHORT).show();

                    double temp_1;
                    int drink_1;
                    temp_1 = (double) object.get("watertemp");
                    drink_1 = (int) object.get("drunk");
                    tempimagechange(temp_1);
                    usercalculate.put("currentdrink", drink_1);
                    usercalculate.saveInBackground();

                    temp.setText(String.valueOf(temp_1) + "°C");
                    drink.setText(String.valueOf(drink_1));

                    if(drink_1 < userDataAnalysis.da() ) {
                        Notify("STUM", "물 마실 시간이에요!!!!");
                    }

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

