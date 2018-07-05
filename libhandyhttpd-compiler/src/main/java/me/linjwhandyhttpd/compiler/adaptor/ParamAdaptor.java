package me.linjwhandyhttpd.compiler.adaptor;

import javax.lang.model.element.VariableElement;

import me.linjw.handyhttpd.annotation.Param;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public abstract class ParamAdaptor {
    abstract public String getConvertCode(String httpRequest, VariableElement param);

    public static String getKeyName(VariableElement param) {
        Param keyName = param.getAnnotation(Param.class);
        if (keyName == null) {
            return param.getSimpleName().toString();
        }
        return keyName.value();
    }
}
