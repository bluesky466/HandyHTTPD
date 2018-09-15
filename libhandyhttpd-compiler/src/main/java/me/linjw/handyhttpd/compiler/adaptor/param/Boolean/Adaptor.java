package me.linjw.handyhttpd.compiler.adaptor.param.Boolean;

import javax.lang.model.element.VariableElement;

import me.linjw.handyhttpd.compiler.adaptor.ParamAdaptor;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public class Adaptor extends ParamAdaptor {
    @Override
    public String getConvertCode(String httpRequest, VariableElement param) {
        return getBasicDataTypeConvertCode(httpRequest, param, "Boolean", "false");
    }
}
