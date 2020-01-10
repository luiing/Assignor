package com.uis.decouple;

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
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.uis.decouple.BindCouple"})
public class DecoupleProcessor extends AbstractProcessor {

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
        TypeMirror connectType = processingEnv.getElementUtils().getTypeElement(ICouple.class.getName()).asType();
            for (Element element : roundEnvironment.getElementsAnnotatedWith(BindCouple.class)) {
                if(element.getKind() == ElementKind.CLASS) {
                    String clsName = element.getAnnotation(BindCouple.class).value();
                    System.out.println("---DecoupleProcessor name="+element.getSimpleName()+",value="+clsName);
                    if(clsName.isEmpty() || !clsName.matches("[a-zA-Z_]\\w*")){
                        System.out.println("---DecoupleProcessor generate fail:"+clsName+" is not java class name");
                    }else if(processingEnv.getTypeUtils().isSubtype(element.asType(),connectType)) {
                        createHookFile(clsName,element);
                        System.out.println("---DecoupleProcessor generate success---");
                    }else{
                        System.out.println("---DecoupleProcessor generate fail:" + element.toString()+" should implements OnDecouble");
                    }
                }
            }
        return false;
    }

    private void createHookFile(String clsName,Element element) {
        try {
            String pkgName = Generate.generatePkg;
            String extName = clsName+Generate.generate;
            JavaFileObject javaFile = filer.createSourceFile(pkgName+"."+extName);
            BufferedWriter writer = new BufferedWriter(javaFile.openWriter());
            writer.write("package "+pkgName+";\n\n");
            writer.write("import "+Generate.class.getName()+";\n\n");
            writer.write("import com.uis.assignor.couple.IDecouple;\n\n");
            writer.write("public class "+extName+"{\n\n");
            writer.write("    public final static IDecouple "+Generate.generateName+" = new "+element.toString()+"();\n\n");
            writer.write("}\n\n");
            writer.flush();
            writer.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}