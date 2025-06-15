package io.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Codec for Integer type.
 * This codec handles the serialization and deserialization of Integer objects
 * to and from a binary format using standard Java serialization.
 */
public class IntegerCodec implements TypeCodec<Integer> {
    @Override
    public Integer read(DataInputStream in) throws IOException {
        return in.readInt();
    }

    @Override
    public void write(DataOutputStream output, Integer value) throws IOException {
        output.writeInt(value);
    }
}
