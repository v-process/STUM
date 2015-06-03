package com.example.administrator.STUM;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
    int SPLASH_TIME=2000;
    private BluetoothAdapter btAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); //splash이미지 레이아웃

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.disable();//끄기

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish(); //현재 액티비티 즉 SplashActivity 종료

                //페이드 인 페이드 아웃 효과 res/anim/fadein, fadeout xml을 만들어 줘야 합니다.
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        };
        handler.sendEmptyMessageDelayed(0,2000); //2000 시간 설정 1000->1초
    }

    public void onBackPressed(){} //splash 이미지 띄우는 과정에 백 버튼을 누를 수도 있다. 백버튼 막기
}