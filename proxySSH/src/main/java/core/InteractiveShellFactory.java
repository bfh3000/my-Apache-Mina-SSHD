package core;

import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.InvertedShell;
import org.apache.sshd.server.shell.InvertedShellWrapper;
import org.apache.sshd.server.shell.ProcessShell;
import org.apache.sshd.server.shell.ShellFactory;

import java.io.IOException;

public class InteractiveShellFactory extends AbstractLoggingBean implements ShellFactory {

    public void setSession(){

    }

    InteractiveShellFactory(){

    }

    @Override
    public Command createShell(ChannelSession channel) throws IOException {
        InvertedShell shell = createInvertedShell(channel);
        return new InvertedShellWrapper(shell);
    }

    private InvertedShell createInvertedShell(ChannelSession channel) {
        return new InteractiveShell();
    }
}
