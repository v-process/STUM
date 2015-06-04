package com.example.administrator.STUM;

/**
 * Created by Administrator on 2015-03-26.
 */

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


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

        BufferedReader in;
        Resources myResources = getResources();
        InputStream myFile = myResources.openRawResource(R.raw.info3);

        StringBuffer strBuffer = new StringBuffer();
        String str = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(myFile, "UTF-8"));  // file이 utf-8 로 저장되어 있다면 "UTF-8"
            while( (str = in.readLine()) != null)                      // file이 KSC5601로 저장되어 있다면 "KSC5601"
            {
                strBuffer.append(str + "\n");
            }
            in.close();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TextView tt = (TextView) v.findViewById(R.id.water_info1);
        tt.setText(strBuffer);

        return v;
    }

}
