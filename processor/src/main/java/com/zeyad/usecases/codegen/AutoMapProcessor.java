package com.zeyad.usecases.codegen;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.zeyad.usecases.annotations.AutoMap;
import com.zeyad.usecases.annotations.FindMapped;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

import static com.squareup.javapoet.ParameterizedTypeName.get;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @author zeyad on 12/12/16.
 */
@SupportedAnnotationTypes("com.zeyad.usecases.annotations.AutoMap")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AutoMapProcessor extends AbstractProcessor {
    private ErrorReporter mErrorReporter;
    private Types mTypeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mErrorReporter = new ErrorReporter(processingEnv);
        mTypeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Collection<? extends Element> annotatedElements = env.getElementsAnnotatedWith(AutoMap.class);
        List<TypeElement> types = new ImmutableList.Builder<TypeElement>()
                .addAll(ElementFilter.typesIn(annotatedElements))
                .build();
        for (TypeElement type : types) {
            processType(type);
        }
        writeSourceFile("AutoMap_DAOMapperUtil", Reformatter.fixup(generateDAOUtilMapper(types)), null);
        // We are the only ones handling AutoMap annotations
        return true;
    }

    private void processType(TypeElement type) {
        if (type.getAnnotation(AutoMap.class) == null) {
            mErrorReporter.abortWithError("annotation processor for @AutoMap was invoked with a" +
                    "type annotated differently; compiler bug? O_o", type);
        }
        if (type.getKind() != ElementKind.CLASS) {
            mErrorReporter.abortWithError("@" + AutoMap.class.getName() + " only applies to classes", type);
        }
        if (ancestorIsAutoMap(type)) {
            mErrorReporter.abortWithError("One @AutoMap class shall not extend another", type);
        }
        checkModifiersIfNested(type);
        // get the fully-qualified class name
        String fqClassName = generatedSubclassName(type, 0);
        // class name
        String className = TypeUtil.simpleNameOf(fqClassName);
        writeSourceFile(className, Reformatter.fixup(generateDataClassFile(type, className)), type);
        writeSourceFile(className + "Mapper", Reformatter.fixup(generateDAOMapper(type, className)), type);
    }

    private void writeSourceFile(String className, String text, TypeElement originatingType) {
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className, originatingType);
            Writer writer = sourceFile.openWriter();
            try {
                writer.write(text);
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            // This should really be an error, but we make it a warning in the hope of resisting Eclipse
            // bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=367599. If that bug manifests, we may get
            // invoked more than once for the same file, so ignoring the ability to overwrite it is the
            // right thing to do. If we are unable to write for some other reason, we should get a compile
            // error later because user code will have a reference to the code we were supposed to
            // generate (new AutoValue_Foo() or whatever) and that reference will be undefined.
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Could not write generated class " + className + ": " + e);
        }
    }

    private boolean ancestorIsAutoMap(TypeElement type) {
        while (true) {
            TypeMirror parentMirror = type.getSuperclass();
            if (parentMirror.getKind() == TypeKind.NONE) {
                return false;
            }
            TypeElement parentElement = (TypeElement) mTypeUtils.asElement(parentMirror);
            if (MoreElements.isAnnotationPresent(parentElement, AutoMap.class)) {
                return true;
            }
            type = parentElement;
        }
    }

    private String generatedSubclassName(TypeElement type, int depth) {
        return generatedClassName(type, Strings.repeat("$", depth) + "AutoMap_");
    }

    private String generatedClassName(TypeElement type, String prefix) {
        String name = type.getSimpleName().toString();
        while (type.getEnclosingElement() instanceof TypeElement) {
            type = (TypeElement) type.getEnclosingElement();
            name = type.getSimpleName() + "_" + name;
        }
        String pkg = TypeUtil.packageNameOf(type);
        String dot = pkg.length() == 0 ? "" : ".";
        return pkg + dot + prefix + name;
    }

    private void checkModifiersIfNested(TypeElement type) {
        ElementKind enclosingKind = type.getEnclosingElement().getKind();
        if (enclosingKind.isClass() || enclosingKind.isInterface()) {
//            if (type.getModifiers().contains(PRIVATE)) {
//                mErrorReporter.abortWithError("@AutoMap class must not be private", type);
//            }
            if (!type.getModifiers().contains(STATIC)) {
                mErrorReporter.abortWithError("Nested @AutoMap class must be static", type);
            }
        }
        // In principle type.getEnclosingElement() could be an ExecutableElement (for a class
        // declared inside a method), but since RoundEnvironment.getElementsAnnotatedWith doesn't
        // return such classes we won't see them here.
    }

    // TODO: 1/4/17 Clean up!
    private String generateDataClassFile(TypeElement type, String className) {
        String pkg = TypeUtil.packageNameOf(type);

        String isEmptyParameterName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, className)
                .substring(0, 1).toLowerCase() + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
                className).substring(1);

        MethodSpec.Builder isEmptyBuilder = MethodSpec.methodBuilder("isEmpty")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(boolean.class);
        String isEmptyImplementation = "return " + isEmptyParameterName + " == null ||\n(";

        // constructor
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .superclass(ClassName.get("io.realm", "RealmObject"))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constructor);

        // get the properties
        List<? extends Element> allElements = type.getEnclosedElements();
        for (int i = 0, allElementsSize = allElements.size(); i < allElementsSize; i++) {
            Element element = allElements.get(i);
            if (element.getKind().isField()) {
                if (element.getAnnotation(Ignore.class) == null) {
                    TypeName typeName = TypeName.get(element.asType());
                    String variableName = element.getSimpleName().toString();
                    FieldSpec.Builder builder;
                    // TODO: 12/18/16 Try to remove annotation!
                    if (element.getAnnotation(FindMapped.class) == null)
                        builder = FieldSpec.builder(typeName, variableName);
                    else {
                        if (isCollection(typeName.toString())) {
                            String name = typeName.toString().split("<")[1];
                            String[] split = name.split("\\.");
                            String vName = split[split.length - 1];
                            vName = "AutoMap_" + vName.split(">")[0];
                            typeName = get(ClassName.get("io.realm", "RealmList"),
                                    ClassName.get(pkg, pkg + "." + vName));
                            builder = FieldSpec.builder(typeName, variableName);
                        } else {
                            String[] split = typeName.toString().split("\\.");
                            String vName = "AutoMap_" + split[split.length - 1];
                            typeName = ClassName.get(pkg, pkg + "." + vName);
                            builder = FieldSpec.builder(typeName, variableName);
                        }
                    }
                    // add field
                    for (Modifier modifier : element.getModifiers()) {
                        builder.addModifiers(modifier);
                    }
                    if (element.getAnnotation(PrimaryKey.class) != null)
                        builder = builder.addAnnotation(PrimaryKey.class);
                    if (element.getAnnotation(SerializedName.class) != null)
                        builder = builder.addAnnotation(AnnotationSpec.builder(SerializedName.class)
                                .addMember("value", "$S", element.getAnnotation(SerializedName.class)
                                        .value())
                                .build());
                    String variableNameCamelCase = variableName.substring(0, 1).toUpperCase() + variableName.substring(1);
                    if (!(element.getModifiers().contains(Modifier.FINAL) && element.getModifiers()
                            .contains(Modifier.STATIC))) {
                        // add setter
                        classBuilder.addMethod(MethodSpec.methodBuilder("set" + variableNameCamelCase)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(void.class)
                                .addParameter(typeName, variableName)
                                .addCode("this.$N = $N;", variableName, variableName)
                                .build());
                        // add getter
                        classBuilder.addMethod(MethodSpec.methodBuilder("get" + variableNameCamelCase)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(typeName)
                                .addCode("return $N;", variableName)
                                .build());
                        if (!element.asType().getKind().isPrimitive()) {
                            isEmptyImplementation += isEmptyParameterName + "." + variableName + " == null &&\n";
                        }
                    } else {
                        // TODO: 12/16/16 Get value
                        builder.initializer("$S", variableNameCamelCase.substring(0, 1).toLowerCase()
                                + variableNameCamelCase.substring(1));
                    }
                    classBuilder.addField(builder.build());
                }
            }
        }
        isEmptyBuilder.addParameter(ClassName.get(pkg, className), isEmptyParameterName);
        isEmptyImplementation = isEmptyImplementation.substring(0, isEmptyImplementation.length() - 4) + ");";
        MethodSpec isEmpty = isEmptyBuilder.addCode(isEmptyImplementation).build();

        classBuilder.addMethod(isEmpty);

        return JavaFile.builder(pkg, classBuilder.build()).build().toString();
    }

    // TODO: 1/4/17 Clean up!
    private String generateDAOMapper(TypeElement type, String className) {
        List<? extends Element> allElements = type.getEnclosedElements();
        String pkg = TypeUtil.packageNameOf(type);
        String parameterName = type.getSimpleName().toString();

        String paramName = "autoMap_" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,
                parameterName);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("mapToDomainManual")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(ClassName.get(pkg, pkg + "." + "AutoMap_" + parameterName), paramName)
                .returns(TypeName.get(type.asType()));

        MethodSpec.Builder staticBuilder = MethodSpec.methodBuilder("staticMapToDomainManual")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(ClassName.get(pkg, pkg + "." + "AutoMap_" + parameterName), paramName)
                .returns(TypeName.get(type.asType()));

        CodeBlock.Builder implementation = CodeBlock.builder();
        implementation.beginControlFlow("if ($N.isEmpty($N))", ClassName.get("",
                pkg + "." + "AutoMap_" + parameterName).simpleName(), paramName);
        implementation.addStatement("return new $N()", className.split("_")[1]);
        implementation.endControlFlow();

        String resultVariableName = parameterName.substring(0, 1).toLowerCase()
                + parameterName.substring(1);
        implementation.add(parameterName + " " + resultVariableName + " = new " + parameterName + "();\n");

        for (int i = 0, allElementsSize = allElements.size(); i < allElementsSize; i++) {
            Element element = allElements.get(i);
            if (element.getAnnotation(Ignore.class) == null && element.getKind().isField()
                    && !(element.getModifiers().contains(Modifier.FINAL) && element.getModifiers()
                    .contains(Modifier.STATIC))) {
                String variableName = element.getSimpleName().toString();
                variableName = variableName.substring(0, 1).toUpperCase() + variableName.substring(1);

                if (element.getAnnotation(FindMapped.class) == null)
                    implementation.addStatement("$N.set$N($N.get$N())", resultVariableName, variableName,
                            paramName, variableName);
                else {
                    TypeName typeName = TypeName.get(element.asType());
                    if (isCollection(typeName.toString())) {
                        boolean isPrimitive = !element.asType().getKind().isPrimitive();

                        String listName = variableName.substring(0, 1).toLowerCase() + variableName
                                .substring(1);
                        implementation.addStatement("$N $N = new $L<>()", typeName.toString(), listName,
                                ClassName.get("java.util", "ArrayList"));

                        if (isPrimitive)
                            implementation.beginControlFlow("if ($N.get$N() != null)", paramName,
                                    variableName);

                        String name = typeName.toString().split("<")[1];
                        String[] split = name.split("\\.");
                        String currentItemType = split[split.length - 1];
                        currentItemType = "AutoMap_" + currentItemType.split(">")[0];
                        String currentItemName = listName.substring(0, listName.length() - 1);

                        implementation.beginControlFlow("for ($N $N : $N.get$N())", pkg + "." + currentItemType,
                                currentItemName, paramName, variableName);
                        implementation.addStatement("$N.add($NMapper.staticMapToDomainManual($N))",
                                listName, pkg + "." + currentItemType, currentItemName);
                        implementation.endControlFlow();
                        if (isPrimitive)
                            implementation.endControlFlow();
                        implementation.addStatement("$N.set$N($N)", resultVariableName, variableName,
                                listName);
                    } else {
                        String[] fullPath = typeName.toString().split("\\.");
                        String staticMapper = typeName.toString().replace(fullPath[fullPath.length - 1],
                                "AutoMap_" + fullPath[fullPath.length - 1] + "Mapper");
                        implementation.addStatement("$N.set$N($N.staticMapToDomainManual($N.get$N()))",
                                resultVariableName, variableName, staticMapper,
                                paramName, variableName);
                    }
                }
            }
        }

        implementation.addStatement("return $N", resultVariableName);

        builder.addCode("return staticMapToDomainManual($N);", paramName);
        MethodSpec mapToDomainManual = builder.build();

        staticBuilder.addCode(implementation.build());
        MethodSpec staticMapToDomainManual = staticBuilder.build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addCode("super();")
                .build();

        String singletonName = "s" + className + "Mapper";

        MethodSpec getInstance = MethodSpec.methodBuilder("getInstance")
                .addModifiers(PUBLIC, STATIC)
                .addCode(CodeBlock.builder()
                        .beginControlFlow("if ($N == null)", singletonName)
                        .addStatement("$N =  new $N()", singletonName, ClassName.get(pkg,
                                className + "Mapper").simpleName())
                        .endControlFlow()
                        .addStatement("return $N", singletonName)
                        .build())
                .returns(ClassName.get(pkg, className + "Mapper"))
                .build();

        TypeName superClass = get(ClassName.get("com.zeyad.usecases.data.mappers",
                "DAOMapper"), ClassName.get(pkg, className.split("_")[1]), ClassName.get(pkg,
                pkg + "." + className));

        return JavaFile.builder(pkg, TypeSpec.classBuilder(className + "Mapper")
                .superclass(superClass)
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(ClassName.get(pkg, className + "Mapper"),
                        singletonName)
                        .addModifiers(PRIVATE, STATIC)
                        .build())
                .addMethod(constructor)
                .addMethod(mapToDomainManual)
                .addMethod(staticMapToDomainManual)
                .addMethod(getInstance)
                .build()).build().toString();
    }

    // TODO: 1/4/17 Clean up!
    private String generateDAOUtilMapper(List<TypeElement> types) {
        String pkg = "com.zeyad.usecases.app";
        String mappersPkg = "com.zeyad.usecases.data.mappers";

        MethodSpec.Builder getDataMapperBuilder = MethodSpec.methodBuilder("getDataMapper")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ParameterSpec.builder(ClassName.get(Class.class), "dataClass").build())
                .returns(ClassName.get(mappersPkg, "IDAOMapper"));

        CodeBlock.Builder getDataMapperCode = CodeBlock.builder();

        for (int i = 0, typesSize = types.size(); i < typesSize; i++) {
            TypeName typeName = TypeName.get(types.get(i).asType());

            getDataMapperCode.beginControlFlow("if (dataClass == $T.class)", typeName);

            String[] fullPath = typeName.toString().split("\\.");
            String staticMapper = typeName.toString().replace(fullPath[fullPath.length - 1],
                    "AutoMap_" + fullPath[fullPath.length - 1] + "Mapper");
            getDataMapperCode.addStatement("return $T.getInstance()", ClassName
                    .get("", staticMapper));

            getDataMapperCode.endControlFlow();
        }

        getDataMapperCode.addStatement("return $T.getInstance()", ClassName.get(mappersPkg, "DefaultDAOMapper"));
        getDataMapperBuilder.addCode(getDataMapperCode.build());

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("AutoMap_DAOMapperUtil")
                .superclass(ClassName.get(mappersPkg, "DAOMapperUtil"))
                .addModifiers(Modifier.PUBLIC);
        classBuilder.addMethod(getDataMapperBuilder.build());
        return JavaFile.builder(pkg, classBuilder.build()).build().toString();
    }

    private boolean isCollection(String className) {
        return className.contains("List") || className.contains("Set") || className.contains("Queue")
                || className.contains("Collection") || className.contains("Iterable");
    }
}
