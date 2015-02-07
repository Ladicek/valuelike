package cz.ladicek.annotationProcessorsTalk.valueLike.example;

import cz.ladicek.annotationProcessorsTalk.valueLike.ValueLike;

import java.util.List;

@ValueLike
public interface Person {
    String name();
    int age();
    List<String> languages();
}
