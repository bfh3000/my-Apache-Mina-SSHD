package core;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.ProcessShellFactory;

import java.io.IOException;

public class ProxyStart {
    public static void main(String[] args) {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setHost("192.168.5.171");
        sshd.setPort(2022);
        sshd.setShellFactory(new ProcessShellFactory("cmd.exe","/c","notepad"));
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

        sshd.setPasswordAuthenticator((username, password, session) -> true);

        try {
            sshd.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true){
            try {
                Thread.sleep(3600000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
