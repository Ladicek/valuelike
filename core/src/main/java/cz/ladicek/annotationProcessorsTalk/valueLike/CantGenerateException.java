package cz.ladicek.annotationProcessorsTalk.valueLike;

import javax.lang.model.element.Element;

final class CantGenerateException extends Exception {
    final Element element;

    CantGenerateException(String message, Element element) {
        super(message);
        this.element = element;
    }
}
