package me.linjw.handyhttpd.compiler.adaptor.param.java.lang.String;

import javax.lang.model.element.VariableElement;

import me.linjw.handyhttpd.compiler.adaptor.ParamAdaptor;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public class Adaptor extends ParamAdaptor {
    @Override
    public String getConvertCode(String httpRequest, VariableElement param) {
        String key = getKeyName(param);
        return isHeaderParam(param)
                ? httpRequest + ".getHeader(\"" + key + "\")"
                : httpRequest + ".getParam(\"" + key + "\")";
    }
}
