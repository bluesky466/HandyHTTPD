package me.linjwhandyhttpd.compiler.adaptor.param.java.lang.Byte;

import javax.lang.model.element.VariableElement;

import me.linjwhandyhttpd.compiler.adaptor.ParamAdaptor;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public class Adaptor extends ParamAdaptor {
    @Override
    public String getConvertCode(String httpRequest, VariableElement param) {
        return ParamAdaptor.getBasicDataTypeConvertCode(httpRequest, param, "Byte", "null");
    }
}
