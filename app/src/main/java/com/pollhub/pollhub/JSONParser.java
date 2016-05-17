package com.pollhub.pollhub;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony on 5/14/2016.
 */

public class JSONParser {
    private String urlString = null;
    private HashMap<String, Object> map = null;
    public volatile boolean parsingComplete = true;

    public JSONParser(String url){
        this.urlString = url;
    }

    public HashMap<String, Object> getMap(){
        return this.map;
    }

    public void fetchJSON(){
        new JSONFetcher().execute(urlString);
    }

    public static HashMap<String, Object> jsonToMap(JSONObject json) throws JSONException {
        HashMap<String, Object> retMap = new HashMap<String, Object>();
        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static HashMap<String, Object> toMap(JSONObject object) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }
            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public class JSONFetcher extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urlstr) {
            String json;
            StringBuffer sb = new StringBuffer();
            InputStream stream = null;
            try {
                URL url = new URL(urlstr[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();

                stream = new BufferedInputStream(conn.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                String inputLine = "";
                while((inputLine = br.readLine()) != null ){
                    sb.append(inputLine);
                }
                json = sb.toString();
                JSONObject jsonRoot = new JSONObject(json);

                map = jsonToMap(jsonRoot);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            parsingComplete = false;
            return "Finished Parsing";
        }
    }

}
