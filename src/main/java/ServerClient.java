import java.io.*;
import java.net.*;
import java.util.Arrays;

public class ServerClient {
    void sendToServer() {
        try {
            System.out.println("Sent");
            Socket socket = new Socket("localhost", 1998);

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream.writeObject(MetalDecomposition.finalMetalAlloy);

            MetalCell[][] serverFinalMetal = (MetalCell[][]) inputStream.readObject();
            System.out.println(Arrays.deepToString(serverFinalMetal));

            outputStream.close();
            inputStream.close();
            socket.close();

        } catch (IOException e) {
            System.out.println("Server could not be found. Is the server running?");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}