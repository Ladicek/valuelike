package cz.ladicek.annotationProcessorsTalk.valueLike;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class GeneratorTest {
    @Test
    public void success() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forResource("Book.java"))
                .processedWith(new Generator())
                .compilesWithoutError();
    }

    @Test
    public void failure() {
        assert_().about(javaSource())
                .that(JavaFileObjects.forSourceString("Book",
                        "@cz.ladicek.annotationProcessorsTalk.valueLike.ValueLike public abstract class Book {}"))
                .processedWith(new Generator())
                .failsToCompile()
                .withErrorContaining("@ValueLike can only be applied to interfaces");
    }
}
