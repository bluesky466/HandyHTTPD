package me.linjw.handyhttpd.httpcore;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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


@SuppressWarnings("WeakerAccess")
public class HttpSession implements Runnable {
    public static final int BUF_SIZE = 8 * 1024;

    public static final int REQUEST_LINE_METHOD = 0;
    public static final int REQUEST_LINE_URI = 1;
    public static final int REQUEST_LINE_VERSION = 2;

    private final HandyHttpdServer mServer;
    private final Socket mSocket;
    private final BufferedInputStream mInputStream;
    private final OutputStream mOutputStream;

    /**
     * parse request line from InputStream.
     * Notice: this method will skip the InputStream when parse the request line.
     *
     * @param is      InputStream
     * @param buf     a temp buffer
     * @param bufSize buffer size
     * @return request line: [METHOD,URI,VERSION] or null
     * @throws IOException IOException
     * @see HttpSession#REQUEST_LINE_METHOD
     * @see HttpSession#REQUEST_LINE_URI
     * @see HttpSession#REQUEST_LINE_VERSION
     */
    public static String[] parseRequestLine(InputStream is, byte[] buf, int bufSize)
            throws IOException {
        int lineLength = moveDataWithSuffix(is, buf, bufSize, "\r\n");
        if (lineLength == 0) {
            return null;
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(buf, 0, lineLength))
        );
        return reader.readLine().split(" ");
    }

    /**
     * parse header fields from InputStream.
     * Notice: this method will skip the InputStream when parse the header fields.
     *
     * @param is      InputStream
     * @param buf     a temp buffer
     * @param bufSize buffer size
     * @return headers
     * @throws IOException IOException
     */
    public static Map<String, String> parseHeaderFields(InputStream is, byte[] buf, int bufSize)
            throws IOException {
        int headerEnd = moveDataWithSuffix(is, buf, bufSize, "\r\n\r\n", "\n\n");
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

    /**
     * move data with one of the suffixs from InputStream to the buffer.
     * Notice: this method will skip the InputStream when write data to buf.
     *
     * @param is      InputStream
     * @param buf     buffer to write
     * @param bufSize buffer size
     * @param suffixs suffixs
     * @return the size of data write to buffer
     * @throws IOException IOException
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static int moveDataWithSuffix(InputStream is, byte[] buf, int bufSize, String... suffixs)
            throws IOException {
        int rlen = 0;
        int length = 0;
        int read = is.read(buf, 0, bufSize);
        while (read > 0) {
            rlen += read;
            length = findEnd(buf, rlen, suffixs);
            if (length > 0) {
                break;
            }
            read = is.read(buf, rlen, bufSize - rlen);
        }

        if (length > 0 && length < rlen) {
            is.reset();
            is.skip(length);
        }

        return length > 0 ? length : 0;
    }

    /**
     * find the end pos of the first occur str with one of the suffixs in byte.
     *
     * @param data    data to search
     * @param size    size of data array
     * @param suffixs suffixs list
     * @return the end pos. Notice it is the last index + 1
     */
    public static int findEnd(final byte[] data, int size, String... suffixs) {
        if (suffixs == null || suffixs.length <= 0) {
            return -1;
        }

        for (int i = 0; i < size; i++) {
            for (String suffix : suffixs) {
                if (isEqual(data, i, size, suffix)) {
                    return i + suffix.length();
                }
            }
        }
        return -1;
    }

    /**
     * compare data with string in buf level.
     *
     * @param data   byte buffer
     * @param offset begin index of data to compare
     * @param size   size of data array
     * @param str    string to compare
     * @return is equal or not
     */
    public static boolean isEqual(final byte[] data, int offset, int size, String str) {
        if (offset + str.length() - 1 >= size) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (data[offset + i] != str.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public HttpSession(HandyHttpdServer server, Socket socket) throws IOException {
        mServer = server;
        mSocket = socket;
        mInputStream = new BufferedInputStream(socket.getInputStream());
        mOutputStream = socket.getOutputStream();
    }

    /**
     * read http request and send http response in a subthread.
     */
    @Override
    public void run() {
        mInputStream.mark(BUF_SIZE);
        byte[] buff = new byte[BUF_SIZE];

        try {
            String[] requestLine = parseRequestLine(mInputStream, buff, BUF_SIZE);
            Map<String, String> headers = parseHeaderFields(mInputStream, buff, BUF_SIZE);


            if (requestLine == null || requestLine.length < 3) {
                return;
            }
            HttpRequest request = new HttpRequest(
                    requestLine[REQUEST_LINE_METHOD],
                    requestLine[REQUEST_LINE_URI],
                    requestLine[REQUEST_LINE_VERSION],
                    headers,
                    mSocket.getInetAddress());

            HttpResponse response = mServer.onRequest(request);
            response.send(mOutputStream);

            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
