package com.example.administrator.STUM;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2015-03-25.
 */

public class ViewPagerFragment1 extends Fragment {

    public static ViewPagerFragment1 newInstance() {
        ViewPagerFragment1 fragment = new ViewPagerFragment1();
        return fragment;
    }


    public ViewPagerFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewpager_fragment1, container, false);

        // Inflate the layout for this fragment
        return v;
    }
}