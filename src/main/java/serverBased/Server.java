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

                // MetalAlloy Setup
                MetalCell[][] originalMetalAlloy = (MetalCell[][]) inputStream.readObject();
                double topLeftTemperature_S = originalMetalAlloy[0][0].getTemperature();
                double bottomRightTemperature_T = originalMetalAlloy[originalMetalAlloy.length - 1][originalMetalAlloy[0].length - 1].getTemperature();
                MetalCell[][] finalMetalAlloy = MetalDecomposition.copyMetalAlloy(originalMetalAlloy);
                MetalAlloy metalAlloy = new MetalAlloy(originalMetalAlloy, finalMetalAlloy, topLeftTemperature_S, bottomRightTemperature_T, false);

                // Executing right partition
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Future<MetalCell[][]> metalAlloyFuture = executorService.submit(metalAlloy);
                MetalCell[][] calculatedMetalAlloy = metalAlloyFuture.get();
                System.out.println("Received: \n" + Arrays.deepToString(finalMetalAlloy)
                        .replace("],", "\n").replace(",", "\t| ")
                        .replaceAll("[\\[\\]]", " "));

//                outputStream.writeObject(finalMetalAlloy);

                executorService.shutdown();
                outputStream.flush();  // Make sure data is sent
                inputStream.close();
                outputStream.close();
                clientSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            //CALLABLE REQUIRES YOU TO THROW EXCEPTION
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
