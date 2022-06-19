import java.io.IOException;
import java.net.Socket;

public class GateClientSocket extends Thread{
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 2022);

    }
}
