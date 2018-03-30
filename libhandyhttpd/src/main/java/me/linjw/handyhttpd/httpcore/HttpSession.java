package me.linjw.handyhttpd.httpcore;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import me.linjw.handyhttpd.HandyHttpdServer;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */


public class HttpSession implements Runnable {
    public static final int BUF_SIZE = 8 * 1024;
    public static final int REQUEST_LINE_METHOD = 0;
    public static final int REQUEST_LINE_URI = 1;
    public static final int REQUEST_LINE_VERSION = 2;

    private HandyHttpdServer mServer;
    private Socket mSocket;
    private BufferedInputStream mInputStream;
    private OutputStream mOutputStream;

    public HttpSession(HandyHttpdServer server, Socket socket) throws IOException {
        mServer = server;
        mSocket = socket;
        mInputStream = new BufferedInputStream(socket.getInputStream());
        mOutputStream = socket.getOutputStream();
    }

    @Override
    public void run() {
        mInputStream.mark(BUF_SIZE);
        byte[] buff = new byte[BUF_SIZE];

        try {
            String[] requestLine = parseRequestLine(mInputStream, buff, BUF_SIZE);
            Map<String, String> headers = parseHeaders(mInputStream, buff, BUF_SIZE);


            if (requestLine == null || requestLine.length < 3) {
                return;
            }
            HttpRequest request = new HttpRequest(
                    requestLine[REQUEST_LINE_METHOD],
                    requestLine[REQUEST_LINE_URI],
                    requestLine[REQUEST_LINE_VERSION],
                    headers);

            HttpResponse response = mServer.onRequest(request);
            response.send(mOutputStream);

            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] parseRequestLine(BufferedInputStream inputStream, byte[] buf, int bufSize)
            throws IOException {
        int lineLength = getDataBySuffixs(inputStream, buf, bufSize, "\r\n");
        if (lineLength == 0) {
            return null;
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(buf, 0, lineLength))
        );
        return reader.readLine().split(" ");
    }

    private Map<String, String> parseHeaders(BufferedInputStream is, byte[] buf, int bufSize)
            throws IOException {
        int headerEnd = getDataBySuffixs(is, buf, bufSize, "\r\n\r\n", "\n\n");
        if (headerEnd == 0) {
            return null;
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(buf, 0, headerEnd))
        );

        Map<String, String> headers = new HashMap<>();
        String line = reader.readLine();
        while (line != null && !line.trim().isEmpty()) {
            int pos = line.indexOf(':');
            if (pos >= 0) {
                headers.put(
                        line.substring(0, pos).trim().toLowerCase(),
                        line.substring(pos + 1).trim()
                );
            }
            line = reader.readLine();
        }

        return headers;
    }

    private int getDataBySuffixs(BufferedInputStream is, byte[] buf, int bufSize, String... suffixs)
            throws IOException {
        int rlen = 0;
        int length = 0;
        int read = is.read(buf, 0, bufSize);
        while (read > 0) {
            rlen += read;
            length = getLength(buf, rlen, suffixs);
            if (length > 0) {
                break;
            }
            read = is.read(buf, rlen, bufSize - rlen);
        }

        if (length < rlen) {
            is.reset();
            is.skip(length);
        }

        return length;
    }

    private int getLength(final byte[] buf, int size, String... suffixs) {
        if (suffixs == null || suffixs.length <= 0) {
            return 0;
        }

        for (int i = 0; i < size; i++) {
            for (String suffix : suffixs) {
                if (isEqual(buf, i, size, suffix)) {
                    return i + suffix.length();
                }
            }
        }
        return 0;
    }

    private boolean isEqual(final byte[] buf, int offset, int size, String str) {
        if (offset + str.length() - 1 >= size) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (buf[offset + i] != str.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
