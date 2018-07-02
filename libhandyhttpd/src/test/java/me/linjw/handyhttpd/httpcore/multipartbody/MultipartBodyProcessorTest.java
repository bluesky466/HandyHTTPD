package me.linjw.handyhttpd.httpcore.multipartbody;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by linjiawei on 2018/7/2.
 * e-mail : bluesky466@qq.com
 */
public class MultipartBodyProcessorTest {
    @Test
    public void process() throws IOException {
        String data = "------WebKitFormBoundaryAjnbFKAbKcHGcUJ9\r\n" +
                "Content-Disposition: form-data; name=\"firstname\"\r\n" +
                "\r\n" +
                "Mickey\r\n" +
                "------WebKitFormBoundaryAjnbFKAbKcHGcUJ9\r\n" +
                "Content-Disposition: form-data; name=\"lastname\"\r\n" +
                "\r\n" +
                "Mouse\r\n" +
                "------WebKitFormBoundaryAjnbFKAbKcHGcUJ9\r\n" +
                "Content-Disposition: form-data; name=\"lastname\" filename=\"lastfile\"\r\n" +
                "\r\n" +
                "Mouse\r\n" +
                "------WebKitFormBoundaryAjnbFKAbKcHGcUJ9--";

        MultipartBodyProcessor processor = new MultipartBodyProcessor();
        processor.setBoundary("----WebKitFormBoundaryAjnbFKAbKcHGcUJ9");
        Map<String, String> params = new HashMap<>();
        Map<String, File> files = new HashMap<>();

        processor.process(data.getBytes(), data.length(), params, files, ".");

        assertEquals(2, params.size());
        assertEquals("Mickey", params.get("firstname"));
        assertEquals("Mouse", params.get("lastname"));

        assertEquals(1, files.size());
        FileReader reader = new FileReader(files.get("lastname"));
        assertEquals("Mouse",new BufferedReader(reader).readLine());
    }
}