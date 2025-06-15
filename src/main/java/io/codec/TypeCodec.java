package io.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface TypeCodec<T> {
    void write(DataOutputStream out, T obj) throws IOException;
    T read(DataInputStream in) throws IOException;
}
