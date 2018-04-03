package me.linjw.handyhttpd.httpcore;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import me.linjw.handyhttpd.HandyHttpd;
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

    public static final String LOCAL_ADDRESS = "127.0.0.1";


    private final HandyHttpdServer mServer;
    private final Socket mSocket;
    private final BufferedInputStream mInputStream;
    private final OutputStream mOutputStream;
    private InetAddress mInetAddress;

    /**
     * parse request line from InputStream.
     *
     * @param reader reader for request line
     * @return request line: [METHOD,URI,VERSION] or null
     * @throws IOException IOException
     * @see HttpSession#REQUEST_LINE_METHOD
     * @see HttpSession#REQUEST_LINE_URI
     * @see HttpSession#REQUEST_LINE_VERSION
     */
    public static String[] parseRequestLine(BufferedReader reader) throws IOException {
        return reader.readLine().split(" ");
    }

    /**
     * parse header fields from InputStream.
     *
     * @param reader reader for header fields
     * @return headers
     * @throws IOException IOException
     */
    public static Map<String, String> parseHeaderFields(BufferedReader reader) throws IOException {
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
        if (read == -1) {
            throw new SocketException("socket disconnect");
        }
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
        byte[] buff = new byte[BUF_SIZE];

        mInetAddress = mSocket.getInetAddress();
        try {
            while (!mSocket.isClosed()) {
                HandyHttpd.log("wait for request : " + mInetAddress);
                mInputStream.mark(BUF_SIZE);
                waitRequest(buff, BUF_SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HandyHttpd.safeClose(mInputStream);
            HandyHttpd.safeClose(mOutputStream);
            HandyHttpd.safeClose(mSocket);
            HandyHttpd.log("disonnect : " + mInetAddress);
        }
    }

    private void waitRequest(byte[] buff, int bufSize) throws IOException {
        int headerEnd = moveDataWithSuffix(mInputStream, buff, bufSize, "\r\n\r\n", "\n\n");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(buff, 0, headerEnd))
        );

        String[] requestLine = parseRequestLine(reader);
        Map<String, String> headers = parseHeaderFields(reader);

        if (mInetAddress != null && headers != null) {
            String ip = mInetAddress.isLoopbackAddress() || mInetAddress.isAnyLocalAddress()
                    ? LOCAL_ADDRESS : mInetAddress.getHostAddress();
            headers.put("remote-addr", ip);
            headers.put("http-client-ip", ip);
        }

        if (requestLine == null || requestLine.length < 3) {
            return;
        }
        HttpRequest request = new HttpRequest(
                requestLine[REQUEST_LINE_METHOD],
                requestLine[REQUEST_LINE_URI],
                requestLine[REQUEST_LINE_VERSION],
                headers,
                mInetAddress);

        HttpResponse response = mServer.onRequest(request);
        String connection = request.getHeaders().get("connection");
        if ("HTTP/1.1".equals(request.getVersion())
                && (connection == null || !connection.contains("close"))) {
            response.setKeepAlive(true);
        }

        response.send(mOutputStream);
    }
}
