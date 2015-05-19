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
public class ViewPagerFragment4 extends Fragment {

    public static ViewPagerFragment4 newInstance(){
        ViewPagerFragment4 fragment = new ViewPagerFragment4();
        return fragment;
    }


    public ViewPagerFragment4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.viewpager_fragment4, container, false);
    }


}
