import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;

public class RpcClient {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("localhost",50051);
        System.out.println("Reading from server...");
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

        HelloRequest request = new HelloRequest("Mykola");
        output.writeObject(request);
        output.flush();

        HelloResponse response = (HelloResponse) input.readObject();
        System.out.println("Received response: " + response.getMessage());

        socket.close();
    }
}
