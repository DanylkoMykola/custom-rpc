package reader;

import java.io.DataInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GenericMessageReader extends GenericMessage{
    public <T> T read(DataInputStream input, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            Field[] fields = getSortedDeclaredFields(clazz);

            for (Field field : fields) {
                String setterName = getFieldSetter(field);
                Method method = clazz.getMethod(setterName, field.getType());
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

    private String getFieldSetter(Field field) {
        String fieldName = field.getName();
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

    }
}
