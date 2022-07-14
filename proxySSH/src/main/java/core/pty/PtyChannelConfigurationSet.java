package core.pty;

import org.apache.sshd.common.channel.PtyChannelConfigurationHolder;
import org.apache.sshd.common.channel.PtyMode;
import org.apache.sshd.common.util.MapEntryUtils;

import java.util.Map;

public class PtyChannelConfigurationSet implements PtyChannelConfigurationHolder {

    int DEFAULT_COLUMNS_COUNT = 80;
    int DEFAULT_ROWS_COUNT = 24;
    int DEFAULT_WIDTH = 640;
    int DEFAULT_HEIGHT = 480;

    String XTERM_PTY_TYPE = "xterm";
    String DUMMY_PTY_TYPE = "dummy";
    String WINDOWS_PTY_TYPE = "windows";

    Map<PtyMode, Integer> DEFAULT_PTY_MODES = MapEntryUtils.EnumMapBuilder.<PtyMode, Integer> builder(PtyMode.class)
            .put(PtyMode.ISIG, 1)
            .put(PtyMode.ICANON, 1)
            .put(PtyMode.ECHO, 1)
            .put(PtyMode.ECHOE, 1)
            .put(PtyMode.ECHOK, 1)
            .put(PtyMode.ECHONL, 0)
            .put(PtyMode.NOFLSH, 1)
            .immutable();

    @Override
    public String getPtyType() {
        return XTERM_PTY_TYPE;
    }

    @Override
    public int getPtyColumns() {
        return 0;
    }

    @Override
    public int getPtyLines() {
        return 0;
    }

    @Override
    public int getPtyWidth() {
        return 0;
    }

    @Override
    public int getPtyHeight() {
        return 0;
    }

    @Override
    public Map<PtyMode, Integer> getPtyModes() {
        return DEFAULT_PTY_MODES;
    }
}
