package me.linjw.handyhttpd.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import me.linjw.handyhttpd.HandyHttpd;
import me.linjw.handyhttpd.annotation.Get;
import me.linjw.handyhttpd.annotation.Header;
import me.linjw.handyhttpd.annotation.Post;
import me.linjw.handyhttpd.annotation.Param;
import me.linjw.handyhttpd.annotation.Path;
import me.linjw.handyhttpd.httpcore.HttpRequest;
import me.linjw.handyhttpd.httpcore.HttpResponse;

/**
 * Created by linjiawei on 2018/7/5.
 * e-mail : bluesky466@qq.com
 */

public class SimpleService {
    private SimpleService mTarget;

    public SimpleService(SimpleService target) {
        mTarget = target;
    }

    @Path("/testParm")
    public String testParam(String str,
                            boolean bool,
                            Boolean Bool,
                            byte b,
                            Byte B,
                            char c,
                            Character C,
                            double d,
                            Double D,
                            float f,
                            Float F,
                            int i,
                            Integer I,
                            long l,
                            Long L,
                            short s,
                            Short S) {
        return mTarget.testParam(str, bool, Bool, b, B, c, C, d, D, f, F, i, I, l, L, s, S);
    }

    @Path("/testParmAnn")
    public void testParmAnn(@Param String a, @Param("B") String b) {
        mTarget.testParmAnn(a, b);
    }

    @Path("/testParmMap")
    public void testParamMap(Map<String, String> params, Map<String, File> files) {
        mTarget.testParamMap(params, files);
    }

    @Path("/testParmHeader")
    public void testParmHeader(@Header() String host,
                               @Header("http-client-ip") String clientIp,
                               @Header Map<String, String> headers) {
        mTarget.testParmHeader(host, clientIp, headers);
    }

    @Path("/testParmHttpRequest")
    public void testParmHttpRequest(HttpRequest request) {
        mTarget.testParmHttpRequest(request);
    }

    @Get
    @Path("/testMethodGet")
    public void testMethodGet() {
        mTarget.testMethodGet();
    }

    @Post
    @Path("/testMethodPost")
    public void testMethodPost() {
        mTarget.testMethodPost();
    }

    @Get
    @Post
    @Path("/testMethodGetPostAnn")
    public void testMethodGetPostAnn() {
        mTarget.testMethodGetPostAnn();
    }

    @Path("/testMethodGetPostDefault")
    public void testMethodGetPostDefault() {
        mTarget.testMethodGetPostDefault();
    }

    @Path("/testStringResponse")
    public String testStringResponse() {
        mTarget.testStringResponse();
        return "testStringResponse";
    }

    @Path("/testFileResponse")
    public File testFileResponse() {
        mTarget.testFileResponse();
        File file = new File(".tmpTestParamFile");
        if (!file.exists()) {
            try {
                FileWriter writer = new FileWriter(file.getAbsoluteFile());
                writer.write("hello world");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Path("/testInputStreamResponse")
    public InputStream testInputStreamResponse() {
        mTarget.testInputStreamResponse();
        File file = new File(".tmpTestParamFile");
        if (!file.exists()) {
            try {
                FileWriter writer = new FileWriter(file.getAbsoluteFile());
                writer.write("hello world");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Path("/testHttpResponse")
    public HttpResponse testHttpResponse() {
        mTarget.testHttpResponse();
        return HandyHttpd.newResponse(HttpResponse.Status.REDIRECT, "Moved Permanently");
    }
}
