package coreDirectTarget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

import java.io.IOException;

public class ManageEntrySSHServer {

    private static Logger log = LogManager.getLogger();
    /*private SshServer sshd;
    public SshServer getSshd() {
        return sshd;
    }*/

    public CloneSshServer sshd;

    public CloneSshServer getSshd() {
        return sshd;
    }

    public String USERNAME = "";
    public String PASSWORD = "";
    public String TARGET_REMOTE_IP = "";

    public String getUSERNAME() {return USERNAME;}
    public String getPASSWORD() {return PASSWORD;}
    public String getTARGET_REMOTE_IP() {return TARGET_REMOTE_IP;}


    public void startListen() throws IOException {
        sshd = CloneSshServer.setUpDefaultServer();
        sshd.setShellFactory(new InteractiveShellFactory()); //내꺼

        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setHost("192.168.5.171");
        sshd.setPort(2022);

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

                        ManageEntrySSHClient client = new ManageEntrySSHClient(TARGET_REMOTE_IP, USERNAME, PASSWORD);
                        client.create();

                        sshd.setShellFactory(new InteractiveShellFactory());
                        return true;
                    }
                }
        );
        sshd.start();
    }
}