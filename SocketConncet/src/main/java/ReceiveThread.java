import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ReceiveThread extends Thread {

    private final Socket socket;

    public ReceiveThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream tmpbuf = new DataInputStream(socket.getInputStream());
            String receiveString;
            while (true) {
                receiveString = tmpbuf.readUTF();
                System.out.println("OtehrPeople : " + receiveString);
            }
        } catch (SocketException e1) {
            System.out.println("OtehrPeople Exit");
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

}