package com.zeyad.usecases.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zeyad on 12/14/16.
 */
@Target(ElementType.TYPE) // on class level
@Retention(RetentionPolicy.SOURCE) // not needed at runtime
public @interface RealmPrimaryKey {
}
