package core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;


public class StartSSHClient extends Thread {
    private static Logger log = LogManager.getLogger();

    /**
     * ConnectionInfo
     *
     * */
    public static String DEST_IP = "";
    public static String USER_NAME = "";
    public static String PASSWORD = "";

    public SshClient client;
    public ClientSession session;
    public AuthFuture auth;
    public ClientChannel channel;

    public byte[] buf = new byte[8192];
    public ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
    public ByteArrayOutputStream responseStream=new ByteArrayOutputStream();
    public ByteArrayOutputStream errorStream=new ByteArrayOutputStream();

    StartSSHClient(String destIP, String username, String password){
        this.DEST_IP = destIP;
        this.USER_NAME = username;
        this.PASSWORD = password;
    }

    @Override
    public void run() {
        OutputStream pipedIn = channel.getInvertedIn();

        log.debug(new String(responseStream.toByteArray()));

//        while(!session.getSessionState().equals(ClientSession.ClientSessionEvent.CLOSED)){
        while(true){
            try {
                pipedIn.write(("ip ad"+" \n").getBytes());
                pipedIn.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            log.debug("===============================================================");
            log.debug(new String(responseStream.toByteArray(), StandardCharsets.UTF_8));
            log.debug("===============================================================");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void create() throws IOException{
        client = SshClient.setUpDefaultClient();
        client.start();

        try{
            session=client.connect(USER_NAME, DEST_IP,22).verify(60,TimeUnit.SECONDS).getSession();

            session.addPasswordIdentity(PASSWORD);
            session.auth().verify(60,TimeUnit.SECONDS);
        }catch(IOException e){
            throw new RuntimeException(e);
        }

        try{
            channel=session.createChannel(Channel.CHANNEL_SHELL);
        }catch(IOException e){
            throw new RuntimeException(e);
        }

        channel.setIn(inputStream);
        channel.setOut(responseStream);
        channel.setErr(errorStream);
        channel.open().verify(60,TimeUnit.SECONDS);

        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),TimeUnit.SECONDS.toMillis(10));
    }
}


    