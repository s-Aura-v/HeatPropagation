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
        double[] edges;
        try (ServerSocket serverSocket = new ServerSocket(MetalDecomposition.PORT)) {
            System.out.println("Waiting for connection");
            Socket clientSocket = serverSocket.accept();

            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            // MetalAlloy Setup
            MetalCell[][] originalMetalAlloy = (MetalCell[][]) inputStream.readObject();
            double topLeftTemperature_S = originalMetalAlloy[0][0].getTemperature();
            double bottomRightTemperature_T = originalMetalAlloy[originalMetalAlloy.length - 1][originalMetalAlloy[0].length - 1].getTemperature();
            MetalAlloy alloy = new MetalAlloy(originalMetalAlloy, topLeftTemperature_S, bottomRightTemperature_T, MetalDecomposition.SHOULD_COMPUTE_LEFT, MetalDecomposition.ITERATIONS);
            boolean programRunning = true;











            ExecutorService executorService = Executors.newFixedThreadPool(1);
//              Executing right partition
            for (int i = 0; i < MetalDecomposition.ITERATIONS; i++) {
                // STEP 1: CALCULATE EDGES
                Future<MetalCell[][]> rightPartition = executorService.submit(alloy);
                // STEP 2.1: GET EDGES
                edges = alloy.getEdges();
                // STEP 2.2: SEND EDGES
                System.out.println(Arrays.toString(edges));

            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();
            clientSocket.close();

        } catch (IOException |
                 ClassNotFoundException e) {
            e.printStackTrace();
        } catch (
                Exception e) {
            throw new RuntimeException(e);
        }

    }

}
