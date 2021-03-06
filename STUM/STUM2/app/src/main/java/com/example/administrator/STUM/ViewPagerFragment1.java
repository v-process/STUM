package com.example.administrator.STUM;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

    private static final String TAG = "NavActivity1";
    private static final int DLG_WEIGHT = 0;
    private static final int TEXT_ID = 0;


    ParseObject usercalculate = new ParseObject("Calculate");
    ImageView imageview;
    int imageArray[] = {R.drawable.water0, R.drawable.water1,
            R.drawable.water2, R.drawable.water3, R.drawable.water4,
            R.drawable.water5, R.drawable.water6, R.drawable.water7,
            R.drawable.water8, R.drawable.water9, R.drawable.water10};

    private TimerTask mTask;
    private Timer mTimer;


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

        //노티피케이션 버튼
        Button notificationButton = (Button) v.findViewById(R.id.notification_btn);
        notificationButton.setOnClickListener(this);
//새로고침버트
        Button refreshButton = (Button) v.findViewById(R.id.refresh_btn);
        refreshButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //현재 물의 온도 및 마신양 가져오는 함수 호출.
                getuserdata();
                //사용자 별 마실 목표 가져오기
                getuserdrink();
                //마신물과 목표 한번에 가져오기
                Calculate();
            }
        });
        //현재 물의 온도 및 마신양 가져오는 함수 호출.
        getuserdata();
        //사용자 별 마실 목표 가져오기
        getuserdrink();
        //마신물과 목표 한번에 가져오기
        Calculate();
/*
        mTask = new TimerTask() {
            @Override
            public void run() {
                //현재 물의 온도 및 마신양 가져오는 함수 호출.
                getuserdata();
                //사용자 별 마실 목표 가져오기
                getuserdrink();
                //마신물과 목표 한번에 가져오기
                Calculate();
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 1500, 3000);//0초 후에 Task를 실행하고 3초마다 반복 해라.
        */


        // Inflate the layout for this fragment
        return v;
    }



    @Override
    public void onClick(View v) {
        Notify("Title: 안녕하세요", "통지 메세지입니다.");
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
                    target = (int) object.get("userdrink");
                    currentdrinkwater = (int) object.get("currentdrink");

                    String ml = String.valueOf(target);
                    drink2.setText(ml);
                    divide(target, currentdrinkwater);
                }
            }
        });
    }


    //물양 이미지 지정 함수
    void divide(int target, int current) {
        if(current >= target) {
            imageview.setImageResource(imageArray[10]);
//            Notify("오늘의 마실물 달성", "축하합니다.");

        }
        else {
            for (int i = 10; i > 0; i--) {
                if (target * (i * 0.1) >= current && current >= target * ((i * 0.1) - 0.1)) {
                    imageview.setImageResource(imageArray[i-1]);
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
                    Log.d("score", "The getFirst request failed.");
                    //Toast.makeText(ViewPagerFragment1.this.getActivity(), "User drink 없음", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(getActivity(), UserDrink.class);
                    startActivity(intent1);
                    getActivity().finish();
                }
                else {
                    Log.d("score", "Retrieved the object.");
                   // Toast.makeText(ViewPagerFragment1.this.getActivity(), "User drink 있어", Toast.LENGTH_SHORT).show();
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

    //현재 물의 온도 및 마신양 가져오는 함수 정의.
    void getuserdata(){
        ParseUser user = ParseUser.getCurrentUser();
        //서버에서 이미지 받아오는 곳.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("dataTestMH");
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
                    drink_1 = (int) object.get("watervolume");

                    usercalculate.put("currentdrink", drink_1);
                    usercalculate.saveInBackground();

                    temp.setText(String.valueOf(temp_1) + "°C");
                    drink.setText(String.valueOf(drink_1));

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

        Intent notificationIntent = new Intent(getActivity(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(getActivity(), notificationTitle, notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(ViewPagerFragment1.this.getActivity().getSupportFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Toast.makeText(
                    ViewPagerFragment1.this.getActivity(),
                    String.valueOf(year) + "-" + String.valueOf(monthOfYear)
                            + "-" + String.valueOf(dayOfMonth),
                    Toast.LENGTH_LONG).show();
        }
    };

    //@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.datePicker) {
            showDatePicker();
            return true;
        }

        if (id == R.id.weight_change) {
            getActivity().showDialog(DLG_WEIGHT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DLG_WEIGHT:
                return createExampleDialog();
            default:
                return null;
        }
    }
    protected void onPrepareDialog(int id, Dialog dialog){
        switch(id) {
            case DLG_WEIGHT:
                EditText weight = (EditText) dialog.findViewById(TEXT_ID);
                weight.setText("");
                break;
        }
    }

    private Dialog createExampleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewPagerFragment1.this.getActivity());
        builder.setTitle("체중 변경");
        builder.setMessage("체중을 자주 갱신해 주세요.");

        // Use an EditText view to get user input.
        final EditText input = new EditText(ViewPagerFragment1.this.getActivity());
        input.setId(TEXT_ID);
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Log.d(TAG, "WEIGHT_CHANGE: " + value);
                return;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        return builder.create();

    }
}