import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GateServerSocket extends Thread{

//    private static Logger log = LogManager.getLogger();

    public static void main(String[] args) throws IOException {

        //Create ServerSocket
        ServerSocket serverSocket = new ServerSocket(2022);
        Socket socket = serverSocket.accept();

        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        String str = (String) dataInputStream.readUTF();


    }
}
