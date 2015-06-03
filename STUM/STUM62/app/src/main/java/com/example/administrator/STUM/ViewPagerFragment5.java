package com.example.administrator.STUM;

/**
 * Created by Administrator on 2015-03-26.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ViewPagerFragment5 extends Fragment {

    public static ViewPagerFragment5 newInstance(){
        ViewPagerFragment5 fragment = new ViewPagerFragment5();
        return fragment;
    }


    public ViewPagerFragment5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewpager_fragment5, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.water_info);
        imageView.setAdjustViewBounds(true);
        return v;
    }

}
