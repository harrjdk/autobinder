package dev.hornetshell.autobinder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Conversion
 *
 * An annotation to specify the conversion of one type into another
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Conversion {
    Converter[] value();
}
