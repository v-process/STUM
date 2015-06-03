package com.example.administrator.STUM;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;

public class UserDataAnalysis {

    int total;
    ParseObject userAnalysis = new ParseObject("UserDataAnalysis");
    Calendar now = Calendar.getInstance();
    int hour = now.get(Calendar.HOUR_OF_DAY);

    int i = 0;
    int time;
    UserDataAnalysis() {
    }

    public int da() {
        time = timecheck();

        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("dataTestMH");
        //query.whereEqualTo("User", user);
        query.whereEqualTo("day", 2);

        query.whereGreaterThan("hour", hour);
        query.whereLessThan("hour",hour-time);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {

                } else {
                    time = timecheck();
                    total = (int) object.get("watervolume");
                    total += total;

                    ParseUser user = ParseUser.getCurrentUser();
                    //userAnalysis.put("User", user);
                    //userAnalysis.put("3-6", total);
                    //userAnalysis.saveInBackground();
                }
            }
        });
        return total;
    }

    public int timecheck(){
        if(hour <= 7){
            i = 7;
        }
        else if(hour <= 11){
            i = 4;
        }
        else if(hour <= 21){
            i = 5;
        }
        return i;
    }

}
