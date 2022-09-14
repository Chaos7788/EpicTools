package com.chaos.epic.processor;

import com.chaos.epic.annotation.AfterMethod;
import com.chaos.epic.annotation.BeforeMethod;
import com.chaos.epic.annotation.EpicParam;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class EpicProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.size() == 0) {
            return false;
        }

        //EpicHookCollection
        MethodSpec method_init = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .build();

        TypeSpec epicHookCollection = TypeSpec.classBuilder("EpicHookCollection")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(method_init)
                .build();


        Filer filer = processingEnv.getFiler();
        JavaFile javaFile = JavaFile
                .builder("com.chaos.epic.tools", epicHookCollection)
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("process set size = " + set.size());
//        Set<? extends Element> epicParamElements = roundEnvironment
//                .getElementsAnnotatedWith(EpicParam.class);


        return true;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add(EpicParam.class.getCanonicalName());
        set.add(BeforeMethod.class.getCanonicalName());
        set.add(AfterMethod.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }


}
