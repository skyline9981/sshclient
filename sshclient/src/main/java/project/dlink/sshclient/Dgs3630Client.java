package project.dlink.sshclient;

import java.util.Arrays;
import java.io.FileWriter;

public class Dgs3630Client extends SshShellClient implements SwitchClient {
    public Dgs3630Client(String ip, String port, String username, String password) {
        super(ip, port, username, password);
        model = "DGS_3630";
    }

    @Override
    public void getController() {
        try {
            String[] reply = commander.addMainCmd("show openflow configuration")
                    .sendCmd()
                    .recvCmd()
                    .split("[\r\n]+");

            System.out.printf(ANSI_GREEN + ANSI_BOLD + "\033[1m" + "\n%s -- %s\n" + ANSI_RESET, ip, model);
            String[] controllers = Arrays.copyOfRange(reply, 11, reply.length - 1);
            System.out.printf("%-17s%-7s%-6s%s\n", "IP", "Port", "Mode", "Role");
            for (String controller : controllers) {
                String[] infos = controller.split("[ \t]+");
                System.out.printf("%-17s%-7s%-6s%s\n", infos[0], infos[1], infos[2], infos[3]);
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void setController(String ip, String port) {
        try {
            port = port.isEmpty() ? port : " service-port " + port;
            String reply = commander.addCmd("configure terminal")
                    .addMainCmd("openflow controller " + ip + port)
                    .addCmd("exit")
                    .sendCmd()
                    .recvCmd();
            System.out.print(reply);
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void unsetController(String ip) {
        try {
            String reply = commander.addCmd("configure terminal")
                    .addMainCmd("no openflow controller " + ip)
                    .addCmd("exit")
                    .sendCmd()
                    .recvCmd();
            System.out.print(reply);
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void getFlows() {
        try {
            String reply = commander.addMainCmd("show openflow flows", "a")
                    .sendCmd()
                    .recvCmd();
            System.out.print(reply);
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void getGroups() {
        try {
            String reply = commander.addMainCmd("show openflow group-desc", "a")
                    .sendCmd()
                    .recvCmd();
            System.out.print(reply);
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void getLogs(FileWriter writer) {
        try {
            String reply = commander.addMainCmd("show logging", "q")
                    .sendCmd()
                    .recvCmd();
            String title = String.format(ANSI_GREEN + ANSI_BOLD + "\033[1m" + "\n%s -- %s\n" + ANSI_RESET, ip, model);
            if (writer == null) {
                System.out.print(title);
                System.out.print(reply);
            } else {
                writer.write(title, 0, title.length());
                writer.write(reply, 0, reply.length());
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void setVxlanSourceInterfaceLoopback(String loopbackId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setVxlanVlan(String vnid, String vid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setVxlanVtep(String vnid, String ip, String mac) {
        throw new UnsupportedOperationException();
    }
}
