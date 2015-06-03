package com.example.administrator.STUM;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPagerFragment6 extends Fragment {

    public static ViewPagerFragment6 newInstance(){
        ViewPagerFragment6 fragment = new ViewPagerFragment6();
        return fragment;
    }


    public ViewPagerFragment6() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewpager_fragment6, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.water_info);
        imageView.setAdjustViewBounds(true);
        return v;
    }
}
