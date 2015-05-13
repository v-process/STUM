package com.example.administrator.STUM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
            image1.setImageResource(R.drawable.screaming_android);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
