package serverBased;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Calculate the right partition in a server and send it back to local machine to be merged.
 * The server should be running in the background before you MetalDecomposition as the results of the latter is dependent on that of the execution of former.
 */
public class Server {
    public static void main(String[] args) throws IOException {
        ObjectInputStream inputStream = null;
        ObjectOutputStream outputStream = null;
        Socket clientSocket = null;
        double[] edges = new double[HeatVisualizer.height];
        try (ServerSocket serverSocket = new ServerSocket(MetalDecomposition.PORT)) {

            while (true) {
                System.out.println("Waiting for connection");
                clientSocket = serverSocket.accept();

                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                // MetalAlloy Setup
                MetalCell[][] originalMetalAlloy = (MetalCell[][]) inputStream.readObject();
                double topLeftTemperature_S = originalMetalAlloy[0][0].getTemperature();
                double bottomRightTemperature_T = originalMetalAlloy[originalMetalAlloy.length - 1][originalMetalAlloy[0].length - 1].getTemperature();
                MetalAlloy alloy = new MetalAlloy(originalMetalAlloy, topLeftTemperature_S, bottomRightTemperature_T, MetalDecomposition.SHOULD_COMPUTE_LEFT);

//                ExecutorService executorService = Executors.newFixedThreadPool(5);
//                Future<MetalCell[][]> futureMetalValue = executorService.submit(alloy::callRightPartition);

//                 Executing right partition
                MetalCell[][] rightPartition = alloy.callRightPartition();

                outputStream.writeObject(rightPartition);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
        clientSocket.close();
    }

    void fillRightEdges(MetalCell[][] metalAlloy) {


    }
}
