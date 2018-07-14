package me.linjw.handyhttpd.httpcore;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import me.linjw.handyhttpd.exception.HandyException;

import static org.junit.Assert.assertEquals;

/**
 * Created by linjiawei on 2018/3/31.
 * e-mail : bluesky466@qq.com
 */
public class HttpRequestTest {
    @Test
    public void createHttpRequest() throws HandyException {
        Map<String, String> headers = new HashMap<>();
        HttpRequest request = new HttpRequest(
                "GET",
                "/path1/path2?key1=val1&key2=val2",
                "HTTP/1.1",
                headers,
                null,
                null
        );

        assertEquals(HttpRequest.Method.GET, request.getMethod());
        assertEquals("/path1/path2", request.getUri());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("val1", request.getParams().get("key1"));
        assertEquals("val2", request.getParams().get("key2"));
        assertEquals(headers, request.getHeaders());
    }

    @Test
    public void parseParams() throws Exception {
        Map<String, String> params = HttpRequest.parseParams("key1=val1&key2=val2");
        assertEquals("val1", params.get("key1"));
        assertEquals("val2", params.get("key2"));
    }
}