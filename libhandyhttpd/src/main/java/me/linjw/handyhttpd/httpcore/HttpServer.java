package me.linjw.handyhttpd.httpcore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import me.linjw.handyhttpd.HandyHttpd;
import me.linjw.handyhttpd.scheduler.FixSizeScheduler;
import me.linjw.handyhttpd.scheduler.IScheduler;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */

@SuppressWarnings("WeakerAccess")
public class HttpServer {
    private int mTimeout;
    private boolean mIsDaemon;
    private HttpEngine mHttpEngine;
    private IScheduler mScheduler;
    private String mTempFileDir = System.getProperty("java.io.tmpdir");

    public HttpServer(
            int timeout,
            boolean isDaemon,
            String tempFileDir,
            IScheduler scheduler) {
        mTimeout = timeout;
        mIsDaemon = isDaemon;
        mTempFileDir = tempFileDir;
        mScheduler = scheduler;
    }

    /**
     * start server.
     *
     * @return is success
     */
    public boolean start(int port) {
        if (mHttpEngine != null) {
            return false;
        }

        if (mScheduler == null) {
            mScheduler = new FixSizeScheduler();
        }

        mHttpEngine = new HttpEngine(port, mTimeout);
        mHttpEngine.setDaemon(mIsDaemon);
        mHttpEngine.start();

        return true;
    }

    /**
     * stop http server.
     *
     * @return is success
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean stop() throws IOException, InterruptedException {
        if (mHttpEngine == null) {
            return false;
        }

        boolean r = mHttpEngine.stopEngine();
        mHttpEngine.join();
        return r;
    }

    /**
     * is http server ready.
     *
     * @return is ready
     */
    public boolean isReady() {
        return mHttpEngine != null && mHttpEngine.isReady();
    }

    /**
     * this method will be called when recv http request,
     * you can override it to do your work.
     *
     * @param request http request
     * @return HttpResponse
     */
    protected HttpResponse onRequest(HttpRequest request) {
        HandyHttpd.Log.log(request);
        return HandyHttpd.newResponse(HttpResponse.Status.NOT_FOUND, "404 Not Found");
    }

    /**
     * http engine.
     */
    private final class HttpEngine extends Thread {
        private int mPort;
        private int mTimeout;
        private boolean mIsRunning;
        private boolean mIsReady;
        private ServerSocket mServerSocket;
        private List<RequestWaiter> mWaiters;

        HttpEngine(int port, int timeout) {
            mPort = port;
            mTimeout = timeout;
            mWaiters = new LinkedList<>();
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
                mIsReady = true;

                while (mIsRunning) {
                    waitClientConnect();
                }
            } catch (IOException e) {
                HandyHttpd.Log.log(e);
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
            RequestWaiter session = new RequestWaiter(HttpServer.this, socket, mTempFileDir);
            mWaiters.add(session);
            mScheduler.schedule(session);
        }

        /**
         * stop engine.
         *
         * @return is success
         * @throws IOException
         */
        public boolean stopEngine() throws IOException {
            if (!mIsRunning || mServerSocket == null) {
                return false;
            }

            for (RequestWaiter waiter : mWaiters) {
                waiter.close();
            }
            mServerSocket.close();
            mIsRunning = false;
            mIsReady = false;
            return true;
        }

        /**
         * is http engine ready.
         *
         * @return is ready
         */
        public boolean isReady() {
            return mIsReady;
        }
    }
}