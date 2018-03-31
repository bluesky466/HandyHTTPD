package me.linjw.handyhttpd.httpcore;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */


@SuppressWarnings("unused")
public class HttpResponse {
    public static final String MIME_TYPE_PLAINTEXT = "text/plain";
    public static final String MIME_TYPE_HTML = "text/html";

    private Status mStatus;
    private InputStream mData;
    private long mDataSize;
    private String mMimeType;

    public HttpResponse(Status status, String mimeType, InputStream data, long dataSize) {
        mStatus = status;
        mData = data;
        mDataSize = dataSize;
        mMimeType = mimeType;
    }

    void send(OutputStream os) {
        try {
            sendHeader(os);
            sendBody(os);
            os.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void sendHeader(OutputStream os) throws UnsupportedEncodingException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        PrintWriter pw = new PrintWriter(writer, false);

        //status line
        pw.append("HTTP/1.1 ").append(mStatus.getDescription()).append(" \r\n");

        //header fields
        printHeaderField(pw, "Content-Type", mMimeType);
        printHeaderField(pw, "content-length", String.valueOf(mDataSize));
        pw.append("\r\n");
        pw.flush();
    }

    private void printHeaderField(PrintWriter pw, String key, String val) {
        pw.append(key).append(": ").append(val).append("\r\n");
    }

    private void sendBody(OutputStream os) throws IOException {
        long BUFFER_SIZE = 16 * 1024;
        byte[] buff = new byte[(int) BUFFER_SIZE];

        int read = mData.read(buff, 0, (int) BUFFER_SIZE);
        while (read > 0) {
            os.write(buff, 0, read);
            read = mData.read(buff, 0, (int) BUFFER_SIZE);
        }
    }

    public enum Status {
        CONTINUE(100, "Continue"),
        SWITCH_PROTOCOL(101, "Switching Protocols"),

        OK(200, "OK"),
        CREATED(201, "Created"),
        ACCEPTED(202, "Accepted"),
        NO_CONTENT(204, "No Content"),
        PARTIAL_CONTENT(206, "Partial Content"),
        MULTI_STATUS(207, "Multi-Status"),

        REDIRECT(301, "Moved Permanently"),
        FOUND(302, "Found"),
        REDIRECT_SEE_OTHER(303, "See Other"),
        NOT_MODIFIED(304, "Not Modified"),
        USE_PROXY(305, "Use Proxy"),
        TEMPORARY_REDIRECT(307, "Temporary Redirect"),

        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
        NOT_ACCEPTABLE(406, "Not Acceptable"),
        REQUEST_TIMEOUT(408, "Request Timeout"),
        CONFLICT(409, "Conflict"),
        GONE(410, "Gone"),
        LENGTH_REQUIRED(411, "Length Required"),
        PRECONDITION_FAILED(412, "Precondition Failed"),
        PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
        RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
        EXPECTATION_FAILED(417, "Expectation Failed"),
        TOO_MANY_REQUESTS(429, "Too Many Requests"),

        INTERNAL_ERROR(500, "Internal Server Error"),
        NOT_IMPLEMENTED(501, "Not Implemented"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),
        UNSUPPORTED_HTTP_VERSION(505, "HTTP Version Not Supported");

        private final int mStatus;
        private final String mDescription;

        Status(int status, String description) {
            mStatus = status;
            mDescription = description;
        }

        public static Status lookup(int requestStatus) {
            for (Status status : Status.values()) {
                if (status.getStatus() == requestStatus) {
                    return status;
                }
            }
            return null;
        }

        public String getDescription() {
            return mStatus + " " + mDescription;
        }

        public int getStatus() {
            return mStatus;
        }

    }
}