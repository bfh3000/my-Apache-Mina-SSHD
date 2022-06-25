package testMain;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.session.ClientSession.ClientSessionEvent;
import org.apache.sshd.common.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class ClienttestSample extends Thread {
    final static Logger logger = LoggerFactory.getLogger(ClienttestSample.class);
    public static void main(String[] args) throws Exception {
        //Start SshClient
        SshClient client = SshClient.setUpDefaultClient();
        client.start();


        //Create Client Session
        ClientSession session = client.connect("root", "192.168.5.102", 22).verify(60, TimeUnit.SECONDS).getSession();
        session.addPasswordIdentity("1234");
        session.auth().verify(60, TimeUnit.SECONDS);

        //Create Client Channel
        ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL);
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        channel.setOut(responseStream);
        channel.setErr(errorStream);
        channel.open().verify(60, TimeUnit.SECONDS);
        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(3));

        try {
            OutputStream pipedIn = channel.getInvertedIn();



            String error = new String(errorStream.toByteArray());
            System.out.println(new String(responseStream.toByteArray()));

            while(!session.getSessionState().equals(ClientSessionEvent.CLOSED)) {
                pipedIn.write(("ip ad"+" \n").getBytes());
                pipedIn.flush();
                System.out.println(new String(responseStream.toByteArray()));
            }


            if (!error.isEmpty()) {
                throw new Exception(error);
            }
        } finally {
            channel.close(false);
        }

    }
}
