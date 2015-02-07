package cz.ladicek.annotationProcessorsTalk.valueLike;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("cz.ladicek.annotationProcessorsTalk.valueLike.ValueLike")
public class Generator extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            return doProcess(annotations, roundEnv);
        } catch (Throwable e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, writer.toString());
            return false;
        }
    }

    private boolean doProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        MustacheFactory mustache = new DefaultMustacheFactory();
        Mustache classTemplate = mustache.compile("class.mustache");

        for (Element annotated : roundEnv.getElementsAnnotatedWith(ValueLike.class)) {
            try {
                GeneratedClass generatedClass = scanInterface(annotated);
                generateClass(generatedClass, classTemplate);
            } catch (CantGenerateException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.element);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString(), annotated);
            }
        }
        return false;
    }

    private GeneratedClass scanInterface(Element annotated) throws CantGenerateException {
        if (annotated.getKind() != ElementKind.INTERFACE) {
            throw new CantGenerateException("@ValueLike can only be applied to interfaces", annotated);
        }

        TypeElement interfaceElement = (TypeElement) annotated;

        if (!interfaceElement.getInterfaces().isEmpty()) {
            throw new CantGenerateException("@ValueLike interface can't extend other interfaces", interfaceElement);
        }
        if (!interfaceElement.getTypeParameters().isEmpty()) {
            throw new CantGenerateException("@ValueLike interface can't be generic", interfaceElement);
        }

        PackageElement interfacePackage = processingEnv.getElementUtils().getPackageOf(interfaceElement);
        Name interfaceName = interfaceElement.getSimpleName();
        List<GeneratedField> fields = new ArrayList<GeneratedField>();

        // this depends on element.getEnclosedElements returning things in source order,
        // which wasn't always the case on Eclipse; see https://bugs.eclipse.org/bugs/show_bug.cgi?id=300408
        for (ExecutableElement method : ElementFilter.methodsIn(interfaceElement.getEnclosedElements())) {
            if (!method.getParameters().isEmpty()) {
                throw new CantGenerateException("@ValueLike interface methods can't have parameters", method);
            }
            if (method.getReturnType().getKind() == TypeKind.VOID) {
                throw new CantGenerateException("@ValueLike interface methods can't return void", method);
            }
            if (!method.getTypeParameters().isEmpty()) {
                throw new CantGenerateException("@ValueLike interface methods can't be generic", method);
            }

            fields.add(new GeneratedField(method.getReturnType(), method.getSimpleName()));
        }

        return new GeneratedClass(interfacePackage, interfaceName, fields);
    }

    private void generateClass(GeneratedClass generatedClass, Mustache classTemplate) throws IOException {
        JavaFileObject file = processingEnv.getFiler().createSourceFile(generatedClass.fullyQualifiedName);
        Writer writer = file.openWriter();
        try {
            classTemplate.execute(writer, generatedClass);
        } finally {
            writer.close();
        }
    }
}
