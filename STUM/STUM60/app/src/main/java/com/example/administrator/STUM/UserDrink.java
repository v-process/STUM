package com.example.administrator.STUM;

/**
 * Created by Administrator on 2015-05-13.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;


public class UserDrink extends Activity {
     EditText edit;
     TextView text;

     CheckBox sports;
     CheckBox weather;
    int water_size_result = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); // 상단액션바 없애기
        setContentView(R.layout.userdrink);
        edit = (EditText)findViewById(R.id.weight_input);
        text = (TextView)findViewById(R.id.water_size);

        sports = (CheckBox)findViewById(R.id.sports);
        weather = (CheckBox)findViewById(R.id.weather);
        Button button1 = (Button)findViewById(R.id.button1);
        Button button2 = (Button)findViewById(R.id.button2);


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(water_size_result >= 0) {
                    upload(water_size_result);
                }
                else{
                    Toast.makeText(UserDrink.this, "계산을 해주세요!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void button1func(View v){
    String input = edit.getText().toString();
    if(input.equals("")){
        Toast.makeText(UserDrink.this, "체중을 입력해주세요!", Toast.LENGTH_SHORT).show();
        return;
    }
    water_size_result = Integer.parseInt(edit.getText().toString()) * 31;

    if(sports.isChecked()) {
        water_size_result = (water_size_result / 100) * 150;
    }
    if(weather.isChecked()) {
        water_size_result = (water_size_result / 100) * 110;
    }

    text.setText(Integer.toString(water_size_result));

}

    void upload(int water_size_result){
        ParseObject DrinkValues = new ParseObject("UserDrink");//파스 오브젝트 생성
        ParseUser user = ParseUser.getCurrentUser();
        DrinkValues.put("User", user);
        DrinkValues.put("Drink", water_size_result);

        DrinkValues.saveInBackground();
        Toast.makeText(UserDrink.this, "당신의 하루 물권장량은" + water_size_result +"ml 입니다.", Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(this, BluetoothMainActivity.class);
        startActivity(intent1);
        finish();

    }
}
