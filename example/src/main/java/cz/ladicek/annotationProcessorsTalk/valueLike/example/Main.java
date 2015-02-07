package cz.ladicek.annotationProcessorsTalk.valueLike.example;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Person me = new ValueLike_Person("Ladislav Thon", 32, Arrays.asList("czech", "english", "java", "dart", "kotlin"));
        System.out.println(me);
    }
}
