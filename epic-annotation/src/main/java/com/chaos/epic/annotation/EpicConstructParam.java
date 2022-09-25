package com.chaos.epic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * hook construct
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EpicConstructParam {
    String className() default "";
}