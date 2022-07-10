package coreDirectTarget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;


public class ManageEntrySSHClient extends Thread {
    private static Logger log = LogManager.getLogger();

    /**
     * ConnectionInfo
     *
     * */
    public static String DEST_IP = "";
    public static String USER_NAME = "";
    public static String PASSWORD = "";
    ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

    //get
    public SshClient client;
    public SshClient getClient() {return client;}

    //get
    public ClientSession session;
    public ClientSession getSession() {return session;}

    //get
    public ClientChannel channel;
    public ClientChannel getChannel() {return channel;}

    //get
    ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
    public ByteArrayOutputStream getResponseStream() {return this.responseStream;}

    //get, set
    public InteractiveShell intershell;
    public void setIntershell(InteractiveShell intershell) {this.intershell = intershell;}
    public InteractiveShell getIntershell() {return intershell;}

    ManageEntrySSHClient(String destIP, String username, String password){
        this.DEST_IP = destIP;
        this.USER_NAME = username;
        this.PASSWORD = password;
    }

    public void create(){
        //Start Client
        client=SshClient.setUpDefaultClient();
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

    @Override
    public void run() {
        while (client.isOpen()){
            if(responseStream.size() > 0){
                System.out.println();
                System.out.println("Client Message");
                System.out.println();
                log.debug(new String(responseStream.toByteArray(), StandardCharsets.UTF_8));
                System.out.println();
                if(intershell != null){
                    try {
                        intershell.getInputStream().write(responseStream.toByteArray());
                        responseStream.flush();
                        responseStream.reset();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() {
        if (this.isAlive() == false && client.isClosed()) {
            this.client.stop();
            Thread.interrupted();
        }
    }
}


    