package me.linjw.handyhttpd;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import me.linjw.handyhttpd.httpcore.HttpRequest;
import me.linjw.handyhttpd.httpcore.HttpResponse;
import me.linjw.handyhttpd.httpcore.HttpServer;
import me.linjw.handyhttpd.scheduler.FixSizeScheduler;
import me.linjw.handyhttpd.scheduler.IScheduler;

/**
 * Created by linjw on 18-4-1.
 */

public class HandyHttpd {
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

    /**
     * safe close.
     *
     * @param closeable closeable
     */
    public static void safeClose(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log Helper.
     */
    public static final class Log {
        private static final boolean DEBUG = true;
        private static final String TAG = "HandyHttpd";

        /**
         * log.
         *
         * @param message message
         */
        public static void log(String message) {
            log(null, message);
        }

        /**
         * log.
         *
         * @param tag     tag
         * @param message message
         */
        public static void log(String tag, String message) {
            if (DEBUG) {
                tag = (tag != null ? TAG + "-" + tag : TAG);
                System.out.println("[" + tag + "] " + message);
            }
        }

        /**
         * log.
         *
         * @param e Exception
         */
        public static void log(Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }

        /**
         * print request just for defbug.
         */
        public static void log(HttpRequest request) {
            if (!DEBUG) {
                return;
            }

            String method = request.getMethod();
            String uri = request.getUri();
            String version = request.getVersion();
            Map<String, String> headers = request.getHeaders();
            Map<String, String> params = request.getParams();

            log("########## " + method + " " + uri + " " + version + " ##########");
            log(request.getInetAddress().toString());
            log("Headers:");
            for (Map.Entry<String, String> header : headers.entrySet()) {
                log(header.getKey() + " : " + header.getValue());
            }
            log("Params:");
            for (Map.Entry<String, String> param : params.entrySet()) {
                log(param.getKey() + " = " + param.getValue());
            }
            log("##########################");
        }
    }

    /**
     * HttpServerBuilder.
     */
    public static final class HttpServerBuilder {
        private boolean mIsDaemon = false;
        private int mTimeout = 5000;
        private String mTempFileDir = System.getProperty("java.io.tmpdir");
        private IScheduler mScheduler;

        /**
         * set daemon.
         *
         * @param isDaemon isDaemon
         */
        public void setDaemon(boolean isDaemon) {
            mIsDaemon = isDaemon;
        }

        /**
         * set timeout.
         *
         * @param timeout timeout
         */
        public void setTimeout(int timeout) {
            mTimeout = timeout;
        }

        /**
         * set tempFileDir.
         *
         * @param tempFileDir tempFileDir
         */
        public void setTempFileDir(String tempFileDir) {
            mTempFileDir = tempFileDir;
        }

        /**
         * set Scheduler.
         *
         * @param scheduler scheduler
         */
        public void setScheduler(IScheduler scheduler) {
            mScheduler = scheduler;
        }

        /**
         * create HttpServer.
         *
         * @return HttpServer
         */
        public HttpServer create() {
            if (mScheduler == null) {
                mScheduler = new FixSizeScheduler();
            }

            return new HttpServer(
                    mTimeout,
                    mIsDaemon,
                    mTempFileDir,
                    mScheduler);
        }
    }
}
