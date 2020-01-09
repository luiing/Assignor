package com.uis.decouple;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.uis.decouple.Hookup"})
public class DecoupleProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if(processingEnv.getElementUtils().getPackageElement("")!=null)
            System.out.println("---decouple processor start 0---"+processingEnv.getElementUtils().getPackageElement("com").getEnclosedElements().toString());
        else System.out.println("---decouple processor start 0---package null");

        System.out.println("---decouple processor setlist---"+set.toString());
        System.out.println("---decouple processor rootlist---"+roundEnvironment.getRootElements().toString());

            for (Element ele : roundEnvironment.getElementsAnnotatedWith(Hookup.class)) {
                System.out.println("---decouple processor---" + ele.getSimpleName()+","+ele.toString());
            }

        return false;
    }

    private void error( String msg, Object... args) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR,msg);
    }
}