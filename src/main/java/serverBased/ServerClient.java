package serverBased;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ServerClient {
    MetalAlloy alloy;

    public ServerClient(MetalAlloy alloy) {
        this.alloy = alloy;
    }

    void clientRun() {
        try (Socket socket = new Socket(MetalDecomposition.SERVER_HOST, MetalDecomposition.PORT)) {
            System.out.println("Socket Created");

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(alloy);
            out.flush();
            System.out.println("MetalAlloy Sent");


            // BYTEBUFFER TEST
            // END OF TEST

            ByteBuffer cellBuffer = ByteBuffer.allocate(1024);
            for (int i = 0; i < MetalDecomposition.ITERATIONS; i++) {
                // STEP 1: GET LEFT PARTITION
                MetalCell[][] leftPartition = alloy.copyMetalAlloy(alloy.getMetalAlloy());
                // STEP 2: CALCULATE LEFT PARTITION
                MetalCell[][] heatedLeftPartition = alloy.heatLeftPartition(leftPartition);
                System.out.println("Left\n" + Arrays.deepToString(heatedLeftPartition)
                        .replace("],", "\n").replace(",", "\t| ")
                        .replaceAll("[\\[\\]]", " "));
                // STEP 3: GET EDGES
                MetalCell[] edges = alloy.getEdges();
                // STEP 4: SEND EDGES
                out.writeObject(edges);
            System.out.println("Sent to Server: " + Arrays.deepToString(edges));
                out.flush();
                // STEP 5: GET EDGE FROM SERVER
                MetalCell[] retrievedCells = new MetalCell[alloy.getMetalAlloy().length];
                for (int j = 0; j < retrievedCells.length; j++) {
                    retrievedCells[j] = new MetalCell(cellBuffer.getDouble(), cellBuffer.getDouble(), cellBuffer.getDouble(),
                            cellBuffer.getDouble(), cellBuffer.getDouble(), cellBuffer.getDouble());
                    retrievedCells[j].setTemperature(cellBuffer.getDouble());
                }
                cellBuffer.clear();
                System.out.println("Retrieved from Server: " + Arrays.toString(retrievedCells));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
