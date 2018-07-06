package me.linjw.handyhttpd.samples;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;

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
        accessServicePath("/test");
        then(mService).should().test(null);
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
