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


            MetalCell[] cells = new MetalCell[10];
            for (int i = 0; i < 10; i++) {
                cells[i] = new MetalCell(.5, .5, .5, .3, .1, .5);
                cells[i].setTemperature(i);
            }
            out.writeObject(cells);

            int cellCount = 10;
            byte[] cellBytes = new byte[1024];

            // Read cell data
            int bytesRead = socket.getInputStream().read(cellBytes);

            // Create MetalCell objects from byte array
            MetalCell[] retrievedCells = new MetalCell[cellCount];
            for (int i = 0; i < cellCount; i++) {
                byte[] cellData = Arrays.copyOfRange(cellBytes, i * MetalCell.BUFFER_SIZE, (i + 1) * MetalCell.BUFFER_SIZE);
                ByteBuffer cellBuffer = ByteBuffer.wrap(cellData);

                retrievedCells[i] = new MetalCell(cellBuffer.getDouble(), cellBuffer.getDouble(), cellBuffer.getDouble(),
                        cellBuffer.getDouble(), cellBuffer.getDouble(), cellBuffer.getDouble());
                retrievedCells[i].setTemperature(cellBuffer.getDouble());
            }
            System.out.println(Arrays.toString(retrievedCells));


            while (true) {

                 /*
                 // STEP 1: GET LEFT PARTITION
                 MetalCell[][] leftPartition = alloy.copyMetalAlloy(alloy.getMetalAlloy());
                 // STEP 2: CALCULATE LEFT PARTITION
                 MetalCell[][] heatedLeftPartition = alloy.heatLeftPartition(leftPartition);
                 System.out.println("Left\n" + Arrays.deepToString(heatedLeftPartition)
                         .replace("],", "\n").replace(",", "\t| ")
                         .replaceAll("[\\[\\]]", " "));
                 // STEP 3: GET EDGES
                 MetalCell[] edges = alloy.getEdges();
                  */

            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
