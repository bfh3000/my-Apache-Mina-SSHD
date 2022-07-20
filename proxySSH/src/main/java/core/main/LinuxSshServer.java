package core.main;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;

import java.io.IOException;

public class LinuxSshServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        SshServer sshd = SshServer.setUpDefaultServer();

        sshd.setHost("192.168.5.201");
        sshd.setPort(2022);

        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setShellFactory(new InteractiveProcessShellFactory());
//        sshd.setShellFactory(new ProcessShellFactory("/bin/sh", "-c", "ls"));
        sshd.setPasswordAuthenticator(
                new PasswordAuthenticator() {
                    @Override
                    public boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException, AsyncAuthException {
                        String tmp = "";
                        return true;
                    }
                }
        );

        sshd.start();

        while(!sshd.isClosed()){
            Thread.sleep(500000);
        }
    }
}
