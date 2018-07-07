package me.linjw.handyhttpd.samples;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import me.linjw.handyhttpd.HandyHttpd;
import me.linjw.handyhttpd.httpcore.HttpRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * Created by linjiawei on 2018/7/5.
 * e-mail : bluesky466@qq.com
 */

@RunWith(PowerMockRunner.class)
public class ServiceTest {
    private static final int PORT = 8888;

    @Mock
    SimpleService mService;

    HandyHttpd.Server mServer;

    @Before
    public void setup() throws
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
        mServer = new HandyHttpd.ServerBuilder()
                .setTempFileDir(".")
                .loadService(new SimpleService(mService))
                .createAndStart(PORT);

        while (!mServer.isReady()) {
            ; //wait server ready
        }
    }

    @After
    public void cleanup() throws IOException, InterruptedException {
        mServer.stop();

        //wait server stop
        Thread.sleep(500);
    }

    @Test
    public void testParam() throws IOException {
        assertEquals(200, accessServicePath("/testParm", "GET").getResponseCode());
        then(mService)
                .should()
                .testParam(null, false, null, (byte) 0, null, '\0', null, 0.0, null, 0.0f, null, 0, null, 0, null, (short) 0, null);

        assertEquals(200, accessServicePath("/testParm?str=str&bool=true&Bool=false&b=1&B=2&c=a&C=b&d=1.23&D=2.34&f=3.21&F=4.32&i=123&I=456&l=321&L=654&s=111&S=11", "GET").getResponseCode());
        then(mService)
                .should()
                .testParam("str", true, Boolean.FALSE, (byte) 1, (byte) 2, 'a', 'b', 1.23, 2.34, 3.21f, 4.32f, 123, 456, 321, 654l, (short) 111, (short) 11);
    }

    @Test
    public void testParamMap() throws IOException {
        assertEquals(200, accessServicePath("/testParmMap", "GET").getResponseCode());
        then(mService)
                .should()
                .testParamMap(new HashMap<String, String>(), new HashMap<String, File>());

        Map<String, String> params = new HashMap<String, String>() {{
            put("a", "1");
            put("b", "2");
        }};
        assertEquals(200, accessServicePath("/testParmMap?a=1&b=2", "GET").getResponseCode());
        then(mService)
                .should()
                .testParamMap(params, new HashMap<String, File>());
    }

    @Test
    public void testParmAnn() throws IOException {
        assertEquals(200, accessServicePath("/testParmAnn?a=123&b=321", "GET").getResponseCode());
        then(mService)
                .should()
                .testParmAnn("123", null);

        assertEquals(200, accessServicePath("/testParmAnn?a=123&B=321", "GET").getResponseCode());
        then(mService)
                .should()
                .testParmAnn("123", "321");
    }

    @Test
    public void testParmHeader() throws IOException {
        assertEquals(200, accessServicePath("/testParmHeader", "GET").getResponseCode());

        Class c = new HashMap<>().getClass();
        ArgumentCaptor<? extends HashMap> headers = ArgumentCaptor.forClass(c);
        then(mService).should().testParmHeader(eq("127.0.0.1:8888"), eq("127.0.0.1"), headers.capture());
        assertEquals("127.0.0.1", headers.getValue().get("remote-addr"));
        assertEquals("keep-alive", headers.getValue().get("connection"));
    }

    @Test
    public void testParmHttpRequest() throws IOException {
        assertEquals(200, accessServicePath("/testParmHttpRequest", "GET").getResponseCode());
        then(mService).should().testParmHttpRequest(notNull(HttpRequest.class));
    }

    @Test
    public void testGetMethod() throws IOException, InterruptedException {
        assertEquals(404, accessServicePath("/testMethodGet", "POST").getResponseCode());
        then(mService).should(never()).testMethodGet();

        assertEquals(200, accessServicePath("/testMethodGet", "GET").getResponseCode());
        then(mService).should().testMethodGet();

        assertEquals(404, accessServicePath("/testMethodPost", "GET").getResponseCode());
        then(mService).should(never()).testMethodPost();

        assertEquals(200, accessServicePath("/testMethodPost", "POST").getResponseCode());
        then(mService).should().testMethodPost();

        assertEquals(200, accessServicePath("/testMethodGetPostAnn", "GET").getResponseCode());
        assertEquals(200, accessServicePath("/testMethodGetPostAnn", "POST").getResponseCode());
        then(mService).should(times(2)).testMethodGetPostAnn();

        assertEquals(200, accessServicePath("/testMethodGetPostDefault", "GET").getResponseCode());
        assertEquals(200, accessServicePath("/testMethodGetPostDefault", "POST").getResponseCode());
        then(mService).should(times(2)).testMethodGetPostDefault();
    }

    @Test
    public void testStringResponse() throws IOException {
        HttpURLConnection conn = accessServicePath("/testStringResponse", "GET");

        assertEquals(200, conn.getResponseCode());
        assertEquals("testStringResponse", inputStreamToString(conn.getInputStream()));
        then(mService).should().testStringResponse();
    }

    @Test
    public void testHttpResponse() throws IOException {
        HttpURLConnection conn = accessServicePath("/testHttpResponse", "GET");

        assertEquals(301, conn.getResponseCode());
        assertEquals("Moved Permanently", inputStreamToString(conn.getInputStream()));
        then(mService).should().testHttpResponse();
    }

    private String inputStreamToString(InputStream is) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();
        while (line != null) {
            builder.append(line);
            line = reader.readLine();
        }
        return builder.toString();
    }

    private HttpURLConnection accessServicePath(String path, String method) throws IOException {
        URL url = new URL("http://127.0.0.1:" + PORT + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod(method);
        conn.connect();
        return conn;
    }
}
