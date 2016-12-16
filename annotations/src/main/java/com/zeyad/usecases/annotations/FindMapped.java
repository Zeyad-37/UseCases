package com.zeyad.usecases.annotations;

/**
 * @author zeyad on 12/16/16.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zeyad on 12/12/16.
 */
@Target(ElementType.FIELD) // on field level
@Retention(RetentionPolicy.SOURCE) // not needed at runtime
public @interface FindMapped {
}
