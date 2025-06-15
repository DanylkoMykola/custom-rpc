import reader.BinaryField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * Represents a response to a hello request.
 * The main idea is not to use Java serialization because it is not portable and can not be used outside Java ecosystem.
 */
public class HelloResponse{

    private String message;

    public HelloResponse(String message) {
        this.message = message;
    }

    public HelloResponse() {
        // Default constructor for deserialization
    }

    @BinaryField(order = 1)
    public String getMessage() {
        return message;
    }

    @BinaryField(order = 1)
    public void setMessage(String message) {
        this.message = message;
    }

    @Deprecated
    public void writeTo(DataOutputStream out) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        out.writeInt(length);
        out.write(bytes);
    }

    @Deprecated
    public static HelloResponse readFrom(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return new HelloResponse(new String(bytes, StandardCharsets.UTF_8));
    }
}
