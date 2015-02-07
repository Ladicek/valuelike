package cz.ladicek.annotationProcessorsTalk.valueLike;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;

final class GeneratedField {
    final String fullyQualifiedType;
    final String name;

    GeneratedField(TypeMirror type, Name name) {
        this.fullyQualifiedType = type.toString();
        this.name = name.toString();
    }
}
