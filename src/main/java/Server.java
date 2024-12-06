import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * Calculate the right partition work in a server and send it back to local machine
 */
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
                double topLeftTemperature_S = receivedObject[0][0].getTemperature();
                double bottomRightTemperature_T = receivedObject[receivedObject.length - 1][receivedObject[0].length - 1].getTemperature();
                MetalAlloy metalAlloy = new MetalAlloy(receivedObject, topLeftTemperature_S, bottomRightTemperature_T, false);
                metalAlloy.run();
                System.out.println("Received object: " + Arrays.deepToString(receivedObject));

                outputStream.writeObject(MetalDecomposition.finalMetalAlloy);

                outputStream.flush();  // Make sure data is sent
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
