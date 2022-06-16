package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 1. Client Setting Start
 * 2. Client Session Start
 * 3. Client Session Auth
 * 4. Success Session to Create ClientChannel
 * 5. ClientChannel open verify
 * 6. inputStream outputStream mappings
 */

public class StartSSHClient extends Thread {
    private static Logger log = LogManager.getLogger();

    @Override
    public void run() {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        ClientSession session;
        try {
            session = client.connect("root", "192.168.5.102", 22).verify(60, TimeUnit.SECONDS).getSession();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        session.addPasswordIdentity("1234");
        try {
            session.auth().verify(60, TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ClientChannel channel = null;
        try {
            channel = session.createChannel(Channel.CHANNEL_SHELL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

        //채널에 OutputStream 2개 매핑
        channel.setOut(responseStream); // 결과값
        channel.setErr(errorStream);

        try {
            channel.open().verify(60, TimeUnit.SECONDS);

            //shell 실행인가
            OutputStream pipedIn = channel.getInvertedIn();

            pipedIn.write("ls\n".getBytes());
            pipedIn.flush();

            //Channel Thread대기 설정
            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(3));

            String error = new String(errorStream.toByteArray());
            log.debug(new String(responseStream.toByteArray()));

            Scanner scan = new Scanner(System.in);

            while (!session.getSessionState().equals(ClientSession.ClientSessionEvent.CLOSED)) {
                String msg = scan.nextLine();

                pipedIn.write((msg + " \n").getBytes());
                pipedIn.flush();

                System.out.println("");
                log.debug(new String(responseStream.toByteArray()));
                System.out.println("");
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
