package coreDirectTarget;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.common.channel.PtyMode;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.MapEntryUtils;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.channel.PuttyRequestHandler;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InvertedShell;
import org.apache.sshd.server.shell.TtyFilterInputStream;
import org.apache.sshd.server.shell.TtyFilterOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InteractiveShell implements InvertedShell {
    private static Logger log = LogManager.getLogger();

//    private String command = "";
    private final List<String> command = new ArrayList<>();
    
    private String cmdValue;
    private ServerSession session;
    private ChannelSession serverChannel;

    private ManageEntrySSHClient manageEntrySSHClient;

    private TtyFilterOutputStream in;
    private TtyFilterInputStream out;
    private TtyFilterInputStream err;

    private byte[] outbuf = new byte[8192];
    private byte[] errbuf = new byte[8192];
    private ByteArrayInputStream rawout = new ByteArrayInputStream(outbuf);
    private ByteArrayOutputStream rawin = new ByteArrayOutputStream();
    private ByteArrayInputStream rawerr = new ByteArrayInputStream(errbuf);

    public InteractiveShell(ChannelSession serverChannel, ManageEntrySSHClient manageEntrySSHClient){
        this.serverChannel = serverChannel;
        this.manageEntrySSHClient = manageEntrySSHClient;
    }
    @Override
    public ChannelSession getServerChannelSession() {
        return serverChannel;
    }

    @Override
    public ServerSession getServerSession() {
        return session;
    }

    @Override
    public void setSession(ServerSession session) {
        this.session = Objects.requireNonNull(session, "No server session");
    }



    @Override
    public void start(ChannelSession channel, Environment env) throws IOException {
        this.serverChannel = channel;

        manageEntrySSHClient.getChannel().getInvertedIn();

        Map<String, String> varsMap = resolveShellEnvironment(env.getEnv());
        for (int i = 0; i < command.size(); i++) {
            String cmd = command.get(i);
            if ("$USER".equals(cmd)) {
                cmd = varsMap.get("USER");
                command.set(i, cmd);
                cmdValue = GenericUtils.join(command, ' ');
            }
        }
        
        ProcessBuilder builder = new ProcessBuilder(command);
        if (MapEntryUtils.size(varsMap) > 0) {
            try {
                Map<String, String> procEnv = builder.environment();
                procEnv.putAll(varsMap);
            } catch (Exception e) {

            }
        }

        if (log.isDebugEnabled()) {
            log.debug("start({}): command='{}', env={}",
                    channel, builder.command(), builder.environment());
        }

        Map<PtyMode, ?> modes = resolveShellTtyOptions(env.getPtyModes());
        out = new TtyFilterInputStream(rawout, modes);
        err = new TtyFilterInputStream(rawerr, modes);
        in = new TtyFilterOutputStream(rawin, out, modes);
    }

    protected Map<String, String> resolveShellEnvironment(Map<String, String> env) {
        return env;
    }

    // for some reason these modes provide best results BOTH with Linux SSH client and PUTTY
    protected Map<PtyMode, Integer> resolveShellTtyOptions(Map<PtyMode, Integer> modes) {
        if (PuttyRequestHandler.isPuttyClient(getServerSession())) {
            return PuttyRequestHandler.resolveShellTtyOptions(modes);
        } else {
            return modes;
        }
    }

    @Override
    public OutputStream getInputStream() {
        return in;
    }

    @Override
    public InputStream getOutputStream() {
        return out;
    }

    @Override
    public InputStream getErrorStream() {
        return err;
    }

    @Override
    public boolean isAlive() {
        return manageEntrySSHClient.isAlive();
    }

    @Override
    public int exitValue() {
        return 0;
    }

    @Override
    public void destroy(ChannelSession channel) {
        manageEntrySSHClient.close();
    }

    @Override
    public String toString() {
        return GenericUtils.isEmpty(cmdValue) ? super.toString() : cmdValue;
    }
}
