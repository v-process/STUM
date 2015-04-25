package com.example.administrator.slidertest;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;


public class ChartActivity extends NavBaseActivity {

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    ViewPager viewPager;
    TapFragmentManager fragmentManger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        viewPager = (ViewPager) findViewById(R.id.pager_chart);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml
        set(navMenuTitles,navMenuIcons);

        fragmentManger = new TapFragmentManager(getSupportFragmentManager());
        viewPager.setAdapter(fragmentManger);

    }


    // TapFragmentManager Class ***************************************
    public class TapFragmentManager extends FragmentPagerAdapter {
        public TapFragmentManager(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int tab_position) {
            switch(tab_position) {
                case 0: return ChartFragment.newInstance();
                default: return ChartFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 1;
        }
    }
}
