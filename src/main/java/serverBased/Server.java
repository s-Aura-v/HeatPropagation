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


            MetalCell[] edges = (MetalCell[]) ois.readObject();
            System.out.println(Arrays.toString(edges));

            MetalCell[] cells = new MetalCell[10];
            for (int i = cells.length - 1; i >= 0; i--) {
                cells[i] = new MetalCell(.5,.5,.5,.3,.1,.5);
                cells[i].setTemperature(100);
            }

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.putDouble(cells[0].getHC1_PERCENTAGE());
            buffer.putDouble(cells[0].getHC2_PERCENTAGE());
            buffer.putDouble(cells[0].getHC3_PERCENTAGE());
            buffer.putDouble(cells[0].getHC1_CONSTANT());
            buffer.putDouble(cells[0].getHC2_CONSTANT());
            buffer.putDouble(cells[0].getHC3_CONSTANT());
            buffer.putDouble(cells[6].getTemperature());

            socket.getOutputStream().write(buffer.array());


        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
