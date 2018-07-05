package me.linjw.handyhttpd.httpcore;

import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import me.linjw.handyhttpd.exception.HandyException;

/**
 * Created by linjiawei on 2018/3/30.
 * e-mail : bluesky466@qq.com
 */


@SuppressWarnings("WeakerAccess")
public class HttpRequest {
    private final Method mMethod;
    private final String mUri;
    private final String mVersion;
    private final Map<String, String> mHeaders;
    private final Map<String, String> mParams;
    private final Map<String, File> mFiles;
    private final InetAddress mInetAddress;

    /**
     * parse params from url query.
     *
     * @param data data
     * @return params
     */
    public static Map<String, String> parseParams(String data) {
        Map<String, String> params = new HashMap<>();

        if (data == null || data.isEmpty()) {
            return params;
        }

        for (String param : data.split("&")) {
            String[] list = param.split("=");
            if (list.length > 1) {
                params.put(list[0], list[1]);
            }
        }
        return params;
    }

    public HttpRequest(String method,
                       String uri,
                       String version,
                       Map<String, String> headers,
                       InetAddress inetAddress) throws HandyException {
        mMethod = Method.toMethod(method);
        mVersion = version;
        mHeaders = headers;
        mInetAddress = inetAddress;
        int posQuery = uri.indexOf("?");
        if (posQuery == -1) {
            mUri = uri;
            mParams = new HashMap<>();
        } else {
            mUri = uri.substring(0, posQuery);
            mParams = parseParams(uri.substring(posQuery + 1));
        }
        mFiles = new HashMap<>();
    }

    public Method getMethod() {
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

    public String getParam(String key) {
        return mParams.get(key);
    }

    public Map<String, File> getFiles() {
        return mFiles;
    }

    public InetAddress getInetAddress() {
        return mInetAddress;
    }

    void putParam(String key, String val) {
        mParams.put(key, val);
    }

    void putParams(Map<String, String> params) {
        mParams.putAll(params);
    }

    void putFile(String name, File file) {
        mFiles.put(name, file);
    }

    void putFiles(Map<String, File> files) {
        mFiles.putAll(files);
    }

    public enum Method {
        GET,
        POST,
        PUT;

        private static Map<String, Method> sMap = new HashMap<>();

        static {
            for (Method method : Method.values()) {
                sMap.put(method.name(), method);
            }
        }

        public static Method toMethod(String method) throws HandyException {
            Method m = sMap.get(method.trim().toUpperCase());
            if (m == null) {
                throw new HandyException("can't find method [" + method + "]");
            }
            return m;
        }
    }
}

