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
    MetalAlloy alloy;

    public Server(MetalAlloy alloy) {
        this.alloy = alloy;
    }

    void runServer() throws IOException {
        System.out.println("Waiting for connection");
        try (ServerSocket serverSocket = new ServerSocket(MetalDecomposition.PORT)) {
            Socket clientSocket = serverSocket.accept();
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("Server Setup Complete");

            for (int i = 0; i < alloy.ITERATIONS; i++) {
                // STEP 1: GET RIGHT PARTITION
                MetalCell[][] rightPartition = alloy.copyMetalAlloy(alloy.getMetalAlloy());
                System.out.println("RIGHT INITIAL\n" + Arrays.deepToString(rightPartition)
                        .replace("],", "\n").replace(",", "\t| ")
                        .replaceAll("[\\[\\]]", " "));
                // STEP 2: CALCULATE RIGHT PARTITION
                MetalCell[][] heatedRightPartition = alloy.heatRightPartition(rightPartition);
                System.out.println("Right\n" + Arrays.deepToString(heatedRightPartition)
                        .replace("],", "\n").replace(",", "\t| ")
                        .replaceAll("[\\[\\]]", " "));
                // STEP 3: GET EDGES
                MetalCell[] edges = alloy.getEdges();
                // STEP 4: SEND RIGHT EDGES TO CLIENT
                System.out.println("Sending edges to client: " + Arrays.toString(edges));
                outputStream.writeObject(edges);
                outputStream.flush();
                // STEP 5: RETRIEVE LEFT EDGES FROM SERVER
                MetalCell[] leftEdges = (MetalCell[]) inputStream.readObject();
                System.out.println("Retrieved Edges from Client: " + Arrays.toString(leftEdges));
                // STEP 6: ADD LEFT EDGE TO SELF
                heatedRightPartition = alloy.addEdgeToAlloy(heatedRightPartition, leftEdges, true);
                // STEP 7: RECALCULATE EDGE TEMP
                MetalCell[][] alloyed = alloy.recalculateEdges(heatedRightPartition, false);
                System.out.println("ALLOYED\n" + Arrays.deepToString(alloyed)
                        .replace("],", "\n").replace(",", "\t| ")
                        .replaceAll("[\\[\\]]", " "));
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
