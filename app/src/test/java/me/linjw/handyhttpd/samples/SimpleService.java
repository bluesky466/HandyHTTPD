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
                            Boolean Bool,
                            byte b,
                            Byte B,
                            char c,
                            Character C,
                            double d,
                            Double D,
                            float f,
                            Float F,
                            int i,
                            Integer I,
                            long l,
                            Long L,
                            short s,
                            Short S) {
        return mTarget.testParam(str, bool, Bool, b, B, c, C, d, D, f, F, i, I, l, L, s, S);
    }
}
