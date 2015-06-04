package com.example.administrator.STUM;

/**
 * Created by Administrator on 2015-03-26.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ViewPagerFragment2 extends Fragment {

    GIFView temp;
    Button gifTest;

    private TimerTask mTask;
    private Timer mTimer;

    String strM;

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
        View v = inflater.inflate(R.layout.viewpager_fragment2, container, false);
        temp = (GIFView) v.findViewById(R.id.gifgif);

        /*
        gifTest = (Button) v.findViewById(R.id.gifTest);
        gifTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp.setGIFResource(R.drawable.character);
            }
        });
        */


        mTask = new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdfnow = new SimpleDateFormat("yyyyMMddHHmmss");
                strM = sdfnow.format(date);

                ParseUser user = ParseUser.getCurrentUser();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Character");
                query.addDescendingOrder("createdAt");
               //query.whereEqualTo("User", user);

                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (object == null) {

                        } else {
                            String tempStr = (String) object.get("characterStatusCheck");
                            String flag = (String) object.get("drinkflag");

                            if(flag.equals("D")) {
                                temp.setGIFResource(R.drawable.character);
                            }
                            else {
                                temp.setGIFResource(R.drawable.character0);
                            }

                        }
                    }
                });

            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTask, 0, 3000);//0초 후에 Task를 실행하고 3초마다 반복 해라.


        return v;
    }

}
