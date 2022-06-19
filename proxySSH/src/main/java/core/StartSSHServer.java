package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;

import java.io.IOException;

public class StartSSHServer extends Thread {

    private static Logger log = LogManager.getLogger();

    @Override
    public void run() {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setHost("192.168.5.171");
        sshd.setPort(2022);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setPasswordAuthenticator(
                new PasswordAuthenticator() {
                    @Override
                    public boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException, AsyncAuthException {

                        return true;
                    }
                }
        );

        sshd.setShellFactory(new InteractiveProcessShellFactory());

        try {
            sshd.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}