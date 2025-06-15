import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;


/**
 * Represents a request to say hello.
 * The main idea is not to use a Java serialization because it is not portable and can not be used outside Java ecosystem.
 */
public class HelloRequest {
    private String name;

    public HelloRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Writes the request to the output stream.
     *
     * @param out the DataOutputStream to write to
     * @throws IOException if an I/O error occurs
     */
    public void writeTo(DataOutputStream out) throws IOException {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        out.writeInt(nameBytes.length);
        out.write(nameBytes);
    }

    /**
     * Reads a HelloRequest from the input stream.
     *
     * @param in the DataInputStream to read from
     * @return a HelloRequest object
     * @throws IOException if an I/O error occurs
     */
    public static HelloRequest readFrom(DataInputStream in) throws IOException {
        int readLength = in.readInt();
        byte[] namesBytes = new byte[readLength];
        in.readFully(namesBytes);
        return new HelloRequest(new String(namesBytes, StandardCharsets.UTF_8));
    }
}
