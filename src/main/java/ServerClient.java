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

            //TODO: Implement this later where I can get the data back from the server
//            MetalCell[][] serverFinalMetal = (MetalCell[][]) inputStream.readObject();
//            System.out.println(inputStream.readObject());

            outputStream.close();
            inputStream.close();
            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
        }
    }
}