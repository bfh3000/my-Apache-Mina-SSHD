package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.common.util.logging.AbstractLoggingBean;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InvertedShell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InteractiveShell extends AbstractLoggingBean implements InvertedShell {
    private static Logger log = LogManager.getLogger();
    InteractiveShell(){
        log.debug("start interactiveshell");
    }

    @Override
    public OutputStream getInputStream() {
        return null;
    }

    @Override
    public InputStream getOutputStream() {
        return null;
    }

    @Override
    public InputStream getErrorStream() {
        return null;
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public int exitValue() {
        return 0;
    }

    @Override
    public ChannelSession getServerChannelSession() {
        return null;
    }

    @Override
    public void start(ChannelSession channel, Environment env) throws IOException {

    }

    @Override
    public void destroy(ChannelSession channel) throws Exception {

    }

    @Override
    public void setSession(ServerSession session) {

    }

    @Override
    public ServerSession getServerSession() {
        return null;
    }
}
