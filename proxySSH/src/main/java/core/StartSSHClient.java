package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;


public class StartSSHClient {
    private static Logger log = LogManager.getLogger();

    /**
     * ConnectionInfo
     *
     * */
    public static String DEST_IP = "";
    public static String USER_NAME = "";
    public static String PASSWORD = "";

    public SshClient client;
    public SshClient getClient() {return client;}

    public ClientSession session;
    public ClientSession getSession() {return this.session;}

    public AuthFuture auth;
    public ClientChannel channel;
    public ClientChannel getChannel() {return this.channel;}

    public byte[] buf = new byte[8192];
    public ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
    public ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
    public ByteArrayOutputStream getResponseStream(){return this.responseStream;}
    public ByteArrayOutputStream errorStream=new ByteArrayOutputStream();

    StartSSHClient(String destIP, String username, String password){
        this.DEST_IP = destIP;
        this.USER_NAME = username;
        this.PASSWORD = password;
    }

    public void start(){
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
            this.channel = session.createChannel(Channel.CHANNEL_SHELL);
        }catch(IOException e){
            throw new RuntimeException(e);
        }


        this.channel.setIn(inputStream);
        this.channel.setOut(responseStream);
        this.channel.setErr(errorStream);
        try {
            this.channel.open().verify(60,TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),TimeUnit.SECONDS.toMillis(3));


        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("1 seconds");
        }
    }

    /*public void create() throws IOException, InterruptedException {
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
            this.channel = session.createChannel(Channel.CHANNEL_SHELL);
        }catch(IOException e){
            throw new RuntimeException(e);
        }


        channel.setIn(inputStream);
        channel.setOut(responseStream);
        channel.setErr(errorStream);
        channel.open().verify(60,TimeUnit.SECONDS);

        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),TimeUnit.SECONDS.toMillis(3));


        while (true){
            Thread.sleep(1000);
            log.debug("1 seconds");
        }
    }*/
}


    