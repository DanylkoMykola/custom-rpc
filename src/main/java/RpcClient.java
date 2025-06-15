import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

public class RpcClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost",50051);
        System.out.println("Reading from server...");
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        // Write the request to the server to prevert blocking of creation of ObjectInputStream on server side.
        // As I understand input stream need some metadata from output stream to be created properly.
        output.flush();
        DataInputStream input = new DataInputStream(socket.getInputStream());

        HelloRequest request = new HelloRequest("Mykola");
        request.writeTo(output);
        output.flush();

        HelloResponse response = HelloResponse.readFrom(input);
        System.out.println("Received response: " + response.getMessage());

        socket.close();
    }
}
