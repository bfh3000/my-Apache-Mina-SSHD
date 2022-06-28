package coreOneByOne;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;


public class StartSSHClientRe {
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
    public ClientChannel channel;
    ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
    ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    public SshClient getClient() {
        return client;
    }

    public ClientSession getSession() {
        return session;
    }

    public ClientChannel getChannel() {
        return channel;
    }

    public ByteArrayOutputStream getResponseStream() {
        return this.responseStream;
    }

    StartSSHClientRe(String destIP, String username, String password){
        this.DEST_IP = destIP;
        this.USER_NAME = username;
        this.PASSWORD = password;
    }

    public void create(){
        //Start Client
        client = SshClient.setUpDefaultClient();
        client.start();

        //Create Client Session
        try{
            session=client.connect(USER_NAME, DEST_IP,22).verify(60,TimeUnit.SECONDS).getSession();
            session.addPasswordIdentity(PASSWORD);
            session.auth().verify(60,TimeUnit.SECONDS);
        }catch(IOException e){
            throw new RuntimeException(e);
        }

        //Create Client Channel
        try{
            channel = session.createChannel(Channel.CHANNEL_SHELL);
        }catch(IOException e){
            throw new RuntimeException(e);
        }

        //set Channel I/O
        channel.setOut(responseStream);
        channel.setErr(errorStream);
        try {
            channel.open().verify(60,TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),TimeUnit.SECONDS.toMillis(3));
    };


//        while(!session.getSessionState().equals(ClientSession.ClientSessionEvent.CLOSED)) {
//            Thread.sleep(1000);
//            channel.getInvertedIn().write(("date"+"\n").getBytes());
//            channel.getInvertedIn().flush();
//            System.out.println(new String(responseStream.toByteArray()));
//        }
}


    