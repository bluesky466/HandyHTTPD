package me.linjw.handyhttpd;

import java.io.ByteArrayInputStream;

import me.linjw.handyhttpd.httpcore.HttpEngine;
import me.linjw.handyhttpd.httpcore.HttpRequest;
import me.linjw.handyhttpd.httpcore.HttpResponse;
import me.linjw.handyhttpd.scheduler.FixSizeScheduler;
import me.linjw.handyhttpd.scheduler.IScheduler;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */


public class HandyHttpdServer {
    private int mPort;
    private HttpEngine mEngine;
    private IScheduler mScheduler;

    public HandyHttpdServer(int port) {
        mPort = port;
        mScheduler = new FixSizeScheduler();
    }

    public boolean start() {
        if (mEngine != null) {
            return false;
        }
        mEngine = new HttpEngine(this, mPort, mScheduler);
        mEngine.start();
        return true;
    }

    public HttpResponse onRequest(HttpRequest request) {
        byte[] data = ("access : " + request.getUri()).getBytes();

        return new HttpResponse(
                HttpResponse.Status.OK,
                HttpResponse.MIME_TYPE_PLAINTEXT,
                new ByteArrayInputStream(data),
                data.length);
    }
}