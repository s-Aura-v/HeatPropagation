package serverBased;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

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

            for (int i = 0; i < alloy.ITERATIONS; i++) {
                // STEP 1: GET LEFT PARTITION;
                // NOTE: YOU NEED TO COPY THE ALLOY INSTEAD OF REFERENCING IT. YOU CAN'T OVERRIDE THE ORIGINAL ARRAY OR IT'LL JUST CALCULATE THE TEMP AS IT CHANGES RATHER THAN USING THE TEMPERATURE IT WAS BEFOREHAND.
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
                // STEP 5: RETRIEVE RIGHT EDGES FROM SERVER
                // TODO BUG - SERVER DATA IS NOT BEING RETRIEVED PROPERLY.
                MetalCell[] rightEdges = (MetalCell[]) inputStream.readObject();
                System.out.println("Retrieved edges from server: " + Arrays.toString(rightEdges));

                // STEP 6: ADD RIGHT EDGE TO SELF
                heatedLeftPartition = alloy.addEdgeToAlloy(heatedLeftPartition, rightEdges, false);
                // STEP 7: RECALCULATE EDGE TEMP
                MetalCell[][] alloyed= alloy.recalculateEdges(heatedLeftPartition, true);
                System.out.println("LEFT_ALLOYED\n" + Arrays.deepToString(alloyed)
                        .replace("],", "\n").replace(",", "\t| ")
                        .replaceAll("[\\[\\]]", " "));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
