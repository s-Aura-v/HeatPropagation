import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1998)) {
            System.out.println("Waiting for connection...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                // Read the object from the client
                MetalCell[][] receivedObject = (MetalCell[][]) inputStream.readObject();
                System.out.println("Received object: " + receivedObject);

                doCalculations();
//                outputStream.writeObject(response);

                outputStream.flush();  // Make sure data is sent
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void doCalculations() {
        System.out.println("add right partition calculation later");
    }
}
