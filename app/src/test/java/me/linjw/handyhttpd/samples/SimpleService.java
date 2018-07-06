package me.linjw.handyhttpd.samples;

import me.linjw.handyhttpd.annotation.Path;

/**
 * Created by linjiawei on 2018/7/5.
 * e-mail : bluesky466@qq.com
 */

public class SimpleService {
    private SimpleService mTarget;

    public SimpleService(SimpleService target) {
        mTarget = target;
    }

    @Path("/test")
    public String test(String arg) {
        return mTarget.test(arg);
    }
}
