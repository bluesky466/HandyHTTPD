package me.linjw.handyhttpd.compiler.adaptor.response.java.lang.String;

import me.linjw.handyhttpd.httpcore.HttpResponse;
import me.linjw.handyhttpd.compiler.adaptor.ResponseAdaptor;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public class Adaptor extends ResponseAdaptor {
    @Override
    public String getConvertCode(String invokeCode) {
        return "\t\tString response = " + invokeCode +
                "\t\t" +
                ResponseAdaptor.getStringResponse(HttpResponse.Status.OK, "response");
    }
}
