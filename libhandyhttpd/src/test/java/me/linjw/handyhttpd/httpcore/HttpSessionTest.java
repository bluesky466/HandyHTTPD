package me.linjw.handyhttpd.httpcore;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

        assertTrue(HttpSession.isEqual(bytes, 0, bytes.length, str));
        assertTrue(HttpSession.isEqual(bytes, "hello ".getBytes().length, bytes.length, "world"));
        assertFalse(HttpSession.isEqual(bytes, 1, bytes.length, "world"));
    }

    @Test
    public void findEnd() {
        byte[] bytes = "hello world\r\nhello world\r\n\r\n\n".getBytes();

        assertEquals(
                "hello world\r\n".getBytes().length,
                HttpSession.findEnd(bytes, bytes.length, "\r\n")
        );

        assertEquals(
                "hello world\r\nhello world\r\n\r\n".getBytes().length,
                HttpSession.findEnd(bytes, bytes.length, "\r\n\r\n")
        );

        assertEquals(
                bytes.length,
                HttpSession.findEnd(bytes, bytes.length, "\n\n")
        );

        assertEquals(
                -1,
                HttpSession.findEnd(bytes, bytes.length, "\r\r")
        );
    }

    @Test
    public void getDataBySuffixs() throws IOException {
        byte[] buf = new byte[1024];
        String data = "hello world\r\nhello world\r\n\r\n";
        InputStream is = new ByteArrayInputStream(data.getBytes());

        assertEquals(
                "hello world\r\n".getBytes().length,
                HttpSession.moveDataWithSuffix(is, buf, buf.length, "\r\n")
        );

        //this method will skip the InputStream when write data to buf,
        //so 'hello world\r\n' is skiped in the before step.
        assertEquals(
                "hello world\r\n\r\n".getBytes().length,
                HttpSession.moveDataWithSuffix(is, buf, buf.length, "\r\n\r\n")
        );

        is.reset();
        assertEquals(
                data.getBytes().length,
                HttpSession.moveDataWithSuffix(is, buf, buf.length, "\r\n\r\n")
        );

        is.reset();
        assertEquals(
                0,
                HttpSession.moveDataWithSuffix(is, buf, buf.length, "\r\r")
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
        byte[] buf = new byte[1024];
        Map<String, String> header = HttpSession.parseHeaderFields(is, buf, buf.length);
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
        byte[] buf = new byte[1024];

        assertEquals(headerFields.getBytes().length, is.available());

        String[] requestLine = HttpSession.parseRequestLine(is, buf, buf.length);
        assertNotNull(requestLine);
        assertEquals("GET", requestLine[HttpSession.REQUEST_LINE_METHOD]);
        assertEquals("/html/rfc2616", requestLine[HttpSession.REQUEST_LINE_URI]);
        assertEquals("HTTP/1.1", requestLine[HttpSession.REQUEST_LINE_VERSION]);

        //this method will skip the InputStream when parse request line,
        //so InputStream is empty now.
        assertEquals(0, is.available());
    }

    @Test
    public void run() throws IOException {
        String header = "GET /html/rfc2616?key=val HTTP/1.1\r\n" +
                "Host: tools.ietf.org\r\n" +
                "Connection: keep-alive\r\n" +
                "Pragma: no-cache\r\n" +
                "Cache-Control: no-cache\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8\r\n\r\n";

        Socket socket = mock(Socket.class);
        given(socket.getInputStream()).willReturn(new ByteArrayInputStream(header.getBytes()));
        given(socket.getOutputStream()).willReturn(new ByteArrayOutputStream(0));

        HttpResponse response = mock(HttpResponse.class);
        HandyHttpdServer server = mock(HandyHttpdServer.class);
        given(server.onRequest(any(HttpRequest.class))).willReturn(response);

        new HttpSession(server, socket).run();

        ArgumentCaptor<HttpRequest> arg = ArgumentCaptor.forClass(HttpRequest.class);

        then(server).should().onRequest(arg.capture());

        HttpRequest request = arg.getValue();
        assertEquals("GET", request.getMethod());
        assertEquals("/html/rfc2616", request.getUri());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("tools.ietf.org", request.getHeaders().get("host"));
        assertEquals("keep-alive", request.getHeaders().get("connection"));
        assertEquals("no-cache", request.getHeaders().get("pragma"));
        assertEquals("no-cache", request.getHeaders().get("cache-control"));
        assertEquals("1", request.getHeaders().get("upgrade-insecure-requests"));
        assertEquals("gzip, deflate, br", request.getHeaders().get("accept-encoding"));
        assertEquals("zh-CN,zh;q=0.9,en;q=0.8", request.getHeaders().get("accept-language"));
        assertEquals("val", request.getParams().get("key"));
    }
}