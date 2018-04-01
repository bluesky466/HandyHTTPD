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

@SuppressWarnings("WeakerAccess")
public class HandyHttpdServer {
    public static final boolean DEBUG = true;

    private int mPort;
    private HttpEngine mEngine;
    private IScheduler mScheduler;

    /**
     * new http response.
     *
     * @param message message
     * @return HttpResponse
     */
    public static HttpResponse newResponse(String message) {
        return newResponse(HttpResponse.Status.OK, message);
    }

    /**
     * new http response.
     *
     * @param status  status
     * @param message message
     * @return HttpResponse
     */
    public static HttpResponse newResponse(HttpResponse.Status status, String message) {
        return newResponse(status, message, HttpResponse.MIME_TYPE_PLAINTEXT);
    }

    /**
     * new http response.
     *
     * @param status   status
     * @param message  message
     * @param mimeType mimeType
     * @return HttpResponse
     */
    public static HttpResponse newResponse(
            HttpResponse.Status status,
            String message,
            String mimeType) {
        return new HttpResponse(
                status,
                mimeType,
                new ByteArrayInputStream(message.getBytes()),
                message.getBytes().length);
    }

    public HandyHttpdServer(int port) {
        mPort = port;
    }


    /**
     * set scheduler.
     *
     * @param scheduler scheduler
     */
    public void setScheduler(IScheduler scheduler) {
        mScheduler = scheduler;
    }


    /**
     * start server.
     *
     * @return is success
     */
    public boolean start() {
        if (mEngine != null) {
            return false;
        }

        if (mScheduler == null) {
            mScheduler = new FixSizeScheduler();
        }

        mEngine = new HttpEngine(this, mPort, mScheduler);
        mEngine.start();
        return true;
    }

    /**
     * this method will be called when recv http request,
     * you can override it to do your work.
     *
     * @param request http request
     * @return HttpResponse
     */
    public HttpResponse onRequest(HttpRequest request) {
        if (DEBUG) {
            request.printRequest();
        }
        return newResponse(HttpResponse.Status.NOT_FOUND, "404 Not Found");
    }
}