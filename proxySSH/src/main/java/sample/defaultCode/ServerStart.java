package sample.defaultCode;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.IOException;

public class ServerStart {
    public static void main(String[] args) throws IOException, InterruptedException, IllegalArgumentException {
        SshServer sshd = SshServer.setUpDefaultServer();

        sshd.setHost("192.168.5.171");
        sshd.setPort(2022);

        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
//        sshd.setShellFactory(new InteractiveShellFactory()); //
        sshd.setShellFactory(new ProcessShellFactory("cmd.exe", "/K", "")); //
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

        while(true){
            Thread.sleep(5000);
        }

    }
}
