import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            HelloRequest request = null;
            try {
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                request = (HelloRequest) input.readObject();
                String responseMessage = "Hello, " + request.getName();
                HelloResponse response = new HelloResponse(responseMessage);

                output.writeObject(response);
                output.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
