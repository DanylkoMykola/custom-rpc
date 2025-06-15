package io.codec;

import java.util.HashMap;
import java.util.Map;

/**
 * TypeCodecRegistry is a registry for TypeCodec instances.
 * It allows registration and retrieval of codecs based on their associated class types.
 */
public class TypeCodecRegistry {
    private static final Map<Class<?>, TypeCodec<?>> codecs = new HashMap<>();
    private static final TypeCodecRegistry INSTANCE = new TypeCodecRegistry();

    static {
        TypeCodecRegistry.register(String.class, new StringCodec());
        TypeCodecRegistry.register(Integer.class, new IntegerCodec());
    }

    public static <T> void register(Class<T> type, TypeCodec<T> codec) {
        codecs.put(type, codec);
    }

    public static TypeCodecRegistry getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> TypeCodec<T> get(Class<T> type) {
        return (TypeCodec<T>) codecs.get(type);
    }
}
