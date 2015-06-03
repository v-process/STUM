package com.example.administrator.STUM;

/**
 * Created by Administrator on 2015-05-22.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

/**
 * @author givenjazz
 */

public class RecycleUtils { //그냥 돌아가면서 정리해주는 코드인거같다.

    private RecycleUtils(){};

    public static void recursiveRecycle(View root) {
        if (root == null)
            return;

        // root.setBackgroundDrawable(null);		// Deprecated
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)root;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                recursiveRecycle(group.getChildAt(i));
            }

            if (!(root instanceof AdapterView)) {
                group.removeAllViews();
            }
        }

        if (root instanceof ImageView) {
            ((ImageView)root).setImageDrawable(null);
        }
        root = null;

        return;
    }
}
