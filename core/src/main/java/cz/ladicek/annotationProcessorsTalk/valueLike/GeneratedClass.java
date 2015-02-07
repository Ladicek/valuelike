package cz.ladicek.annotationProcessorsTalk.valueLike;

import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import java.util.List;

final class GeneratedClass {
    final String packageName;
    final String interfaceName;
    final String className;
    final String fullyQualifiedName;
    final List<GeneratedField> fields;

    GeneratedClass(PackageElement interfacePackage, Name interfaceName, List<GeneratedField> fields) {
        this.packageName = interfacePackage.getQualifiedName().toString();
        this.interfaceName = interfaceName.toString();
        this.className = "ValueLike_" + interfaceName.toString();
        this.fullyQualifiedName = packageName + "." + className; // default package not supported
        this.fields = fields;
    }

    String constructorParameters() {
        StringBuilder result = new StringBuilder();
        for (GeneratedField field : fields) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(field.fullyQualifiedType).append(" ").append(field.name);
        }
        return result.toString();
    }
}
