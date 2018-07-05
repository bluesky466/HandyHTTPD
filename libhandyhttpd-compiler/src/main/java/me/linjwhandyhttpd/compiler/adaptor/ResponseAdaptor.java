package me.linjwhandyhttpd.compiler.adaptor;

import me.linjw.handyhttpd.httpcore.HttpResponse;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public abstract class ResponseAdaptor {
    abstract public String getConvertCode(String invokeCode);

    /**
     * get string response.
     *
     * @param status   status
     * @param response response
     * @return return line code
     */
    public static String getStringResponse(HttpResponse.Status status, String response) {
        return "return HandyHttpd.newResponse(" +
                "HttpResponse.Status." + status.name() +
                ", " +
                (response != null ? response : "\"\"") +
                ");\n";
    }
}
