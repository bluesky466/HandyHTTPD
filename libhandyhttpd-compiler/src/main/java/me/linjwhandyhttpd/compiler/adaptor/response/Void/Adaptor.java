package me.linjwhandyhttpd.compiler.adaptor.response.Void;

import me.linjw.handyhttpd.httpcore.HttpResponse;
import me.linjwhandyhttpd.compiler.adaptor.ResponseAdaptor;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public class Adaptor extends ResponseAdaptor {
    @Override
    public String getConvertCode(String invokeCode) {
        return "\t\t" +
                invokeCode +
                "\t\t" +
                ResponseAdaptor.getStringResponse(HttpResponse.Status.OK, null);
    }
}
