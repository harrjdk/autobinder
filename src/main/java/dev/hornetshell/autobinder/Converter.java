package dev.hornetshell.autobinder;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A converter from one type to another
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Conversion.class)
public @interface Converter {
    String value();
    String matchingProperty() default "";
}
