package me.linjw.handyhttpd.httpcore;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import me.linjw.handyhttpd.HandyHttpd;
import me.linjw.handyhttpd.httpcore.cookie.Cookie;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */


@SuppressWarnings("unused")
public class HttpResponse {
    private Status mStatus;
    private InputStream mData;
    private MimeType mMimeType;
    private long mDataSize;
    private boolean mKeepAlive;
    private Map<String, Cookie> mCookies;
    private Map<String, String> mHeader = new HashMap<>();

    public HttpResponse(Status status, MimeType mimeType, InputStream data, long dataSize) {
        mStatus = status;
        mData = data;
        mDataSize = dataSize;
        mMimeType = mimeType;
    }

    void send(OutputStream os) throws IOException {
        try {
            sendHeader(os);
            sendBody(os);
            os.flush();
        } finally {
            HandyHttpd.safeClose(mData);
        }
    }

    void setCookies(Map<String, Cookie> cookies) {
        mCookies = cookies;
    }

    void setKeepAlive(boolean keepAlive) {
        mKeepAlive = keepAlive;
    }

    private void sendHeader(OutputStream os) throws UnsupportedEncodingException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        PrintWriter pw = new PrintWriter(writer, false);

        //status line
        pw.append("HTTP/1.1 ").append(mStatus.getDescription()).append(" \r\n");

        //header fields
        printHeaderField(pw, "Content-Type", mMimeType.getType());
        printHeaderField(pw, "content-length", String.valueOf(mDataSize));
        printHeaderField(pw, "Connection", mKeepAlive ? "keep-alive" : "close");
        if (mCookies != null) {
            for (Map.Entry<String, Cookie> entry : mCookies.entrySet()) {
                if (entry.getValue().isUpdata()) {
                    printHeaderField(pw, "Set-Cookie", entry.getValue().getHeader());
                }
            }
        }
        if (mDataSize < 0) {
            printHeaderField(pw, "Transfer-Encoding", "chunked");
        }
        pw.append("\r\n");
        pw.flush();
    }

    private void printHeaderField(PrintWriter pw, String key, String val) {
        pw.append(key).append(": ").append(val).append("\r\n");
    }

    private void sendBody(OutputStream os) throws IOException {
        ChunkedOutputStream chunkedOutputStream = null;
        if (mDataSize < 0) {
            chunkedOutputStream = new ChunkedOutputStream(os);
            os = chunkedOutputStream;
        }

        long BUFFER_SIZE = 16 * 1024;
        byte[] buff = new byte[(int) BUFFER_SIZE];

        int read = mData.read(buff, 0, (int) BUFFER_SIZE);
        while (read > 0) {
            os.write(buff, 0, read);
            read = mData.read(buff, 0, (int) BUFFER_SIZE);
        }

        if (chunkedOutputStream != null) {
            chunkedOutputStream.finish();
        }
        mData.close();
    }

    /**
     * ChunkedOutputStream
     */
    private static class ChunkedOutputStream extends OutputStream {
        private OutputStream mOutputStream;

        ChunkedOutputStream(OutputStream outputstream) {
            mOutputStream = outputstream;
        }

        @Override
        public void write(int data) throws IOException {
            write(new byte[]{(byte) data}, 0, 1);
        }

        @Override
        public void write(byte[] data) throws IOException {
            write(data, 0, data.length);
        }

        @Override
        public void write(byte[] data, int offset, int size) throws IOException {
            if (size > 0) {
                mOutputStream.write(String.format("%x\r\n", size).getBytes());
                mOutputStream.write(data, offset, size);
                mOutputStream.write("\r\n".getBytes());
            }
        }

        public void finish() throws IOException {
            mOutputStream.write("0\r\n\r\n".getBytes());
        }
    }

    /**
     * set header.
     *
     * @param key key
     * @param val val
     */
    public void setHeader(String key, String val) {
        mHeader.put(key, val);
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