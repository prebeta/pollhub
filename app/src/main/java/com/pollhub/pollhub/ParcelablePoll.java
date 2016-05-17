package com.pollhub.pollhub;

/**
 * Created by Tony on 5/16/2016.
 */
import android.os.Parcel;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ParcelablePoll implements Parcelable {
    public String title;
    public String org;
    public Date date;
    public HashMap<String, Object> data;

    public ParcelablePoll(String t, String o, Date d, HashMap<String, Object> da){
        this.title = t;
        this.org = o;
        this.date = d;
        this.data = da;
    }

    public ParcelablePoll(HashMap<String, Object> pollData) {
        this.title = (String) pollData.get("name");
        this.org = (String) pollData.get("organization");
        Date d = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        try {
            d = formatter.parse((String) pollData.get("date"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.date = d;
        this.data = (HashMap<String, Object>) pollData.get("data");
    }

    public ParcelablePoll(Poll p){
        this.title = p.title;
        this.data = p.data;
        this.org = p.org;
        this.date = p.date;
    }

    protected ParcelablePoll(Parcel in) {
        title = in.readString();
        org = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        data = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(org);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeValue(data);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ParcelablePoll> CREATOR = new Parcelable.Creator<ParcelablePoll>() {
        @Override
        public ParcelablePoll createFromParcel(Parcel in) {
            return new ParcelablePoll(in);
        }

        @Override
        public ParcelablePoll[] newArray(int size) {
            return new ParcelablePoll[size];
        }
    };
}