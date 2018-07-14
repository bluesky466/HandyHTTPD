package me.linjw.handyhttpd.httpcore.cookie;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by linjiawei on 2018/7/14.
 * e-mail : bluesky466@qq.com
 */

public class Cookie {
    public static final int DEFAULT_EXPIRES = 30;
    private String mKey;
    private String mValue;
    private int mExpires;
    private boolean mIsUpdata;

    public Cookie(String header) {
        if (header == null) {
            return;
        }
        String[] data = header.split("=");
        if (data.length == 2) {
            mKey = data[0];
            mValue = data[1];
        }
    }

    public Cookie(String key, int value) {
        this(key, value, DEFAULT_EXPIRES);
    }

    public Cookie(String key, int value, int expires) {
        this(key, String.valueOf(value), DEFAULT_EXPIRES);
    }

    public Cookie(String key, long value) {
        this(key, value, DEFAULT_EXPIRES);
    }

    public Cookie(String key, long value, int expires) {
        this(key, String.valueOf(value), DEFAULT_EXPIRES);
    }

    public Cookie(String key, String value) {
        this(key, value, DEFAULT_EXPIRES);
    }

    public Cookie(String key, String value, int expires) {
        mKey = key;
        mValue = value;
        mExpires = expires;
        mIsUpdata = mValue != null;
    }

    public static String getHTTPTime(int days) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return fmt.format(calendar.getTime());
    }

    public int getExpires() {
        return mExpires;
    }

    public String getKey() {
        return mKey;
    }

    public String getValueString() {
        return mValue;
    }

    public int getValueInteger() {
        return Integer.getInteger(mValue);
    }

    public long getValueLong() {
        return Long.getLong(mValue);
    }

    public void setValue(String value, int expires) {
        mValue = value;
        mExpires = expires;
        mIsUpdata = true;
    }

    public void setValue(String value) {
        setValue(value, DEFAULT_EXPIRES);
    }

    public void setValue(int value) {
        setValue(String.valueOf(value));
    }

    public void setValue(int value, int expires) {
        setValue(String.valueOf(value), expires);
    }

    public void setValue(long value) {
        setValue(String.valueOf(value));
    }

    public void setValue(long value, int expires) {
        setValue(String.valueOf(value), expires);
    }

    public void delete() {
        delete(DEFAULT_EXPIRES);
    }

    public void delete(int expires) {
        setValue("-delete-", -expires);
    }

    public boolean isUpdata() {
        return mIsUpdata;
    }

    public String getHeader() {
        String expires = getHTTPTime(mExpires);
        return String.format("%s=%s; expires=%s", mKey, mValue, expires);
    }
}
