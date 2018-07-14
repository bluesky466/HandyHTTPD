package me.linjwhandyhttpd.compiler.adaptor.param.me.linjw.handyhttpd.httpcore.cookie.Cookie;

import javax.lang.model.element.VariableElement;

import me.linjwhandyhttpd.compiler.adaptor.ParamAdaptor;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public class Adaptor extends ParamAdaptor {
    @Override
    public String getConvertCode(String httpRequest, VariableElement param) {
        String key = getKeyName(param);
        return httpRequest + ".getCookie(\"" + key + "\")";
    }
}
