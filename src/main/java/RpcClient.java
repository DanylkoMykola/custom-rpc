import reader.GenericMessageReader;
import reader.GenericMessageWriter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

public class RpcClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost",50051);
        System.out.println("Reading from server...");
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        // Write the request to the server to prevert blocking of creation of ObjectInputStream on server side.
        // As I understand input stream need some metadata from output stream to be created properly.
        output.flush();
        DataInputStream input = new DataInputStream(socket.getInputStream());

        HelloRequest request = new HelloRequest("Mykola");


        GenericMessageWriter writer = new GenericMessageWriter();
        writer.write(output, request);

        output.flush();

        GenericMessageReader reader = new GenericMessageReader();
        HelloResponse response = reader.read(input, HelloResponse.class);
        System.out.println("Received response: " + response.getMessage());

        socket.close();
    }
}
