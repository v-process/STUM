package com.example.administrator.STUM;

/**
 * Created by Administrator on 2015-05-13.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;


public class UserDrink extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdrink);

        final EditText edit = (EditText)findViewById(R.id.weight_input);
        final TextView text = (TextView)findViewById(R.id.water_size);

        final CheckBox sports = (CheckBox)findViewById(R.id.sports);
        final CheckBox weather = (CheckBox)findViewById(R.id.weather);

        Button button = (Button)findViewById(R.id.button1);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = edit.getText().toString();
                if(input.equals("")){
                    Toast.makeText(UserDrink.this, "체중을 입력해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int water_size_result = Integer.parseInt(edit.getText().toString()) * 31;

                if(sports.isChecked()) {
                    water_size_result = (water_size_result / 100) * 150;
                }
                if(weather.isChecked()) {
                    water_size_result = (water_size_result / 100) * 110;
                }

                text.setText(Integer.toString(water_size_result)+"ml");

                upload(water_size_result);

            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
