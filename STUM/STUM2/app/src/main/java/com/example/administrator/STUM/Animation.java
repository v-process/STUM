package com.example.administrator.STUM;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;

public class Animation extends NavBaseActivity {

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    boolean go;
    SpriteView spriteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        spriteView = (SpriteView) findViewById(R.id.spriteView);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml
        set(navMenuTitles, navMenuIcons);
    }


    public void onBtnStart(View v) {
        spriteView.startAnimation();
    }

    public void onBtnStop(View v){
        spriteView.stopAnimation();
    }
}
