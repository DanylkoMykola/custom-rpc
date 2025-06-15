package io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BinaryField is an annotation used to mark fields in a class that should be serialized
 * in a binary format. The order attribute specifies the order in which the fields should
 * be serialized.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BinaryField {
    int order();
}
