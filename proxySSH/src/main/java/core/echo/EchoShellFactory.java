package core.echo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

/**
 * TODO Add javadoc
 *
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public class EchoShellFactory implements ShellFactory {
    private static Logger log = LogManager.getLogger();

    public static final EchoShellFactory INSTANCE = new EchoShellFactory();

    public SSHClientForEcho sshClientForEcho;
    public EchoShellFactory(SSHClientForEcho client) {
        this.sshClientForEcho = client;
    }

    public EchoShellFactory() {
        super();
    }

    @Override
    public Command createShell(ChannelSession channel) {
        EchoShell shell = new EchoShell(channel, sshClientForEcho);
        sshClientForEcho.setEchoShell(shell);
        return shell;
    }
}