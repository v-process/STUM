package com.example.administrator.slidertest;


import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, "ZfjR3Gbh9Ly5JJJTop2oHMr3gSg2C9tSD0NNSs8O", "bohAfTs7aO1PXYOcpc1ucvIi30Hhu1B0SNBzky8Y");

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }

}