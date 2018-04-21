package me.linjw.handyhttpd.exception;

/**
 * Created by linjiawei on 2018/4/21.
 * e-mail : bluesky466@qq.com
 */

public class HandyException extends Exception {
    public HandyException(String message) {
        super(message);
    }

    public HandyException(String message, Exception e) {
        super(message, e);
    }
}
