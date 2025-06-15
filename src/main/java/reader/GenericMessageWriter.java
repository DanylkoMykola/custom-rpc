package reader;

import java.io.DataOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

/**
 * GenericMessageWriter is responsible for writing objects to a DataOutputStream
 * using reflection to handle methods annotated with BinaryField.
 * It sorts the methods based on the order specified in the BinaryField annotation.
 */
public class GenericMessageWriter extends GenericMessage {
    public <T> void write(DataOutputStream output, T obj) {
        try {
            Class<?> clazz = obj.getClass();

            Field[] fields = getSortedDeclaredFields(clazz);

            for (Field field : fields) {
                String getterName = getFieldGetter(field);
                Method method = clazz.getMethod(getterName);

                Object value = method.invoke(obj);
                Class<?> type = method.getReturnType();

                if (type == String.class) {
                    byte[] bytes = ((String) value).getBytes(StandardCharsets.UTF_8);
                    output.writeInt(bytes.length);
                    output.write(bytes);
                }
                else if (type == Integer.class) {
                    output.writeInt(((int) value));
                }
                else if (type == Long.class) {
                    output.writeLong(((long) value));
                }
                else if (type == Double.class) {
                    output.writeDouble(((double) value));
                }
                else if (type == Boolean.class) {
                    output.writeBoolean(((boolean) value));
                }
                else {
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

    private String getFieldGetter(Field field) {
        String fieldName = field.getName();
        return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

    }
}
