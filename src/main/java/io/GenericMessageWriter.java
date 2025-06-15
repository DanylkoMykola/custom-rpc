package io;

import io.codec.TypeCodec;
import io.codec.TypeCodecRegistry;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.Arrays;
import java.util.List;

/**
 * GenericMessageWriter is responsible for writing objects to a DataOutputStream
 * using reflection to handle methods annotated with BinaryField.
 * It sorts the methods based on the order specified in the BinaryField annotation.
 */
public class GenericMessageWriter extends GenericMessage {

    private final TypeCodecRegistry codecRegistry;

    public GenericMessageWriter(TypeCodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    public <T> void writeStream(DataOutputStream output, List<T> messages) {
        for ( T message : messages) {
            StreamEnvelope<T> envelope = new StreamEnvelope<>(false, message);
            write(output, envelope);
        }
        // End of stream
        write(output, new StreamEnvelope<>(true, messages));
    }

    public <T> void write(DataOutputStream output, T obj) {
        try {
            Class<?> clazz = obj.getClass();

            Field[] fields = getSortedDeclaredFields(clazz);

            for (Field field : fields) {
                if (!isGetMethodExists(clazz, field)) continue;;
                String getterName = getFieldGetterName(field);
                Method method = clazz.getDeclaredMethod(getterName);

                Object value = method.invoke(obj);
                Class<?> type = method.getReturnType();

                TypeCodec<Object> codec = (TypeCodec<Object>) codecRegistry.get(type);

                if (codec != null) {
                    codec.write(output, value);
                } else {
                    // Fallback to recursively serializing this object
                    write(output, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error writing object to output stream", e);
        }
    }
    private boolean isGetter(Method method) {
        return Modifier.isPublic(method.getModifiers())
                && method.getName().startsWith("get")
                && Arrays.stream(method.getDeclaredAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().equals(BinaryField.class));
    }

    private String getFieldGetterName(Field field) {
        String fieldName = field.getName();
        return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

    }

    private boolean isGetMethodExists(Class<?> tClass, Field field) {
        String methodName = getFieldGetterName(field);
        Method[] methods = tClass.getDeclaredMethods();
        return Arrays.stream(methods)
                .anyMatch(method -> method.getName().equals(methodName));
    }
}
