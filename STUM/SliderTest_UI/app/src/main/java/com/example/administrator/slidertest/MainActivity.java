package com.example.administrator.slidertest;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
//처음 어플 실행시 로그인되어있는 유저인지 확인.
public class MainActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager nm =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);



        nm.cancel(9999);

//처음 시작시 로그인되어 있으면 바로 로그인 페이지 호출
        // Determine whether the current user is an anonymous user
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // If user is anonymous, send the user to LoginSignupActivity.class
            Intent intent = new Intent(MainActivity.this,
                    LoginSignupActivity.class);
            startActivity(intent);
            finish();
        } else {
            // If current user is NOT anonymous user
            // Get current user data from Parse.com
            ParseUser currentUser = ParseUser.getCurrentUser();
            String struser = currentUser.getUsername().toString();
            if (currentUser != null) {
                // Send logged in users to Welcome.class
                Intent intent = new Intent(MainActivity.this, NavActivity1.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),
                        "안녕하세요 " + struser + " 님",
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                // Send user to LoginSignupActivity.class
                Intent intent = new Intent(MainActivity.this,
                        LoginSignupActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }
}
