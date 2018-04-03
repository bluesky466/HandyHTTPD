package me.linjw.handyhttpd;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import me.linjw.handyhttpd.httpcore.HttpRequest;
import me.linjw.handyhttpd.httpcore.HttpResponse;

/**
 * Created by linjw on 18-4-1.
 */

public class HandyHttpd {
    private static final boolean DEBUG = false;

    private static final String TAG = "HandyHttpd";

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
     * log.
     *
     * @param message message
     */
    public static void log(String message) {
        log(TAG, message);
    }

    /**
     * log.
     *
     * @param tag     tag
     * @param message message
     */
    public static void log(String tag, String message) {
        if (DEBUG) {
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
