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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

//네비게이션 프레그먼트3
public class NavActivity1 extends NavBaseActivity {
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    TextView temp;
    TextView drink;
    TextView drink2;
    ImageView image1;
    ImageView image2;
    int userdrink;
    int currentdrink;
    int current;
    int total;

    private static final String TAG = "NavActivity1";
    private static final int DLG_WEIGHT = 0;
    private static final int TEXT_ID = 0;


    ParseObject usercalculate = new ParseObject("Calculate");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity1);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml
        set(navMenuTitles, navMenuIcons);
        temp = (TextView) findViewById(R.id.temp_view);
        drink = (TextView) findViewById(R.id.drink_view);
        drink2 = (TextView) findViewById(R.id.drink_view2);

        image1 = (ImageView) findViewById(R.id.char_1);
        //노티피케이션 버튼
        Button notificationButton = (Button) findViewById(R.id.notification_btn);

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notify("Title: 안녕하세요",
                        "통지 메세지입니다.");
            }
        });
//현재 물의 온도 및 마신양 가져오는 함수 호출.

        getuserdata();
        getuserdrink();
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
                    Log.d("score", "The getFirst request failed.");
                    Toast.makeText(getBaseContext(), "calculate 테이블 없네", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("score", "Retrieved the object.");
                    Toast.makeText(getBaseContext(), "calculate 테이블 있네", Toast.LENGTH_SHORT).show();

                    int a = (int) object.get("userdrink");
                    int b = (int) object.get("currentdrink");
                    drink2.setText(String.valueOf(userdrink));

                    divide(a , b);

                }
            }
        });

    }
    void divide(int a, int b){
        Toast.makeText(getBaseContext(), a + " "+ b + "있네", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getBaseContext(), "User drink 없음", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(NavActivity1.this, UserDrink.class);
                    startActivity(intent1);
                    finish();
                } else {
                    Log.d("score", "Retrieved the object.");
                    Toast.makeText(getBaseContext(), "User drink 있어", Toast.LENGTH_SHORT).show();

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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ChartTest");
        // query.whereEqualTo("User", user);
        // query.orderByAscending("createdAt");

        query.addDescendingOrder("createdAt");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("score", "The getFirst request failed.");
                    Toast.makeText(getBaseContext(), "온도와 마신물 없나바", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d("score", "Retrieved the object.");
                    Toast.makeText(getBaseContext(), "물양이랑 온도 있나바 : ", Toast.LENGTH_SHORT).show();

                    int temp_1;
                    temp_1 = (int) object.get("drink");
                    currentdrink = (int) object.get("temp");

                    charimage(currentdrink);

                    temp.setText(String.valueOf(temp_1));
                    drink.setText(String.valueOf(currentdrink));
                    usercalculate.put("currentdrink", currentdrink);

                    usercalculate.saveInBackground();
                }
            }
        });
    }


//이미지 세팅 함수.
    void charimage(int drink_1){
        if (drink_1 < 50){
            //image1.setImageResource(R.drawable.screaming_android);
        }
    }


    @SuppressWarnings("deprecation")
    private void Notify(String notificationTitle, String notificationMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

        @SuppressWarnings("deprecation")
        Notification notification = new Notification(R.drawable.status3,
                "새로운 메시지입니다.", System.currentTimeMillis());

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notification.setLatestEventInfo(this, notificationTitle,
                notificationMessage, pendingIntent);
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
        date.show(getSupportFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Toast.makeText(
                    NavActivity1.this,
                    String.valueOf(year) + "-" + String.valueOf(monthOfYear)
                            + "-" + String.valueOf(dayOfMonth),
                    Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            showDialog(DLG_WEIGHT);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("체중 변경");
        builder.setMessage("체중을 자주 갱신해 주세요.");

        // Use an EditText view to get user input.
        final EditText input = new EditText(this);
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
