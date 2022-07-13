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


    public InteractiveShell shell;
    public InteractiveShell getShell() {return shell;}

    public ManageEntrySSHClient manageEntrySSHClient;
    public InteractiveShellFactory(ManageEntrySSHClient client) {
        this.manageEntrySSHClient = client;
    }

    @Override
    public Command createShell(ChannelSession channel) throws IOException {
        shell = new InteractiveShell(channel, manageEntrySSHClient);

        manageEntrySSHClient.setIntershell(shell);


        return new InvertedShellWrapper(shell);
    }
}
