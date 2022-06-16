package importProxy;

import importProxy2st.ClientDaemon2;
import importProxy2st.CustomShellFactory2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.io.*;
import org.apache.sshd.common.kex.KexProposalOption;
import org.apache.sshd.common.session.ConnectionService;
import org.apache.sshd.common.session.ReservedSessionMessagesHandler;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public class ServerDaemon2 {
    private static final Logger logger = LogManager.getLogger(ServerDaemon2.class);
    private static int step = 0;

    private static class Daemon extends Thread {
        private SshServer sshd;
        private ClientDaemon2 client;

        public Daemon() {

            this.sshd = SshServer.setUpDefaultServer();
            this.sshd.setPort(2022);
            this.sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

            this.sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
                @Override
                public boolean authenticate(String username, String password, ServerSession session)
                        throws PasswordChangeRequiredException, AsyncAuthException {


                    client = new ClientDaemon2();

                    try {
                        client.create();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }

                    CustomShellFactory2 shellFactory = new CustomShellFactory2();

                    shellFactory.setClient(client);

                    sshd.setShellFactory(shellFactory);
                    return true;
                }
            });
        }

        @Override
        public void run() {
            try {
                this.sshd.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isHealthy() {
            return !this.sshd.isClosed();
        }

        public void kill() {
            try {
                this.sshd.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Daemon sshd = new Daemon();
        sshd.start();

        while (sshd.isHealthy()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                sshd.kill();
                System.exit(1);
            }
        }
    }
}
