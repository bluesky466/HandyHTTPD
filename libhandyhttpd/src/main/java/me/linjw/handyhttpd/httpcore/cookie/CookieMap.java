package me.linjw.handyhttpd.httpcore.cookie;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linjiawei on 2018/7/14.
 * e-mail : bluesky466@qq.com
 */

public class CookieMap extends HashMap<String, Cookie> {
    private Map<String, Cookie> mOriginCookies;

    public CookieMap(Map<String, Cookie> cookies) {
        super(cookies);
        mOriginCookies = cookies;
    }

    @Override
    public Cookie put(String key, Cookie cookie) {
        mOriginCookies.put(key, cookie);
        return super.put(key, cookie);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Cookie> cookies) {
        mOriginCookies.putAll(cookies);
        super.putAll(cookies);
    }

    @Override
    public Cookie remove(Object key) {
        Cookie cookie = mOriginCookies.get(key);
        if (cookie != null) {
            cookie.delete();
        }
        return super.remove(key);
    }
}
