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

    @Path("/testParm")
    public String testParam(String str,
                            boolean bool,
                            byte b,
                            char c,
                            double d,
                            float f,
                            int i,
                            long l,
                            short s) {
        return mTarget.testParam(str, bool, b, c, d, f, i, l, s);
    }
}
