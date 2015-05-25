package com.example.administrator.STUM;

/**
 * Created by Administrator on 2015-03-26.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ViewPagerFragment2 extends Fragment {

    public static ViewPagerFragment2 newInstance(){
        ViewPagerFragment2 fragment = new ViewPagerFragment2();
        return fragment;
    }


    public ViewPagerFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.viewpager_fragment2, container, false);
        return v;
    }


}
