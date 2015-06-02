package com.example.administrator.STUM;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPagerFragment7 extends Fragment {

    public static ViewPagerFragment7 newInstance(){
        ViewPagerFragment7 fragment = new ViewPagerFragment7();
        return fragment;
    }


    public ViewPagerFragment7() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewpager_fragment7, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.water_info);
        imageView.setAdjustViewBounds(true);
        return v;
    }
}
