package me.linjw.handyhttpd.compiler.adaptor.param.java.lang.Integer;

import javax.lang.model.element.VariableElement;

import me.linjw.handyhttpd.compiler.adaptor.ParamAdaptor;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public class Adaptor extends ParamAdaptor {
    @Override
    public String getConvertCode(String httpRequest, VariableElement param) {
        String key = ParamAdaptor.getKeyName(param);
        String map = httpRequest + (isHeaderParam(param) ? ".getHeaders()" : ".getParams()");
        return map + ".containsKey(\"" + key + "\")"
                + "?Integer.parseInt(" + map + ".get(\"" + key + "\"))"
                + ":null";
    }
}
