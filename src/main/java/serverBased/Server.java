package serverBased;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Calculate the right partition work in a server and send it back to local machine
 * The server should be running in the background before you MetalDecomposition as the results of the latter is dependent on that of the execution of former.
 */
public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(MetalDecomposition.PORT)) {
            System.out.println("Waiting for connection...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                // MetalAlloy Setup
                MetalCell[][] originalMetalAlloy = (MetalCell[][]) inputStream.readObject();
                double topLeftTemperature_S = originalMetalAlloy[0][0].getTemperature();
                double bottomRightTemperature_T = originalMetalAlloy[originalMetalAlloy.length - 1][originalMetalAlloy[0].length - 1].getTemperature();
                MetalAlloy alloy = new MetalAlloy(originalMetalAlloy, topLeftTemperature_S, bottomRightTemperature_T, MetalDecomposition.SHOULD_COMPUTE_LEFT);
                System.out.println("Metal Alloy Created");

                // Executing right partition
                MetalCell[][] rightPartition = alloy.callRightPartition();
                System.out.println("Server Calculated: \n" + Arrays.deepToString(rightPartition)
                        .replace("],", "\n").replace(",", "\t| ")
                        .replaceAll("[\\[\\]]", " "));

                outputStream.writeObject(rightPartition);
                outputStream.flush();
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
