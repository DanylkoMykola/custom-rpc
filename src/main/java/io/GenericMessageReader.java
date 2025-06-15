package io;

import io.codec.TypeCodec;
import io.codec.TypeCodecRegistry;

import java.io.DataInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * GenericMessageReader is responsible for reading objects from a DataInputStream
 * using reflection to handle methods annotated with BinaryField.
 * It sorts the fields based on the order specified in the BinaryField annotation.
 */
public class GenericMessageReader extends GenericMessage{

    private final TypeCodecRegistry codecRegistry;

    public GenericMessageReader(TypeCodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    public <T> T read(DataInputStream input, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            Field[] fields = getSortedDeclaredFields(clazz);

            for (Field field : fields) {
                String setterName = getFieldSetter(field);
                Method method = clazz.getMethod(setterName, field.getType());
                Class<?> type = method.getParameterTypes()[0];

                TypeCodec<Object> codec = (TypeCodec<Object>) codecRegistry.get(type);
                if (codec != null) {
                    Object value = codec.read(input);
                    method.invoke(instance, value);
                }
                else {
                    Object nastedObject = read (input, type);
                    method.invoke(instance, nastedObject);
                }
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Error reading object from input stream", e);
        }
    }
    private boolean isSetter(Method method) {
        return Modifier.isPublic(method.getModifiers())
                && method.getName().startsWith("set")
                && Arrays.stream(method.getDeclaredAnnotations())
                .anyMatch(annotation -> annotation.annotationType().equals(BinaryField.class));
    }

    private String getFieldSetter(Field field) {
        String fieldName = field.getName();
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

    }
}
