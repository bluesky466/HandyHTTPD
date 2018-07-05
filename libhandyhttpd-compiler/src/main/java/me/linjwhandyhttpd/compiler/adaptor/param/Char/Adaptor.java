package me.linjwhandyhttpd.compiler.adaptor.param.Char;

import javax.lang.model.element.VariableElement;

import me.linjwhandyhttpd.compiler.adaptor.ParamAdaptor;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public class Adaptor extends ParamAdaptor {
    @Override
    public String getConvertCode(String httpRequest, VariableElement param) {
        String key = ParamAdaptor.getKeyName(param);
        String map = httpRequest + ".getParams()";
        return map + ".containsKey(\"" + key + "\")"
                + "?" + map + ".get(\"" + key + "\").charAt(0)"
                + ":'\\0'";
    }
}
