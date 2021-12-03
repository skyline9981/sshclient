package project.dlink.sshclient;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class SshClient {
    public String ip;
    public String port;
    public String username;
    public String password;
    public String model;

    protected Session session;
    protected Channel channel;
    protected BufferedReader reader;

    protected final JSch jsch = new JSch();
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    protected static final int TIMEOUT = 3000;
    protected static final int RECV_BUF_SIZE = 1024;
    protected static final String ANSI_RESET = "\u001B[0m";
    protected static final String ANSI_GREEN = "\u001B[32m";
    protected static final String ANSI_RED = "\u001B[31m";
    protected static final String ANSI_BOLD = "\033[1m";

    public SshClient(String ip, String port, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
