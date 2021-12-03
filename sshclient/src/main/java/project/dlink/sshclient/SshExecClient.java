package project.dlink.sshclient;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class SshExecClient extends SshClient {
    public SshExecClient(String ip, String port, String username, String password) {
        super(ip, port , username, password);
    }

    public void connectToServer() throws Exception {
        try {
            if (session == null || !session.isConnected()) {
                session = jsch.getSession(username, ip, Integer.parseInt(port));
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect(TIMEOUT);
            }
        } catch (Exception e) {
            System.err.printf(ANSI_RED + ANSI_BOLD +
                    "\nFailed to connect to %s:%s\n" + ANSI_RESET, ip, port);
            throw e;
        }
    }

    public void sendCmd(String cmd) throws Exception {
        try {
            connectToServer();
            channel = session.openChannel("exec");
            reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            ((ChannelExec) channel).setCommand(cmd);
            channel.connect();
        } catch (Exception e) {
            log.info("Failed to open exec channel to %s:%s", ip, port);
            throw e;
        }
    }

    public String recvCmd() {
        int nbytes;
        char[] buf = new char[RECV_BUF_SIZE];
        String reply = "";

        try {
            while ((nbytes = reader.read(buf, 0, RECV_BUF_SIZE)) > -1) {
                reply += String.valueOf(buf, 0, nbytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reply;
    }
}
