package me.linjw.handyhttpd;

import me.linjw.handyhttpd.httpcore.HttpRequest;
import me.linjw.handyhttpd.httpcore.HttpResponse;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public interface IServiceHandler {
    /**
     * http request handler.
     *
     * @param request request
     * @return Response
     */
    HttpResponse onRequest(HttpRequest request);
}
