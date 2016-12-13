package com.zeyad.usecase.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * @author zeyad on 12/12/16.
 */
class TypeUtil {
    /**
     * Returns the name of the package that the given type is in. If the type is in the default
     * (unnamed) package then the name is the empty string.
     *
     * @param type given type
     * @return package name
     */
    static String packageNameOf(TypeElement type) {
        while (true) {
            Element enclosing = type.getEnclosingElement();
            if (enclosing instanceof PackageElement) {
                return ((PackageElement) enclosing).getQualifiedName().toString();
            }
            type = (TypeElement) enclosing;
        }
    }

    /**
     * Returns the simple class name once package is strip package.
     *
     * @param s fully qualified class name
     * @return simple class name
     */
    static String simpleNameOf(String s) {
        if (s.contains(".")) {
            return s.substring(s.lastIndexOf('.') + 1);
        } else {
            return s;
        }
    }

    static boolean isClassOfType(Types typeUtils, TypeMirror type, TypeMirror cls) {
        return type != null && typeUtils.isAssignable(cls, type);
    }
}
