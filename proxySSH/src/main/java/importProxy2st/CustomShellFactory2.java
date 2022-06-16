package importProxy2st;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.common.Factory;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.InvertedShell;
import org.apache.sshd.server.shell.InvertedShellWrapper;
import org.apache.sshd.server.shell.ShellFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Factory} of {@link Command} that will create a new process and bridge the streams.
 *
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public class CustomShellFactory2 extends AbstractLoggingBean implements ShellFactory {
    private static final Logger logger = LogManager.getLogger(CustomShellFactory2.class);

    private String command;
    private List<String> elements;
    public InvertedShell _shell;
    public ClientDaemon2 _client;

    public CustomShellFactory2() {
    }

    public void setClient(ClientDaemon2 client) {
        this._client = client;
    }


    /**
     * @return The original unparsed raw command
     */

    public String getCommand() {
        return command;
    }

    /**
     * @return The parsed command elements
     */
    public List<String> getElements() {
        return elements;
    }

    public void setCommand(String command, String... elements) {
        setCommand(command, GenericUtils.isEmpty(elements) ? Collections.emptyList() : Arrays.asList(elements));
    }

    public void setCommand(String command, List<String> elements) {
        this.command = ValidateUtils.checkNotNullAndNotEmpty(command, "No command");
        this.elements = ValidateUtils.checkNotNullAndNotEmpty(elements, "No parsed elements");
    }

    @Override
    public Command createShell(ChannelSession channel) {
        InvertedShell shell = createInvertedShell(channel);
        this._shell = shell;
        return new InvertedShellWrapper(this._shell);
    }

    protected InvertedShell createInvertedShell(ChannelSession channel) {
        return new CustomShell2(this._client);
    }
}