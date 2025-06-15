package reader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;

public abstract class GenericMessage {
    public Field[] getSortedDeclaredFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Arrays.sort(fields, Comparator.comparingInt(f -> {
            Annotation annotation = f.getAnnotation(BinaryField.class);
            if (annotation == null) {
                return Integer.MAX_VALUE; // If no BinaryField annotation, place at the end
            }
            return f.getAnnotation(BinaryField.class).order();
        }));
        return fields;
    }
}
