package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.common.session.helpers.AbstractSession;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.IOException;
import java.util.List;

public class StartSSHServer extends Thread {

    private static Logger log = LogManager.getLogger();
    private SshServer sshd;

    public String USERNAME = "";
    public String PASSWORD = "";
    public String TARGET_REMOTE_IP = "";

    public String getUSERNAME() {return USERNAME;}
    public String getPASSWORD() {return PASSWORD;}
    public String getTARGET_REMOTE_IP() {return TARGET_REMOTE_IP;}


    public void create(){
        sshd = SshServer.setUpDefaultServer();
        sshd.setHost("192.168.5.171");
        sshd.setPort(2022);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setShellFactory(new InteractiveProcessShellFactory());
        sshd.setPasswordAuthenticator(
                new PasswordAuthenticator() {
                    @Override
                    public boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException, AsyncAuthException {

                        // C12345678 9자리
                        if(username.substring(0, 9).equals("C12345678")){
                            TARGET_REMOTE_IP = "192.168.5.102";
                            USERNAME = "root";
                            PASSWORD = "1234";
                        }
                        else if(username.substring(0, 9).equals("C11111111")){
                            TARGET_REMOTE_IP = "192.168.0.201";
                            USERNAME = "root";
                            PASSWORD = "201sac201";
                        }

//                        session.getFactoryManager().addSessionListener();

//                        CustomShellFactory2 shellFactory = new CustomShellFactory2();
//                        shellFactory.setClient(client);
//                        sshd.setShellFactory(shellFactory);

                        return true;
                    }
                }
        );

    }

    @Override
    public void run() {
        try {
            this.sshd.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

//    public List<AbstractSession> getSessionList(){
//        return sshd.getActiveSessions();
//    }
}