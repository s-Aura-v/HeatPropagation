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
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

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
            

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

//    public MetalCell[][] callServer() {
//
//        MetalCell[][] serverFinalMetal = new MetalCell[0][0];
//        edges = new double[originalMetalAlloy.length];
//        try {
//            Socket socket = new Socket(MetalDecomposition.SERVER_HOST, 1998);
//
//            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
//
//            // DURING THE FIRST ITERATION, SEND THE ENTIRE OBJECT SO IT CAN CONSTRUCT THE METALALLOY
//            if (MetalDecomposition.SERVER_HIT == 0) {
//                outputStream.writeObject(editedMetalAlloy);
//            }
//
//            // AFTERWARDS, ONLY SEND THE EDGES
//            getEdges();
//            outputStream.writeObject(edges);
//
////            System.out.println("Object Written to Server");
//
////            serverFinalMetal = (MetalCell[][]) inputStream.readObject();
////            System.out.println("Waiting for Server Output");
//
//            outputStream.close();
//            inputStream.close();
//            socket.close();
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return serverFinalMetal;
//    }

}
