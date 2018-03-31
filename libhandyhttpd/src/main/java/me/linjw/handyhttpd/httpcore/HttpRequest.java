package me.linjw.handyhttpd.httpcore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */


@SuppressWarnings("WeakerAccess")
public class HttpRequest {
    private String mMethod;
    private String mUri;
    private String mVersion;
    private Map<String, String> mHeaders;
    private Map<String, String> mParams;

    public HttpRequest(String method, String uri, String version, Map<String, String> headers) {
        mMethod = method;
        mVersion = version;
        mHeaders = headers;

        int posQuery = uri.indexOf("?");
        if (posQuery == -1) {
            mUri = uri;
            mParams = new HashMap<>();
        } else {
            mUri = uri.substring(0, posQuery);
            mParams = parseParams(uri.substring(posQuery + 1));
        }
    }

    public String getMethod() {
        return mMethod;
    }

    public String getUri() {
        return mUri;
    }

    public String getVersion() {
        return mVersion;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public static Map<String, String> parseParams(String query) {
        Map<String, String> params = new HashMap<>();

        if (query == null || query.isEmpty()) {
            return params;
        }

        for (String param : query.split("&")) {
            String[] list = param.split("=");
            if (list.length > 1) {
                params.put(list[0], list[1]);
            }
        }
        return params;
    }

}

