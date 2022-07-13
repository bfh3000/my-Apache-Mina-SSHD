package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.sshd.common.channel.PtyMode;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.channel.ChannelDataReceiver;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.channel.PuttyRequestHandler;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InvertedShell;
import org.apache.sshd.server.shell.TtyFilterInputStream;
import org.apache.sshd.server.shell.TtyFilterOutputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        Map<PtyMode, ?> modes = resolveShellTtyOptions(serverChannel.getEnvironment().getPtyModes());
        out = new TtyFilterInputStream(rawout, modes);
        err = new TtyFilterInputStream(rawerr, modes);
        in = new TtyFilterOutputStream(rawin, out, modes);

        channel.setDataReceiver(new ChannelDataReceiver() {
            @Override
            public int data(ChannelSession channel, byte[] buf, int start, int len) throws IOException {
                String stringified = new String(Arrays.copyOfRange(buf, start, start + len));
                String hex = HexFormat.ofDelimiter("").formatHex(stringified.getBytes(StandardCharsets.UTF_8));
                String msg = new String(Arrays.copyOfRange(buf, start, start + len));
                System.out.println();
                log.info("Terminal Input DATA: " + msg);
                System.out.println();

                manageEntrySSHClient.getChannel().getInvertedIn().write(stringified.getBytes(StandardCharsets.UTF_8));
                manageEntrySSHClient.getChannel().getInvertedIn().flush();

                return 0;
            }

            @Override
            public void close() throws IOException {
            }
        });

    }

    protected Map<String, String> resolveShellEnvironment(Map<String, String> env) {
        return env;
    }

    // for some reason these modes provide best results BOTH with Linux SSH client and PUTTY
    protected Map<PtyMode, Integer> resolveShellTtyOptions(Map<PtyMode, Integer> modes) {
        if (PuttyRequestHandler.isPuttyClient(getServerSession())) {
//            return PuttyShellHandler.resolveShellTtyOptions(modes);
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
