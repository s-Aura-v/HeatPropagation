package serverBased;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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

            // STEP 1: GET LEFT PARTITION
            MetalCell[][] leftPartition = alloy.copyMetalAlloy(alloy.getMetalAlloy());
            // STEP 2: CALCULATE LEFT PARTITION
            MetalCell[][] heatedLeftPartition = alloy.heatLeftPartition(leftPartition);
            // STEP 3: GET EDGES
            double[] edges= alloy.getEdges();
            // STEP 4: SEND LEFT EDGES TO SERVER
            outputStream.writeObject(edges);
            // STEP 5: RETRIEVE RIGHT EDGES FROM SERVER
            double[] rightEdges = (double[]) inputStream.readObject();
            // STEP 6: ADD RIGHT EDGE TO SELF
            heatedLeftPartition = alloy.addEdgeToAlloy(heatedLeftPartition, rightEdges, false);
            // STEP 7: RECALCULATE EDGE TEMP
            alloy.recalculateEdges(heatedLeftPartition, true);
            System.out.println("completed?");


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
