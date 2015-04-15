package com.example.administrator.slidertest;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

public class MainActivity extends BaseActivity implements ActionBar.TabListener{

    ViewPager viewPager;
    TapFragmentManager fragmentManger;

    ActionBar actionBar;
    String[] tabName = {"물", "물과 건강", "기타"};

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml

        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml

        set(navMenuTitles,navMenuIcons);

        viewPager = (ViewPager) findViewById(R.id.pager);

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        fragmentManger = new TapFragmentManager(getSupportFragmentManager());
        viewPager.setAdapter(fragmentManger);

        for (String str : tabName) {
            actionBar.addTab(actionBar.newTab().setText(str).setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int tabPosition) {
                actionBar.setSelectedNavigationItem(tabPosition);

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

    @Override
    public void onTabReselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction arg1) {
        // TODO Auto-generated method stub
        viewPager.setCurrentItem(tab.getPosition());
        // viewPager.notifyAll();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
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
                case 0: return TextFragment.newInstance();
                case 1: return ImageFragment.newInstance();
                case 2: return ButtonFragment.newInstance();
                default: return ButtonFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tabName.length;
        }
    }
}
