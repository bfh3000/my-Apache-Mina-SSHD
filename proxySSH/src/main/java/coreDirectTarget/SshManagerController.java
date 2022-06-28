package coreDirectTarget;

import coreOneByOne.StartSSHServerRe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.common.session.helpers.AbstractSession;
import org.apache.sshd.common.util.buffer.ByteArrayBuffer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SshManagerController extends Thread {

    private static Logger log = LogManager.getLogger();

    public static void main(String[] args) throws IOException, InterruptedException {
        StartSSHServerRe s = new StartSSHServerRe();
        s.startListen();

        ByteArrayBuffer buffer = new ByteArrayBuffer(("date"+"\n").getBytes(), false);
        
        int count = 0;
        while(true){
            Thread.sleep(3000);
            List<AbstractSession> tmp = s.getSshd().getActiveSessions();
            System.out.println("3 seconds");

            count++;
            if(tmp.size()>0 && count >= 5){
                String commandG = "hi \n";
                s.getSshd().getShellFactory().getShell().getInputStream().write(commandG.getBytes(StandardCharsets.UTF_8), 0, commandG.length());
                s.getSshd().getShellFactory().getShell().getInputStream().flush();
            }
        }

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