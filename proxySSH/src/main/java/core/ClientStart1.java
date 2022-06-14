package core;

import org.apache.logging.log4j.core.Logger;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ClientStart1 {
    final static Logger logger = (Logger) LoggerFactory.getLogger(ClientStart1.class);

    public static void main(String[] args) throws IOException {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        ClientSession session;
        try {
            session = client.connect("root", "192.168.5.102", 22).verify(60, TimeUnit.SECONDS).getSession();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        session.addPasswordIdentity("1234");
        AuthFuture aaa = session.auth().verify(60, TimeUnit.SECONDS);

        ClientChannel channel = session.createChannel(Channel.CHANNEL_SHELL);
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        logger.info("Temperature set to {}. Old temperature was {}.");

        channel.setOut(responseStream);
        channel.setErr(errorStream);


        try {
            channel.open().verify(60, TimeUnit.SECONDS);
            OutputStream pipedIn = channel.getInvertedIn();
            pipedIn.write("ls\n".getBytes());
            pipedIn.flush();

            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(3));

            String error = new String(errorStream.toByteArray());
            System.out.println(new String(responseStream.toByteArray()));

            Scanner scan = new Scanner(System.in);

            while (!session.getSessionState().equals(ClientSession.ClientSessionEvent.CLOSED)) {
                String msg = scan.nextLine();

                pipedIn.write((msg + " \n").getBytes());
                pipedIn.flush();

                System.out.println(new String(responseStream.toByteArray()));
            }

            if (!error.isEmpty()) {
                throw new Exception(error);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            channel.close(false);
        }
    }
}
