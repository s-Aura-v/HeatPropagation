package serverBased;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Server {
    public static void main(String[] args) {
        System.out.println("Waiting for connection");
        try (ServerSocket serverSocket = new ServerSocket(MetalDecomposition.PORT)) {
            Socket socket = serverSocket.accept();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            MetalAlloy alloy = (MetalAlloy) ois.readObject();
            System.out.println(Arrays.deepToString(alloy.getMetalAlloy()));

            double[] metalCells = new double[alloy.getMetalAlloy().length];
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            for (int i = 0; i < MetalDecomposition.ITERATIONS; i++) {
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
                for (int j = 0; j < edges.length; j++) {
                    buffer.putDouble(edges[j].getHC1_PERCENTAGE());
                    buffer.putDouble(edges[j].getHC2_PERCENTAGE());
                    buffer.putDouble(edges[j].getHC3_PERCENTAGE());
                    buffer.putDouble(edges[j].getHC1_CONSTANT());
                    buffer.putDouble(edges[j].getHC2_CONSTANT());
                    buffer.putDouble(edges[j].getHC3_CONSTANT());
                    buffer.putDouble(edges[j].getTemperature());
                }
                socket.getOutputStream().write(buffer.array());
                buffer.clear();
                // STEP 5: RETRIEVE LEFT EDGES FROM SERVER
                MetalCell[] leftEdges = (MetalCell[]) ois.readObject();
                System.out.println("Retrieved Edges from Client: " + Arrays.toString(leftEdges));
            }


        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
