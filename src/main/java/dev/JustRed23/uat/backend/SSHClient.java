package dev.JustRed23.uat.backend;

import com.jcraft.jsch.*;
import dev.JustRed23.uat.backend.ex.SSHConnectException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.InetAddress;

public class SSHClient {

    private final Logger LOGGER = LoggerFactory.getLogger(SSHClient.class);

    private final String username, password;
    private final InetAddress host;
    private final int port;
    private Session session;

    public SSHClient(InetAddress host, String username, String password) throws SSHConnectException {
        this(host, 22, username, password);
    }

    public SSHClient(InetAddress host, int port, String username, String password) throws SSHConnectException {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;

        try {
            this.session = connect();
            LOGGER.debug("Connection established");
        } catch (JSchException e) {
            if (this.session != null)
                this.session.disconnect();
            throw new SSHConnectException(e);
        }
    }

    private @NotNull Session connect() throws JSchException {
        LOGGER.debug("Connecting to " + host.getHostAddress() + ":" + port);
        Session session = new JSch().getSession(username, host.getHostAddress(), port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        return session;
    }

    public void disconnect() {
        LOGGER.debug("Disconnecting from " + host.getHostAddress() + ":" + port);
        if (session != null) {
            session.disconnect();
            session = null;
        }
    }

    public int sendCommand(String command) {
        if (session == null || !session.isConnected())
            return -1;

        LOGGER.debug("Sending command: " + command + " to " + host.getHostAddress() + ":" + port);

        ChannelExec exec = null;
        try {
            exec = (ChannelExec) session.openChannel("exec");
            exec.setInputStream(null);
            exec.setErrStream(System.err);
            exec.setCommand(command);

            InputStream in = exec.getInputStream();

            exec.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    LOGGER.debug("RESP: " + new String(tmp, 0, i));
                }
                if (exec.isClosed()) {
                    LOGGER.debug("channel closed with exit code: " + exec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {}
            }
            return exec.getExitStatus();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (exec != null)
                exec.disconnect();
        }
    }

    public InetAddress getHost() {
        return host;
    }

    public Session getSession() {
        return session;
    }
}
