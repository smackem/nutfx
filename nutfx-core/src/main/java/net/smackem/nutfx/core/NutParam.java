package net.smackem.nutfx.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NutParam {
    String value();
    boolean isRequired() default false;
}
