package com.uis.call;

import java.io.BufferedWriter;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.uis.call.BindCall"})
public class BindCallProcessor extends AbstractProcessor {

    Filer filer;
    Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(BindCall.class)) {
                if(element.getKind() == ElementKind.CLASS) {
                    String clsName = element.getAnnotation(BindCall.class).value();
                    System.out.println("---DecoupleProcessor name="+element.getSimpleName()+",value="+clsName);
                    if(clsName.isEmpty() || !clsName.matches("[a-zA-Z_]\\w*")){
                        System.out.println("---DecoupleProcessor generate fail:"+clsName+" is not java class name");
                    }else{
                        createJavaFile(clsName,element);
                    }
                }
            }
        return false;
    }

    private void createJavaFile(String clsName,Element element) {
        try {
            String pkgName = BindCallUtils.generatePkg;
            String extName = clsName + BindCallUtils.generate;
            JavaFileObject javaFile = filer.createSourceFile(pkgName+"."+extName);
            BufferedWriter writer = new BufferedWriter(javaFile.openWriter());
            writer.write("package "+pkgName+";\n\n");
            writer.write("import "+BindCallUtils.class.getName()+";\n");
            writer.write("public class "+extName+"{\n\n");
            writer.write("    public final Object "+clsName+" = new "+element.toString()+"();\n\n");
            writer.write("}\n");
            writer.flush();
            writer.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}