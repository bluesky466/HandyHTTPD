package me.linjw.handyhttpd.httpcore;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import me.linjw.handyhttpd.HandyHttpd;
import me.linjw.handyhttpd.exception.HandyException;
import me.linjw.handyhttpd.httpcore.multipartbody.MultipartBodyProcessor;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */


@SuppressWarnings("WeakerAccess")
class RequestWaiter implements Runnable {
    public static final int BUF_SIZE = 8 * 1024;

    public static final int REQUEST_LINE_METHOD = 0;
    public static final int REQUEST_LINE_URI = 1;
    public static final int REQUEST_LINE_VERSION = 2;

    public static final String LOCAL_ADDRESS = "127.0.0.1";

    private final HttpServer mServer;
    private final Socket mSocket;
    private final BufferedInputStream mInputStream;
    private final OutputStream mOutputStream;
    private final String mTempFileDir;
    private InetAddress mInetAddress;
    private MultipartBodyProcessor mMultipartBodyProcessor;

    /**
     * parse request line from InputStream.
     *
     * @param reader reader for request line
     * @return request line: [Method,URI,VERSION] or null
     * @throws IOException IOException
     * @see RequestWaiter#REQUEST_LINE_METHOD
     * @see RequestWaiter#REQUEST_LINE_URI
     * @see RequestWaiter#REQUEST_LINE_VERSION
     */
    static String[] parseRequestLine(BufferedReader reader) throws IOException {
        return reader.readLine().split(" ");
    }

    /**
     * parse header fields from InputStream.
     *
     * @param reader reader for header fields
     * @return headers
     * @throws IOException IOException
     */
    static Map<String, String> parseHeaderFields(BufferedReader reader) throws IOException {
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
    static int moveDataWithSuffix(InputStream is, byte[] buf, int bufSize, byte[]... suffixs)
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
    static int findEnd(final byte[] data, int size, byte[]... suffixs) {
        if (suffixs == null || suffixs.length <= 0) {
            return -1;
        }

        for (int i = 0; i < size; i++) {
            for (byte[] suffix : suffixs) {
                if (isEqual(data, i, size, suffix)) {
                    return i + suffix.length;
                }
            }
        }
        return -1;
    }

    /**
     * compare data with string in buf level.
     *
     * @param data      byte buffer
     * @param offset    begin index of data to compare
     * @param size      size of data array
     * @param toCompare string to compare
     * @return is equal or not
     */
    static boolean isEqual(final byte[] data, int offset, int size, byte[] toCompare) {
        if (offset + toCompare.length - 1 >= size) {
            return false;
        }

        for (int i = 0; i < toCompare.length; i++) {
            if (data[offset + i] != toCompare[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * decode the url which might with %.
     *
     * @param str url
     * @return decode str
     */
    static String decodeUrl(String str) {
        String decoded = null;
        try {
            decoded = URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException e) {
            HandyHttpd.Log.log(e);
        }
        return decoded;
    }

    /**
     * parse http body.
     *
     * @param is       InputStream
     * @param request  request
     * @param buff     buff
     * @param cacheDir cacheDir
     */
    static void parseBody(InputStream is,
                          HttpRequest request,
                          byte[] buff,
                          String cacheDir,
                          MultipartBodyProcessor processor) throws IOException {
        if (request.getMethod() != HttpRequest.Method.POST) {
            return;
        }

        long size = getBodySize(request);
        if (size <= 0) {
            return;
        }

        ContentType contentType = new ContentType(request.getHeaders().get("content-type"));

        if (contentType.isMultipart()) {
            int rlen = 0;

            Map<String, String> params = new HashMap<>();
            Map<String, File> files = new HashMap<>();
            while (rlen >= 0 && size > 0) {
                rlen = is.read(buff);
                size -= rlen;
                processor.setBoundary(contentType.getBoundary());
                processor.process(buff, rlen, params, files, cacheDir);
            }
            request.putParams(params);
            request.putFiles(files);
            return;
        }

        String postLine = getStringFromInputStream(is, buff, size);
        if (postLine == null) {
            return;
        }

        if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentType.getContentType())) {
            request.putParams(HttpRequest.parseParams(postLine));
        } else if (postLine.length() != 0) {
            request.putParam("postData", postLine);
        }
    }

    /**
     * get boundary from content type.
     *
     * @param contentType content type
     * @return boundary
     */
    static String getBoundary(String contentType) {
        int begin = contentType.indexOf("boundary");
        if (begin < 0) {
            return null;
        }
        int end = contentType.indexOf(";", begin);
        if (end < 0) {
            end = contentType.length() - 1;
        }
        String boundary = contentType.substring(begin, end)
                .replace(" ", "")
                .substring("boundary=".length());
        return "--" + boundary;
    }

    /**
     * get String from InputStream.
     *
     * @param is   InputStream
     * @param buff buff
     * @param size size for is
     * @return String converted from InputStream
     */
    static String getStringFromInputStream(InputStream is, byte[] buff, long size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            int rlen = 0;
            if (size >= 0) {
                while (rlen >= 0 && size > 0) {
                    rlen = is.read(buff, 0, (int) Math.min(size, buff.length));
                    if (rlen > 0) {
                        dos.write(buff, 0, rlen);
                        size -= rlen;
                    }
                }
            } else {
                while (rlen >= 0) {
                    rlen = is.read(buff, 0, buff.length);
                    if (rlen > 0) {
                        dos.write(buff, 0, rlen);
                    }
                }
            }
            return baos.toString();
        } catch (IOException e) {
            HandyHttpd.Log.log(e);
        } finally {
            HandyHttpd.safeClose(dos);
            HandyHttpd.safeClose(baos);
        }
        return null;
    }

    /**
     * get body size.
     *
     * @param request request
     * @return return body size if headers contain content-length,else return -1
     */
    static long getBodySize(HttpRequest request) {
        if (request.getHeaders().containsKey("content-length")) {
            return Long.parseLong(request.getHeaders().get("content-length"));
        }
        return -1;
    }

    RequestWaiter(HttpServer server, Socket socket, String tempFileDir)
            throws IOException {
        mServer = server;
        mSocket = socket;
        mInputStream = new BufferedInputStream(socket.getInputStream());
        mOutputStream = socket.getOutputStream();
        mTempFileDir = tempFileDir;
        mMultipartBodyProcessor = new MultipartBodyProcessor();
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
                HandyHttpd.Log.log("wait for request : " + mInetAddress);
                mInputStream.mark(BUF_SIZE);
                waitRequest(buff, BUF_SIZE);
            }
        } catch (IOException e) {
            HandyHttpd.Log.log(e);
        } catch (HandyException e) {
            HandyHttpd.Log.log(e);
        } finally {
            HandyHttpd.safeClose(mInputStream);
            HandyHttpd.safeClose(mOutputStream);
            HandyHttpd.safeClose(mSocket);
            HandyHttpd.Log.log("disonnect : " + mInetAddress);
        }
    }

    private void waitRequest(byte[] buff, int bufSize) throws IOException, HandyException {
        int headerEnd = moveDataWithSuffix(
                mInputStream, buff, bufSize, "\r\n\r\n".getBytes(), "\n\n".getBytes());
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(buff, 0, headerEnd))
        );

        if (headerEnd == 0) {
            return;
        }

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
        requestLine[REQUEST_LINE_URI] = decodeUrl(requestLine[REQUEST_LINE_URI]);
        HttpRequest request = new HttpRequest(
                requestLine[REQUEST_LINE_METHOD],
                requestLine[REQUEST_LINE_URI],
                requestLine[REQUEST_LINE_VERSION],
                headers,
                mInetAddress);
        parseBody(mInputStream, request, buff, mTempFileDir, mMultipartBodyProcessor);
        HttpResponse response = mServer.onRequest(request);

        String connection = request.getHeaders().get("connection");
        if ("HTTP/1.1".equals(request.getVersion())
                && (connection == null || !connection.contains("close"))) {
            response.setKeepAlive(true);
        }
        response.send(mOutputStream);
    }

    public void close() throws IOException {
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.close();
        }
    }
}

