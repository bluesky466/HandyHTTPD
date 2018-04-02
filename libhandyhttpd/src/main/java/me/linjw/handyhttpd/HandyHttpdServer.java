package me.linjw.handyhttpd;

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
    public static final int DEFAULT_KEEP_ALIVE_TIMEOUT = 5000;

    private int mPort;
    private HttpEngine mEngine;
    private IScheduler mScheduler;

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
        return start(DEFAULT_KEEP_ALIVE_TIMEOUT);
    }

    /**
     * start server.
     *
     * @param timeout timeout for keepalive
     * @return is success
     */
    public boolean start(int timeout) {
        if (mEngine != null) {
            return false;
        }

        if (mScheduler == null) {
            mScheduler = new FixSizeScheduler();
        }

        mEngine = new HttpEngine(this, mPort, timeout, mScheduler);
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
        HandyHttpd.printRequest(request);
        return HandyHttpd.newResponse(HttpResponse.Status.NOT_FOUND, "404 Not Found");
    }
}