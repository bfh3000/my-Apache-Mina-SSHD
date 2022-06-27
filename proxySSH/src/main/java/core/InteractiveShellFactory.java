package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.InvertedShellWrapper;
import org.apache.sshd.server.shell.ShellFactory;

import java.io.IOException;

public class InteractiveShellFactory implements ShellFactory {
    private static Logger log = LogManager.getLogger();

    @Override
    public Command createShell(ChannelSession channel) throws IOException {
        InteractiveShell shell = new InteractiveShell(channel);
        return new InvertedShellWrapper(shell);
    }
}
