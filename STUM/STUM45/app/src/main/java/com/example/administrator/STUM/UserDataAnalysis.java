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
    int hour = now.get(Calendar.HOUR);

    UserDataAnalysis() {
    }

    public int da() {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("dataTestMH");
        query.whereEqualTo("User", user);
        query.whereEqualTo("day", 22);

        query.whereGreaterThan("hour", hour);
        query.whereLessThan("hour",hour+3);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {

                } else {
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

}
