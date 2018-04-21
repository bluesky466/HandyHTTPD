package me.linjw.handyhttpd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import me.linjw.handyhttpd.httpcore.HttpRequest;
import me.linjw.handyhttpd.httpcore.HttpResponse;
import me.linjw.handyhttpd.httpcore.HttpSession;
import me.linjw.handyhttpd.scheduler.FixSizeScheduler;
import me.linjw.handyhttpd.scheduler.IScheduler;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */

@SuppressWarnings("WeakerAccess")
public class HandyHttpdServer {
    public static final int DEFAULT_TIMEOUT = 5000;

    private int mPort;
    private HttpEngine mHttpEngine;
    private IScheduler mScheduler;
    private String mTempFileDir = System.getProperty("java.io.tmpdir");

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
        return start(DEFAULT_TIMEOUT);
    }

    /**
     * start server.
     *
     * @param timeout timeout
     * @return is success
     */
    public boolean start(int timeout) {
        return start(timeout, false);
    }

    /**
     * start server.
     *
     * @param timeout  timeout
     * @param isDaemon isDaemon
     * @return is success
     */
    public boolean start(int timeout, boolean isDaemon) {
        if (mHttpEngine != null) {
            return false;
        }

        if (mScheduler == null) {
            mScheduler = new FixSizeScheduler();
        }

        mHttpEngine = new HttpEngine(timeout);
        mHttpEngine.setDaemon(isDaemon);
        mHttpEngine.start();

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
        HandyHttpd.log(request);
        return HandyHttpd.newResponse(HttpResponse.Status.NOT_FOUND, "404 Not Found");
    }

    /**
     * set tempfile dir.
     *
     * @param tempFileDir tempfile dir
     */
    public void setTempFileDir(String tempFileDir) {
        mTempFileDir = tempFileDir;
    }

    /**
     * http engine.
     */
    private final class HttpEngine extends Thread {
        private boolean mIsRunning;
        private int mTimeout;
        private ServerSocket mServerSocket;

        HttpEngine(int timeout) {
            mTimeout = timeout;
        }

        @Override
        public synchronized void start() {
            super.start();
            mIsRunning = true;
        }

        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket();
                mServerSocket.setReuseAddress(true);
                mServerSocket.bind(new InetSocketAddress(mPort));

                while (mIsRunning) {
                    waitClientConnect();
                }
            } catch (IOException e) {
                HandyHttpd.log(e);
            }
        }

        private void waitClientConnect() throws IOException {
            Socket socket = mServerSocket.accept();
            if (socket == null) {
                return;
            }
            if (mTimeout > 0) {
                socket.setSoTimeout(mTimeout);
            }
            HttpSession session = new HttpSession(HandyHttpdServer.this, socket, mTempFileDir);
            mScheduler.schedule(session);
        }
    }
}