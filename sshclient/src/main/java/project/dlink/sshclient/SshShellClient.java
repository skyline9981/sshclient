package project.dlink.sshclient;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class SshShellClient extends SshClient {
    private PrintWriter writer;
    protected final Commander commander = new Commander();
    
    public SshShellClient(String ip, String port, String username, String password) {
        super(ip, port , username, password);
    }

    public void connectToServer() throws Exception {
        try {
            if (session == null || !session.isConnected()) {
                session = jsch.getSession(username, ip, Integer.parseInt(port));
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect(TIMEOUT);
                channel = session.openChannel("shell");
            }

            if (!channel.isConnected()) {
                writer = new PrintWriter(channel.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.connect(TIMEOUT);
            }
        } catch (Exception e) {
            System.err.printf(ANSI_RED + ANSI_BOLD +
                    "\nFailed to connect to %s:%s\n" + ANSI_RESET, ip, port);
            throw e;
        }
    }

    protected class Commander {
        private ArrayList<String> cmds = new ArrayList<String>();
        private String mainCmd;
        private String reply;
        private static final String CMD_END_MARK = "# CMD_END #";
        private static final int RECV_BUF_SIZE = 1024;

        public Commander addCmd(String... cmd) {
            for (String c : cmd) {
                cmds.add(c + "\n");
            }
            return this;
        }

        public Commander addMainCmd(String cmd, String... ctrls) {
            cmds.add(cmd + "\n");
            for (String ctrl : ctrls) {
                cmds.add(ctrl);
            }
            cmds.add(CMD_END_MARK + "\n");
            mainCmd = cmd;
            return this;
        }

        public Commander sendCmd() throws Exception {
            try {
                connectToServer();
            } catch (Exception e) {
                throw e;
            }

            for (String cmd : cmds) {
                writer.print(cmd);
                writer.flush();
            }

            cmds.clear();
            return this;
        }

        /**
         * Extract the output for mainCmd from remote machine.
         * This function depends on mainCmd having been setted with addMainCmd.
         *
         * @return Pure output for the mainCmd.
         */
        public String recvCmd() {
            int nbytes;
            char[] buf = new char[RECV_BUF_SIZE];
            String reply = "";

            try {
                while ((nbytes = reader.read(buf, 0, RECV_BUF_SIZE)) > -1) {
                    reply += String.valueOf(buf, 0, nbytes);
                    if (reply.contains(CMD_END_MARK)) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            reply = reply.substring(reply.indexOf(mainCmd) + mainCmd.length());
            reply = reply.substring(reply.indexOf("\n") + 1);
            reply = reply.substring(0, reply.lastIndexOf(CMD_END_MARK));
            reply = reply.substring(0, reply.lastIndexOf("\n")+ 1);
            return reply;
        }
    }
}
