package com.pollhub.pollhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tony on 5/14/2016.
 */
public class PollsAdapter extends ArrayAdapter<Poll> {
    public PollsAdapter(Context context, ArrayList<Poll> polls){
        super(context, 0, polls);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Poll poll = getItem(position);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        HashMap<String, Object> dataMap;
        String d = "";

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poll_item, parent, false);
        }
        TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
        TextView tvOrg = (TextView) convertView.findViewById(R.id.org);
        TextView tvDate = (TextView) convertView.findViewById(R.id.date);
        TextView tvData = (TextView) convertView.findViewById(R.id.data);

        dataMap = poll.data;
        for(String key : dataMap.keySet()){
            d += key + " " + dataMap.get(key) + "    ";
        }
        tvTitle.setText(poll.title);
        tvOrg.setText(poll.org);
        tvDate.setText(formatter.format(poll.date));
        tvData.setText(d);

        return convertView;
    }
}
