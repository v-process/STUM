package com.example.administrator.STUM;


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
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


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
            if (currentUser != null) { //로그인 되어있을때.
                // Send logged in users to Welcome.class
                //블루투스가 비활성화면 블투화면으로 보내주고 블투에서 네비로 보내는게 좋을듯.
                //여기다가 if문 넣어 블투가 비활성화면 블투화면 아니면 네비화면 이렇게 설정하는것으로..
               /* BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter != null) {
                    Log.d("화면전환때 블투아답터을때", "????");
                    if (mBluetoothAdapter.isEnabled() == false) {
                        Log.d("화면전환때 블투아답터을때", "블루투스가 비활성화임 블투설정으로 가자");
                        Intent intent = new Intent(MainActivity.this, BluetoothMainActivity.class);
                        startActivity(intent);
                    } else {
                        Log.d("화면전환때 블투아답터을때", "블루투스가 활성화되있음 메인으로 가자");
                        Intent intent = new Intent(MainActivity.this, NavActivity1.class);
                        startActivity(intent);

                    }*/

                Intent intent = new Intent(MainActivity.this, NavActivity1.class);
                startActivity(intent);

                Toast.makeText(getApplicationContext(),
                        "안녕하세요 " + struser + " 님",
                        Toast.LENGTH_LONG).show();
                finish();


            } else { //아싸리 로그인 안되어있음
                // Send user to LoginSignupActivity.class
                Intent intent = new Intent(MainActivity.this,
                        LoginSignupActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }
}