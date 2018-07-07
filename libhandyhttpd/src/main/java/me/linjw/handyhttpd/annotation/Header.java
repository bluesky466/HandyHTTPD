package me.linjw.handyhttpd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by linjiawei on 2018/7/7.
 * e-mail : bluesky466@qq.com
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.CLASS)
public @interface Header {
    /**
     * header name.
     *
     * @return header name
     */
    String value() default "";
}