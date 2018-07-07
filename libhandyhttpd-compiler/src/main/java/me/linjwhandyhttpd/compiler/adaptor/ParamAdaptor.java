package me.linjwhandyhttpd.compiler.adaptor;

import javax.lang.model.element.VariableElement;

import me.linjw.handyhttpd.annotation.Param;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public abstract class ParamAdaptor {
    abstract public String getConvertCode(String httpRequest, VariableElement param);

    protected static String getKeyName(VariableElement param) {
        Param keyName = param.getAnnotation(Param.class);
        if (keyName == null || keyName.value().isEmpty()) {
            return param.getSimpleName().toString();
        }
        return keyName.value();
    }

    protected static String getBasicDataTypeConvertCode(String httpRequest,
                                                        VariableElement param,
                                                        String type,
                                                        String defaultVal) {
        String key = ParamAdaptor.getKeyName(param);
        String map = httpRequest + ".getParams()";
        return map + ".containsKey(\"" + key + "\")" +
                "?" + type + ".parse" + type + "(" + map + ".get(\"" + key + "\"))" +
                ":" + defaultVal;
    }
}
