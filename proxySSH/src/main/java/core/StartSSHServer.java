package core;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.session.ServerSessionImpl;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.IOException;

public class StartSSHServer {
    public static void main(String[] args) {

        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setHost("192.168.5.171");
        sshd.setPort(2022);

//        sshd.setShellFactory(new ProcessShellFactory("/bin/sh", "/C", "..."));
        sshd.setShellFactory(new ProcessShellFactory("cmd.exe", "/C", "..."));

        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

        sshd.setPasswordAuthenticator(
                new PasswordAuthenticator() {
                    @Override
                    public boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException, AsyncAuthException {
                        
                        ServerSession tmp = session;

                        System.out.println("test");

                        /*
                            1. putty.exe /C {commnad} {C1234123root}
                            2. C1234123으로 DB조회 후 Client 처리
                            3. 아.......... shell로 만든 세션 넣어야하는데 어디에 넣는지가 관건이네
                        */  

                        return true;
                    }
                }

        );

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
