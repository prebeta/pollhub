package com.pollhub.pollhub;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;

/**
 * Created by Tony on 5/14/2016.
 */
public class Poll {
    public String title;
    public String org;
    public Date date;
    public HashMap<String, Object> data;

    public Poll(String t, String o, Date d, HashMap<String, Object> da){
        this.title = t;
        this.org = o;
        this.date = d;
        this.data = da;
    }

    public Poll(HashMap<String, Object> pollData) {
        this.title = (String) pollData.get("name");
        this.org = (String) pollData.get("organization");
        Date d = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            d = formatter.parse((String) pollData.get("date"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.date = d;
        this.data = (HashMap<String, Object>) pollData.get("data");
    }
}


