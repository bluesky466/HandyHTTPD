package me.linjw.handyhttpd.samples;

import java.io.File;
import java.util.Map;

import me.linjw.handyhttpd.HandyHttpd;
import me.linjw.handyhttpd.annotation.GET;
import me.linjw.handyhttpd.annotation.POST;
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

    @Path("/testParmHttpRequest")
    public void testParmHttpRequest(HttpRequest request) {
        mTarget.testParmHttpRequest(request);
    }

    @GET
    @Path("/testMethodGet")
    public void testMethodGet() {
        mTarget.testMethodGet();
    }

    @POST
    @Path("/testMethodPost")
    public void testMethodPost() {
        mTarget.testMethodPost();
    }

    @GET
    @POST
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

    @Path("/testHttpResponse")
    public HttpResponse testHttpResponse() {
        mTarget.testHttpResponse();
        return HandyHttpd.newResponse(HttpResponse.Status.REDIRECT, "Moved Permanently");
    }
}
