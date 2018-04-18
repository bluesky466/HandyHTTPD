package me.linjw.handyhttpd.httpcore;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

import me.linjw.handyhttpd.HandyHttpdServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Created by linjiawei on 2018/3/31.
 * e-mail : bluesky466@qq.com
 */
public class HttpSessionTest {
    @Test
    public void isEqual() {
        String str = "hello world";
        byte[] bytes = str.getBytes();

        assertTrue(HttpSession.isEqual(
                bytes, 0, bytes.length, str.getBytes()));
        assertTrue(HttpSession.isEqual(
                bytes, "hello ".getBytes().length, bytes.length, "world".getBytes()));
        assertFalse(HttpSession.isEqual(
                bytes, 1, bytes.length, "world".getBytes()));
    }

    @Test
    public void findEnd() {
        byte[] bytes = "hello world\r\nhello world\r\n\r\n\n".getBytes();

        assertEquals(
                "hello world\r\n".getBytes().length,
                HttpSession.findEnd(bytes, bytes.length, "\r\n".getBytes())
        );

        assertEquals(
                "hello world\r\nhello world\r\n\r\n".getBytes().length,
                HttpSession.findEnd(bytes, bytes.length, "\r\n\r\n".getBytes())
        );

        assertEquals(
                bytes.length,
                HttpSession.findEnd(bytes, bytes.length, "\n\n".getBytes())
        );

        assertEquals(
                -1,
                HttpSession.findEnd(bytes, bytes.length, "\r\r".getBytes())
        );
    }

    @Test
    public void getDataBySuffixs() throws IOException {
        byte[] buf = new byte[1024];
        String data = "hello world\r\nhello world\r\n\r\n";
        InputStream is = new ByteArrayInputStream(data.getBytes());

        assertEquals(
                "hello world\r\n".getBytes().length,
                HttpSession.moveDataWithSuffix(is, buf, buf.length, "\r\n".getBytes())
        );

        //this method will skip the InputStream when write data to buf,
        //so 'hello world\r\n' is skiped in the before step.
        assertEquals(
                "hello world\r\n\r\n".getBytes().length,
                HttpSession.moveDataWithSuffix(is, buf, buf.length, "\r\n\r\n".getBytes())
        );

        is = new ByteArrayInputStream(data.getBytes());
        assertEquals(
                data.getBytes().length,
                HttpSession.moveDataWithSuffix(is, buf, buf.length, "\r\n\r\n".getBytes())
        );

        is = new ByteArrayInputStream(data.getBytes());
        assertEquals(
                0,
                HttpSession.moveDataWithSuffix(is, buf, buf.length, "\r\r".getBytes())
        );
    }

    @Test
    public void parseHeaderFields() throws IOException {
        String headerFields = "Host: tools.ietf.org\r\n" +
                "Connection: keep-alive\r\n" +
                "Pragma: no-cache\r\n" +
                "Cache-Control: no-cache\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n\r\n";

        InputStream is = new ByteArrayInputStream(headerFields.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Map<String, String> header = HttpSession.parseHeaderFields(reader);
        assertNotNull(header);
        assertEquals("tools.ietf.org", header.get("host"));
        assertEquals("keep-alive", header.get("connection"));
        assertEquals("no-cache", header.get("pragma"));
        assertEquals("no-cache", header.get("cache-control"));
        assertEquals("1", header.get("upgrade-insecure-requests"));
        assertEquals("gzip, deflate, br", header.get("accept-encoding"));
        assertEquals("zh-CN,zh;q=0.9,en;q=0.8", header.get("accept-language"));
    }

    @Test
    public void parseRequestLine() throws IOException {
        String headerFields = "GET /html/rfc2616 HTTP/1.1\r\n";

        InputStream is = new ByteArrayInputStream(headerFields.getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String[] requestLine = HttpSession.parseRequestLine(reader);
        assertNotNull(requestLine);
        assertEquals("GET", requestLine[HttpSession.REQUEST_LINE_METHOD]);
        assertEquals("/html/rfc2616", requestLine[HttpSession.REQUEST_LINE_URI]);
        assertEquals("HTTP/1.1", requestLine[HttpSession.REQUEST_LINE_VERSION]);
    }

    @Test
    public void run() throws IOException {
        String header = "POST /html/rfc2616?key1=val1 HTTP/1.1\r\n" +
                "Host: tools.ietf.org\r\n" +
                "Connection: keep-alive\r\n" +
                "Pragma: no-cache\r\n" +
                "Cache-Control: no-cache\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n" +
                "Content-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n" +
                "content-type: application/x-www-form-urlencoded\r\n" +
                "content-length: 19\r\n" +
                "\r\n" +
                "key2=val2&key3=val3";

        header += header;
        Socket socket = mock(Socket.class);
        given(socket.getInputStream()).willReturn(new ByteArrayInputStream(header.getBytes()));
        given(socket.getOutputStream()).willReturn(new ByteArrayOutputStream(0));

        HttpResponse response = mock(HttpResponse.class);
        HandyHttpdServer server = mock(HandyHttpdServer.class);
        given(server.onRequest(any(HttpRequest.class))).willReturn(response);

        new HttpSession(server, socket).run();

        ArgumentCaptor<HttpRequest> arg = ArgumentCaptor.forClass(HttpRequest.class);

        then(server).should(times(2)).onRequest(arg.capture());

        HttpRequest request = arg.getValue();
        assertEquals("POST", request.getMethod());
        assertEquals("/html/rfc2616", request.getUri());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("tools.ietf.org", request.getHeaders().get("host"));
        assertEquals("keep-alive", request.getHeaders().get("connection"));
        assertEquals("no-cache", request.getHeaders().get("pragma"));
        assertEquals("no-cache", request.getHeaders().get("cache-control"));
        assertEquals("1", request.getHeaders().get("upgrade-insecure-requests"));
        assertEquals("gzip, deflate, br", request.getHeaders().get("accept-encoding"));
        assertEquals("zh-CN,zh;q=0.9,en;q=0.8", request.getHeaders().get("accept-language"));
        assertEquals("val1", request.getParams().get("key1"));
        assertEquals("val2", request.getParams().get("key2"));
        assertEquals("val3", request.getParams().get("key3"));
    }

    @Test
    public void runWithPercent() throws IOException {
        String url = "/%E8%B7%AF%E5%BE%84%E8%B7%AF%E5%BE%84" +
                "?%E5%8F%82%E6%95%B01=%E5%80%BC1" +
                "&%E5%8F%82%E6%95%B02=%E5%80%BC2";

        String header = "POST " + url + " HTTP/1.1\r\n" +
                "Host: tools.ietf.org\r\n" +
                "Connection: keep-alive\r\n" +
                "Pragma: no-cache\r\n" +
                "Cache-Control: no-cache\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n" +
                "\r\n" +
                "PostBody";

        Socket socket = mock(Socket.class);
        given(socket.getInputStream()).willReturn(new ByteArrayInputStream(header.getBytes()));
        given(socket.getOutputStream()).willReturn(new ByteArrayOutputStream(0));

        HttpResponse response = mock(HttpResponse.class);
        HandyHttpdServer server = mock(HandyHttpdServer.class);
        given(server.onRequest(any(HttpRequest.class))).willReturn(response);

        new HttpSession(server, socket).run();

        ArgumentCaptor<HttpRequest> arg = ArgumentCaptor.forClass(HttpRequest.class);

        then(server).should(times(1)).onRequest(arg.capture());

        HttpRequest request = arg.getValue();
        assertEquals("POST", request.getMethod());
        assertEquals("/路径路径", request.getUri());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("tools.ietf.org", request.getHeaders().get("host"));
        assertEquals("keep-alive", request.getHeaders().get("connection"));
        assertEquals("no-cache", request.getHeaders().get("pragma"));
        assertEquals("no-cache", request.getHeaders().get("cache-control"));
        assertEquals("1", request.getHeaders().get("upgrade-insecure-requests"));
        assertEquals("gzip, deflate, br", request.getHeaders().get("accept-encoding"));
        assertEquals("zh-CN,zh;q=0.9,en;q=0.8", request.getHeaders().get("accept-language"));
        assertEquals("值1", request.getParams().get("参数1"));
        assertEquals("值2", request.getParams().get("参数2"));
        assertEquals("PostBody", request.getParams().get("postData"));
    }

    @Test
    public void getBoundary() {
        String contentType = "multipart/form-data; boundary=rxU1IcP2kHsJVF37W5; charset=UTF-8";
        assertEquals("--rxU1IcP2kHsJVF37W5", HttpSession.getBoundary(contentType));

        contentType = "multipart/form-data; boundary = rxU1IcP2kHsJVF37W5; charset=UTF-8";
        assertEquals("--rxU1IcP2kHsJVF37W5", HttpSession.getBoundary(contentType));

        contentType = "boundary=rxU1IcP2kHsJVF37W5; charset=UTF-8; multipart/form-data";
        assertEquals("--rxU1IcP2kHsJVF37W5", HttpSession.getBoundary(contentType));
    }
}