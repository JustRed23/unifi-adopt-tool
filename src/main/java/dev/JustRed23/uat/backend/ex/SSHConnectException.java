package dev.JustRed23.uat.backend.ex;

import com.jcraft.jsch.JSchException;
import org.jetbrains.annotations.NotNull;

public class SSHConnectException extends Exception {

    public SSHConnectException(@NotNull JSchException e) {
        super("Could not connect to remote host: " + e.getMessage());
    }
}
