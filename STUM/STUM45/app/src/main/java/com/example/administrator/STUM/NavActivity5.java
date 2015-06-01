package com.example.administrator.STUM;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParseUser;

//네비게이션 프레그먼트2
public class NavActivity5 extends NavBaseActivity implements AdapterView.OnItemClickListener {

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private String data[] = { "블루투스 연결", "로그아웃", "개발자 소개"};

    ListView list;
    EditText edit;
    ArrayAdapter<String> array;


    //리스트 아이템 클릭 포지션 0과 1에서 선택에 따른 엑엑티비티구현.
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if (position == 0) {
            Intent intent0 = new Intent(NavActivity5.this, BluetoothMainActivity.class);
            startActivity(intent0);
        }

        else if (position == 1) {
            ParseUser.logOut();
            Intent intent1 = new Intent(NavActivity5.this, LoginSignupActivity.class);
            startActivity(intent1);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity5);

        //네비게이션 타이틀과 아이콘 받아오기.
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml

        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml
        //타이틀과 아이콘 적용.
        set(navMenuTitles,navMenuIcons);


        //뷰페이져에 리스트 추가.
        list=(ListView)findViewById(R.id.ListView01);
        array =  new ArrayAdapter<String>(this, R.layout.simple_listview, data);
        list.setAdapter(array);
        list.setOnItemClickListener(this);
        //리스너 등록끝.

        // Retrieve current user from Parse.com
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_two, menu);
        return true;
    }


    //네비게이션 바 옵션 아이템 번호 가져오기.
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
