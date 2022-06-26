package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.client.session.ClientSession;

import java.io.IOException;

public class SshManagerController extends Thread {

    private static Logger log = LogManager.getLogger();

    public static void main(String[] args) throws IOException, InterruptedException {


        /*StartSSHClient client = new StartSSHClient("192.168.5.102", "root", "1234");
        client.create();
        while(!client.getSession().getSessionState().equals(ClientSession.ClientSessionEvent.CLOSED)) {
            Thread.sleep(1000);
            client.getChannel().getInvertedIn().write(("date"+"\n").getBytes());
            client.getChannel().getInvertedIn().flush();
            System.out.println(new String(client.getResponseStream().toByteArray()));
        }*/
    }
}