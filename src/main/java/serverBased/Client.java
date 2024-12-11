package serverBased;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Client {
    MetalAlloy alloy;

    public Client(MetalAlloy alloy) {
        this.alloy = alloy;
    }

    void runClient() {
        try {
            // SETUP
            Socket socket = new Socket(MetalDecomposition.SERVER_HOST, MetalDecomposition.PORT);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Client Setup Complete");

            // SEND ALLOY
            outputStream.writeObject(alloy);

            for (int i = 0; i < alloy.ITERATIONS; i++) {
                // STEP 1: GET LEFT PARTITION
                MetalCell[][] leftPartition = alloy.copyMetalAlloy(alloy.getMetalAlloy());

                // STEP 2: CALCULATE LEFT PARTITION
                MetalCell[][] heatedLeftPartition = alloy.heatLeftPartition(leftPartition);
                System.out.println("Left\n" + Arrays.deepToString(heatedLeftPartition)
                        .replace("],", "\n").replace(",", "\t| ")
                        .replaceAll("[\\[\\]]", " "));

                // STEP 3: GET EDGES
                MetalCell[] edges = alloy.getEdges();

                // STEP 4: SEND LEFT EDGES TO SERVER
                System.out.println("Sending edges to server: " + Arrays.toString(edges));
                outputStream.writeObject(edges);
                outputStream.flush();

                MetalCell[] rightEdges = (MetalCell[]) inputStream.readObject();
                System.out.println("Retrieved Edges from Server: " + Arrays.toString(rightEdges));

                // STEP 6: ADD RIGHT EDGE TO SELF
                MetalCell[][] updatedAlloy = alloy.addEdgeToAlloy(heatedLeftPartition, rightEdges, false);

                // STEP 7: RECALCULATE EDGE TEMP
                MetalCell[][] alloyed = alloy.recalculateEdges(updatedAlloy, true);
                System.out.println("LEFT_ALLOYED\n" + Arrays.deepToString(alloyed)
                        .replace("],", "\n").replace(",", "\t| ")
                        .replaceAll("[\\[\\]]", " "));
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
