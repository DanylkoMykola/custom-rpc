import io.GenericMessageReader;
import io.GenericMessageWriter;
import io.codec.TypeCodecRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(50051);
        ;
        System.out.println("Server is running on port 50051...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            try (ExecutorService executorService = Executors.newCachedThreadPool()) {
                executorService.submit(new ClientHandler(clientSocket));

            }

        }
    }
    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private static final TypeCodecRegistry codecRegistry = TypeCodecRegistry.getInstance();

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            HelloRequest request = null;
            try {
                DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                GenericMessageReader reader = new GenericMessageReader(codecRegistry);
                request = reader.read(input, HelloRequest.class);

                String responseMessage = "Hello, " + request.getName();
                HelloResponse response = new HelloResponse(responseMessage);

                GenericMessageWriter writer = new GenericMessageWriter(codecRegistry);
                writer.write(output, response);
                output.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
