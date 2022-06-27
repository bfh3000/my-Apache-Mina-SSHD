package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.common.channel.PtyMode;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.MapEntryUtils;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.common.util.io.IoUtils;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.channel.PuttyRequestHandler;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InvertedShell;
import org.apache.sshd.server.shell.TtyFilterInputStream;
import org.apache.sshd.server.shell.TtyFilterOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class InteractiveShell implements InvertedShell {
    private static Logger log = LogManager.getLogger();

//    private String command = "";
    private final List<String> command = new ArrayList<>();
    
    private String cmdValue;
    private ServerSession session;
    private ChannelSession channelSession;
    private Process process;
    private TtyFilterOutputStream in;
    private TtyFilterInputStream out;
    private TtyFilterInputStream err;

    public InteractiveShell(ChannelSession channel){
        this.channelSession = channel;
        command.add("cmd.exe");
        command.add("/K");
        command.add("ipconfig");
    }
    @Override
    public ChannelSession getServerChannelSession() {
        return channelSession;
    }

    @Override
    public ServerSession getServerSession() {
        return session;
    }

    @Override
    public void setSession(ServerSession session) {
        this.session = Objects.requireNonNull(session, "No server session");
        ValidateUtils.checkTrue(process == null, "Session set after process started");
    }



    @Override
    public void start(ChannelSession channel, Environment env) throws IOException {
        this.channelSession = channel;

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

        process = builder.start();
        Map<PtyMode, ?> modes = resolveShellTtyOptions(env.getPtyModes());
        out = new TtyFilterInputStream(process.getInputStream(), modes);
        err = new TtyFilterInputStream(process.getErrorStream(), modes);
        in = new TtyFilterOutputStream(process.getOutputStream(), err, modes);

        /*String commandG = "hi";
        in.write(commandG.getBytes(), 0, commandG.length());
        in.flush();*/
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
        return process.isAlive();
    }

    @Override
    public int exitValue() {
        if (isAlive()) {
            try {
                return process.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            return process.exitValue();
        }
    }

    @Override
    public void destroy(ChannelSession channel) {
        // NOTE !!! DO NOT NULL-IFY THE PROCESS SINCE "exitValue" is called subsequently
        boolean debugEnabled = log.isDebugEnabled();
        if (process != null) {
            if (debugEnabled) {
                log.debug("destroy({}) Destroy process for '{}'", channel, cmdValue);
            }
            process.destroy();
        }

        IOException e = IoUtils.closeQuietly(getInputStream(), getOutputStream(), getErrorStream());
        if (e != null) {

        }
    }

    @Override
    public String toString() {
        return GenericUtils.isEmpty(cmdValue) ? super.toString() : cmdValue;
    }
}
