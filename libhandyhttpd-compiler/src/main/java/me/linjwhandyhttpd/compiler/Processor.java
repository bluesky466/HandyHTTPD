package me.linjwhandyhttpd.compiler;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import me.linjw.handyhttpd.HandyHttpd;
import me.linjw.handyhttpd.annotation.Path;
import me.linjwhandyhttpd.compiler.adaptor.ParamAdaptor;
import me.linjwhandyhttpd.compiler.adaptor.ResponseAdaptor;

/**
 * Created by linjiawei on 2018/7/4.
 * e-mail : bluesky466@qq.com
 */

@SupportedAnnotationTypes({"me.linjw.handyhttpd.annotation.Path"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class Processor extends AbstractProcessor {
    private Map<String, ParamAdaptor> mParamAdaptorMap = new HashMap<>();
    private Map<String, ResponseAdaptor> mResponseAdaptorMap = new HashMap<>();
    private Elements mElementUtils;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element elem : roundEnvironment.getElementsAnnotatedWith(Path.class)) {
            ExecutableElement method = (ExecutableElement) elem;
            Element clazz = method.getEnclosingElement();
            PackageElement pkg = mElementUtils.getPackageOf(clazz);

            genServiceHandler(
                    pkg.getQualifiedName().toString(),
                    clazz.getSimpleName().toString(),
                    method);
        }

        return false;
    }

    private void genServiceHandler(String pkg, String clazz, ExecutableElement method) {
        String service = HandyHttpd.getServiceHandlerSimpleName(clazz, method.getSimpleName().toString());

        String packageLine = "package " + pkg + ";\n\n";
        String importLine = "import java.util.Map;\n" +
                "import me.linjw.handyhttpd.IServiceHandler;\n" +
                "import me.linjw.handyhttpd.HandyHttpd;\n" +
                "import me.linjw.handyhttpd.httpcore.HttpRequest;\n" +
                "import me.linjw.handyhttpd.httpcore.HttpResponse;\n\n";

        String classCode = "public class " + service + " implements IServiceHandler {\n" +
                "\tprivate " + clazz + " mService;\n\n" +
                "\tpublic " + service + "(" + clazz + " service) {\n" +
                "\t\tmService = service;\n" +
                "\t};\n\n" +
                "\t@Override\n" +
                "\tpublic HttpResponse onRequest(HttpRequest request) {\n" + getInvokeCode(method) +
                "\t}\n" +
                "}";

        generateCode(service, packageLine + importLine + classCode);
    }

    private String getInvokeCode(ExecutableElement method) {
        Iterator<? extends VariableElement> itParams = method.getParameters().iterator();
        StringBuilder builder = new StringBuilder()
                .append("mService.")
                .append(method.getSimpleName())
                .append("(");

        while (itParams.hasNext()) {
            VariableElement param = itParams.next();
            ParamAdaptor adaptor = getParamAdaptor(param);
            builder.append(adaptor.getConvertCode("request", param));

            if (!itParams.hasNext()) {
                break;
            }
            builder.append(", ");
        }

        builder.append(");\n");
        return getResponseAdaptor(method.getReturnType())
                .getConvertCode(builder.toString());
    }

    private ParamAdaptor getParamAdaptor(VariableElement param) {
        String clazz = param.asType().toString();
        if (!clazz.contains(".")) {
            clazz = Character.toUpperCase(clazz.charAt(0)) + clazz.substring(1);
        } else if (clazz.contains("<")) {
            clazz = clazz.substring(0, clazz.indexOf("<")) +
                    clazz.substring(clazz.lastIndexOf("."), clazz.length() - 1);
        }
        ParamAdaptor adaptor = mParamAdaptorMap.get(clazz);
        if (adaptor == null) {
            String c = "me.linjwhandyhttpd.compiler.adaptor.param." + clazz + ".Adaptor";
            try {
                adaptor = (ParamAdaptor) Class.forName(c).newInstance();
                mParamAdaptorMap.put(clazz, adaptor);
            } catch (Exception e) {
                throw new RuntimeException("do not support for " + clazz + " param");
            }
        }
        return adaptor;
    }

    private ResponseAdaptor getResponseAdaptor(TypeMirror response) {
        String clazz = response.toString();
        if (!clazz.contains(".")) {
            clazz = Character.toUpperCase(clazz.charAt(0)) + clazz.substring(1);
        }
        ResponseAdaptor adaptor = mResponseAdaptorMap.get(clazz);
        if (adaptor == null) {
            String c = "me.linjwhandyhttpd.compiler.adaptor.response." + clazz + ".Adaptor";
            try {
                adaptor = (ResponseAdaptor) Class.forName(c).newInstance();
                mResponseAdaptorMap.put(clazz, adaptor);
            } catch (Exception e) {
                throw new RuntimeException("do not support for " + clazz + " response");
            }
        }
        return adaptor;
    }

    private void generateCode(String className, String code) {
        try {
            JavaFileObject file = mFiler.createSourceFile(className);
            Writer writer = file.openWriter();
            writer.write(code);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
