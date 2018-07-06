package me.linjw.handyhttpd.samples;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import me.linjw.handyhttpd.HandyHttpd;

import static org.mockito.BDDMockito.then;

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
        accessServicePath("/testParm");
        then(mService)
                .should()
                .testParam(null, false, null, (byte) 0, null, '\0', null, 0.0, null, 0.0f, null, 0, null, 0, null, (short) 0, null);

        accessServicePath("/testParm?str=str&bool=true&Bool=false&b=1&B=2&c=a&C=b&d=1.23&D=2.34&f=3.21&F=4.32&i=123&I=456&l=321&L=654&s=111&S=11");
        then(mService)
                .should()
                .testParam("str", true, Boolean.FALSE, (byte) 1, (byte) 2, 'a', 'b', 1.23, 2.34, 3.21f, 4.32f, 123, 456, 321, 654l, (short) 111, (short) 11);
    }

    @Test
    public void testParamMap() throws IOException {
        accessServicePath("/testParmMap");
        then(mService)
                .should()
                .testParamMap(new HashMap<String, String>(), new HashMap<String, File>());

        Map<String, String> params = new HashMap<String, String>() {{
            put("a", "1");
            put("b", "2");
        }};
        accessServicePath("/testParmMap?a=1&b=2");
        then(mService)
                .should()
                .testParamMap(params, new HashMap<String, File>());
    }

    private InputStream accessServicePath(String path) throws IOException {
        URL url = new URL("http://127.0.0.1:" + PORT + path);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);
        conn.connect();

        return conn.getInputStream();
    }
}
