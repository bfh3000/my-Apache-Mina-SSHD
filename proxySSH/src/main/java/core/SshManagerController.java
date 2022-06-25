package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.helpers.AbstractSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.*;

public class SshManagerController extends Thread {

    private static Logger log = LogManager.getLogger();

    public static void main(String[] args) throws InterruptedException, IOException {
        
        StartSSHClient client = new StartSSHClient("192.168.5.102", "root", "1234");
        client.start();

        OutputStream pipedIn = client.getChannel().getInvertedIn();
        ByteArrayOutputStream Stream = client.getResponseStream();
        while (!client.getSession().getSessionState().equals(ClientSession.ClientSessionEvent.CLOSED)){
            Thread.sleep(5000);
            pipedIn.write(("ip ad"+" \n").getBytes());
            pipedIn.flush();
            log.debug("debug : "+new String(Stream.toByteArray()));
        }

    }
}