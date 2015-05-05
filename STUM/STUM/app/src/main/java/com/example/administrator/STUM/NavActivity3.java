package com.example.administrator.STUM;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

//로그인 성공시 리스트뷰가 보여짐
public class NavActivity3 extends NavBaseActivity {

    ViewPager viewPager;
    TapFragmentManager fragmentManger;

    //뷰페이져에 나오는 이름 탭네임 등록.
    String[] tabName = {"물", "물과 건강", "기타"};

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private static final String TAG = "NavActivity1";
    private static final int DLG_WEIGHT = 0;
    private static final int TEXT_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity3);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml
        set(navMenuTitles,navMenuIcons);

        viewPager = (ViewPager) findViewById(R.id.pager);

        fragmentManger = new TapFragmentManager(getSupportFragmentManager());
        viewPager.setAdapter(fragmentManger);

        viewPager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int tabPosition) {
                //actionBar.setSelectedNavigationItem(tabPosition);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
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
                    NavActivity3.this,
                    String.valueOf(year) + "-" + String.valueOf(monthOfYear)
                            + "-" + String.valueOf(dayOfMonth),
                    Toast.LENGTH_LONG).show();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
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


    // TapFragmentManager Class
    public class TapFragmentManager extends FragmentPagerAdapter {
        public TapFragmentManager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int tab_position) {
            switch(tab_position) {
                case 0: return ViewPagerFragment1.newInstance();
                case 1: return ViewPagerFragment2.newInstance();
                case 2: return ViewPagerFragment3.newInstance();
                default: return ViewPagerFragment3.newInstance();
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tabName.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabName[position];
        }
    }
}
