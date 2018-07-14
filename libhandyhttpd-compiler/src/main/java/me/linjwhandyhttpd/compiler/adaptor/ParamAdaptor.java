package me.linjwhandyhttpd.compiler.adaptor;

import javax.lang.model.element.VariableElement;

import me.linjw.handyhttpd.annotation.Header;
import me.linjw.handyhttpd.annotation.Key;
import me.linjw.handyhttpd.annotation.Param;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

public abstract class ParamAdaptor {
    abstract public String getConvertCode(String httpRequest, VariableElement param);

    protected static String getKeyName(VariableElement param) {
        Param paramName = param.getAnnotation(Param.class);
        Header header = param.getAnnotation(Header.class);
        Key key = param.getAnnotation(Key.class);
        if (header != null && !header.value().isEmpty()) {
            return header.value();
        } else if (paramName != null && !paramName.value().isEmpty()) {
            return paramName.value();
        } else if (key != null && !key.value().isEmpty()) {
            return key.value();
        }
        return param.getSimpleName().toString();

    }

    protected static boolean isHeaderParam(VariableElement param) {
        return param.getAnnotation(Header.class) != null;
    }

    protected static String getBasicDataTypeConvertCode(String httpRequest,
                                                        VariableElement param,
                                                        String type,
                                                        String defaultVal) {
        String key = ParamAdaptor.getKeyName(param);
        String map = httpRequest + (isHeaderParam(param) ? ".getHeaders()" : ".getParams()");
        return map + ".containsKey(\"" + key + "\")" +
                "?" + type + ".parse" + type + "(" + map + ".get(\"" + key + "\"))" +
                ":" + defaultVal;
    }
}
