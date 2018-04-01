package me.linjw.handyhttpd.httpcore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import me.linjw.handyhttpd.HandyHttpdServer;
import me.linjw.handyhttpd.scheduler.IScheduler;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */


public class HttpEngine extends Thread {
    private HandyHttpdServer mServer;
    private ServerSocket mServerSocket;
    private IScheduler mScheduler;
    private boolean mIsRunning;
    private int mKeepAliveTimeout;
    private int mPort;

    public HttpEngine(HandyHttpdServer server, int port, int keepAliveTimeout, IScheduler scheduler) {
        mServer = server;
        mPort = port;
        mScheduler = scheduler;
        mKeepAliveTimeout = keepAliveTimeout;
    }

    @Override
    public synchronized void start() {
        mIsRunning = true;
        super.start();
    }

    @Override
    public void run() {
        super.run();

        try {
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(mPort));

            while (mIsRunning) {
                waitClientConnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitClientConnect() throws IOException {
        Socket socket = mServerSocket.accept();
        if (mKeepAliveTimeout > 0) {
            socket.setSoTimeout(mKeepAliveTimeout);
        }
        HttpSession session = new HttpSession(mServer, socket);
        mScheduler.schedule(session);
    }
}