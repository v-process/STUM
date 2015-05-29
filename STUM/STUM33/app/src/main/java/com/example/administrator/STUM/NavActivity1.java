package com.example.administrator.STUM;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

//네비게이션 프레그먼트3
public class NavActivity1 extends NavBaseActivity {

    ViewPager viewPager;
    TapFragmentManager fragmentManger;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    EditText weight;
    CheckBox sports, weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity1);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml
        set(navMenuTitles, navMenuIcons);

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
            DialogFragment df = new WeightDialog();
            df.show(getFragmentManager(), "WEIGHT");

            return true;
        }

        return super.onOptionsItemSelected(item);
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
                default: return ViewPagerFragment1.newInstance();
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }


}
