package me.linjw.handyhttpd.compiler.adaptor.response.java.io.File;

import me.linjw.handyhttpd.compiler.adaptor.ResponseAdaptor;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public class Adaptor extends ResponseAdaptor {
    @Override
    public String getConvertCode(String invokeCode) {
        return "\t\tjava.io.File response = " + invokeCode + "\t\t" +
                "return HandyHttpd.newResponse(" +
                "HttpResponse.Status.OK, " +
                "response);\n";
    }
}
