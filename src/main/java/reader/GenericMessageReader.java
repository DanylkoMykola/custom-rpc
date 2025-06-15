package reader;

import java.io.DataInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

public class GenericMessageReader {
    public <T> T read(DataInputStream input, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            Method[] methods = clazz.getDeclaredMethods();
            Arrays.sort(methods, Comparator.comparingInt(m -> {
                Annotation annotation = m.getAnnotation(BinaryField.class);
                if (annotation == null) {
                    return Integer.MAX_VALUE; // If no BinaryField annotation, place at the end
                }
                return m.getAnnotation(BinaryField.class).order();
            }));

            for (Method method : methods) {
                if (!isSetter(method)) continue;
                Class<?> type = method.getParameterTypes()[0];

                if (type == String.class) {
                    int length = input.readInt();
                    byte[] bytes = new byte[length];
                    input.readFully(bytes);
                    method.invoke(instance, new String(bytes, StandardCharsets.UTF_8));
                }
                else if (type == Integer.class) {
                    int value = input.readInt();
                    method.invoke(instance, value);
                }
                else if (type == Long.class) {
                    long value = input.readLong();
                    method.invoke(instance, value);
                }
                else if (type == Double.class) {
                    double value = input.readDouble();
                    method.invoke(instance, value);
                }
                else if (type == Boolean.class) {
                    boolean value = input.readBoolean();
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
}
