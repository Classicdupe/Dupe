package xyz.prorickey.classicdupe.metrics;

import java.time.Duration;

public class ServerMetrics {

    private final Long serverStartTime;

    public ServerMetrics() {
        serverStartTime = System.currentTimeMillis();
    }

    public Long getServerUptime() { return System.currentTimeMillis()-serverStartTime; }

    public String getServerUptimeFormatted() {
        Duration duration = Duration.ofMillis(System.currentTimeMillis()-serverStartTime);
        long seconds = duration.getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        return String.format("%02d:%02d:%02d", HH, MM, SS);
    }

}
