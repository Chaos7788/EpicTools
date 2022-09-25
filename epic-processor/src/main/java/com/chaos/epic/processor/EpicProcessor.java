package com.chaos.epic.processor;

import com.chaos.epic.annotation.AfterMethod;
import com.chaos.epic.annotation.BeforeMethod;
import com.chaos.epic.annotation.EpicConstructParam;
import com.chaos.epic.annotation.EpicMethodParam;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.tools.javac.code.Type;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
public class EpicProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    /**
     * @param set              注解集合
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.size() == 0) {
            return false;
        }
        Filer filer = processingEnv.getFiler();


        //EpicHookCollection
        ClassName log = ClassName.get("android.util", "Log");
        MethodSpec.Builder initMethodBuild = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("$T.d($S, $S)", log, "EpicHookCollection", "init")
                .returns(TypeName.VOID);


        //EpicHookUtils.hookMethod("className", "methodName", null, "SimpleClassName$$methodNmaHookMethod")
        Set<? extends Element> epicMethodElements = roundEnvironment.getElementsAnnotatedWith(EpicMethodParam.class);
        for (Element element : epicMethodElements) {
            //1.获取需要hook的类名方法名 参数列表
            EpicMethodParam annotation = element.getAnnotation(EpicMethodParam.class);
            String className = annotation.className();
            String methodName = annotation.methodName();
            List<? extends TypeMirror> typeMirrors = null;
            try {
                Class[] paramTypes = annotation.paramType();
            } catch (MirroredTypesException e) {
                typeMirrors = e.getTypeMirrors();
            }


            // 2.构造XXXHook$$Proxy, 继承MethodHook
            String hookProxyName = element.getSimpleName().toString() + "$$Proxy";
            ClassName superMethodHook = ClassName
                    .get("com.chaos.epic.sample.tool.base", "MethodHook");
            TypeSpec.Builder proxyBuild = TypeSpec
                    .classBuilder(hookProxyName)
                    .superclass(superMethodHook)
                    .addModifiers(Modifier.PUBLIC);

            // 3.获取afterMethod beforeMethod注解，如果有
            // getEnclosedElements 获取元素的内部的包装元素
            // getEnclosingElement 获取包装这个元素的上层元素，如果是顶层了，返回包元素
            List<? extends Element> enclosedElements = element.getEnclosedElements();
            MethodSpec construct = null;
            FieldSpec realHook = null;
            for (Element enclosedElement : enclosedElements) {
                BeforeMethod beforeMethod = enclosedElement.getAnnotation(BeforeMethod.class);
                AfterMethod afterMethod = enclosedElement.getAnnotation(AfterMethod.class);
                MethodSpec methodSpec = null;
                if (beforeMethod != null) {
                    methodSpec = MethodSpec.methodBuilder("beforeHookedMethod").addModifiers(Modifier.PUBLIC)
                            .addStatement("super.beforeHookedMethod(param)")
                            .addParameter(
                                    ClassName.get(
                                            "de.robv.android.xposed.XC_MethodHook",
                                            "MethodHookParam"),
                                    "param")
                            .addAnnotation(Override.class)
                            .returns(TypeName.VOID)
                            .addStatement("mHook.$N()", enclosedElement.getSimpleName()).build();
                } else if (afterMethod != null) {
                    methodSpec = MethodSpec.methodBuilder("afterHookedMethod").addModifiers(Modifier.PUBLIC)
                            .addParameter(
                                    ClassName.get(
                                            "de.robv.android.xposed.XC_MethodHook",
                                            "MethodHookParam"),
                                    "param")
                            .addAnnotation(Override.class)
                            .addStatement("super.afterHookedMethod(param)")
                            .returns(TypeName.VOID)
                            .addStatement("mHook.$N()", enclosedElement.getSimpleName()).build();
                }

                if (methodSpec != null) {
                    ClassName type = null;
                    if (realHook == null) {
                        type = ClassName.get(element.getEnclosingElement().toString(),
                                element.getSimpleName().toString());
                        realHook = FieldSpec.builder(
                                type,
                                "mHook").build();
                        proxyBuild.addField(realHook);
                    }
                    if (construct == null) {
                        construct = MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addStatement("mHook = new $T()", type)
                                .build();
                        proxyBuild.addMethod(construct);
                    }
                    proxyBuild.addMethod(methodSpec);
                }
            }

            // 生成XXXHook$$Proxy
            TypeSpec proxy = proxyBuild.build();
            JavaFile proxyJavaFile = JavaFile
                    .builder("com.chaos.epic.tools", proxy)
                    .build();
            try {
                proxyJavaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // EpicHookCollection的init方法调用EpicHookUtils.hookMethod(className, methodName, methodHook, params)
            ClassName epicHookUtils = ClassName.get("com.chaos.epic.sample.tool", "EpicHookUtils");
            ClassName hookProxy = ClassName.get("com.chaos.epic.tools", hookProxyName);
            // 创建params
            Object[] clazz = null;
            StringBuilder paramBuilder = null;
            if (typeMirrors != null && typeMirrors.size() > 0) {
                clazz = new Object[typeMirrors.size() + 4];
                paramBuilder = new StringBuilder();
                // 构造{$T, $T}
                paramBuilder.append("new Object[]{");
                for (int i = 0; i < typeMirrors.size(); i++) {
                    TypeMirror mirror = typeMirrors.get(i);
                    Type declaredType = (Type) mirror;
                    TypeElement paramElement = (TypeElement) declaredType.asElement();
                    String packetName = paramElement.getEnclosingElement().toString();
                    String simpleName = paramElement.getSimpleName().toString();
                    ClassName param = ClassName.get(packetName, simpleName);
                    clazz[4 + i] = param;
                    paramBuilder.append("$T.class");
                    if (i != typeMirrors.size() - 1) {
                        paramBuilder.append(",");
                    }
                }
                paramBuilder.append("}");
            } else {
                clazz = new Object[4];
            }

            clazz[0] = epicHookUtils;
            clazz[1] = className;
            clazz[2] = methodName;
            clazz[3] = hookProxy;


            if (clazz.length > 4) {
                initMethodBuild.addStatement("$T.hookMethod($S, $S, new $T(), " + paramBuilder + ")", clazz);
            } else {
                initMethodBuild.addStatement("$T.hookMethod($S, $S, new $T())", clazz);
            }

        }

        MethodSpec method_init = initMethodBuild.build();
        TypeSpec epicHookCollection = TypeSpec.classBuilder("EpicHookCollection")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(method_init)
                .build();


        JavaFile javaFile = JavaFile
                .builder("com.chaos.epic.tools", epicHookCollection)
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("process set size = " + set.size());


        return true;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add(EpicConstructParam.class.getCanonicalName());
        set.add(BeforeMethod.class.getCanonicalName());
        set.add(AfterMethod.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }


}
