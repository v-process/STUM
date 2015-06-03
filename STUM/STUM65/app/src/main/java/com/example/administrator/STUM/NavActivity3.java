package com.example.administrator.STUM;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

//로그인 성공시 리스트뷰가 보여짐
public class NavActivity3 extends NavBaseActivity {

    ViewPager viewPager;
    TapFragmentManager fragmentManger;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

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


    // TapFragmentManager Class
    public class TapFragmentManager extends FragmentPagerAdapter {
        public TapFragmentManager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int tab_position) {
            switch(tab_position) {
                case 0: return ViewPagerFragment3.newInstance();
                case 1: return ViewPagerFragment4.newInstance();
                case 2: return ViewPagerFragment5.newInstance();
                case 3: return ViewPagerFragment6.newInstance();
                case 4: return ViewPagerFragment7.newInstance();
                default: return ViewPagerFragment3.newInstance();
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
