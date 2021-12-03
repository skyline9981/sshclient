package project.dlink.sshclient;

import java.util.Arrays;
import java.util.stream.Stream;
import java.io.FileWriter;

public class Dxs5000Client extends SshShellClient implements SwitchClient {
    public Dxs5000Client(String ip, String port, String username, String password) {
        super(ip, port, username, password);
        model = "DXS_5000";
    }

    @Override
    public void getController() {
        try {
            String[] reply = commander.addCmd("enable")
                    .addMainCmd("show openflow configured controller")
                    .addCmd("exit")
                    .sendCmd()
                    .recvCmd()
                    .split("[\r\n]+");

            System.out.printf(ANSI_GREEN + ANSI_BOLD + "\n%s -- %s\n" + ANSI_RESET, ip, model);
            String[] controllers = Arrays.copyOfRange(reply, 3, reply.length);
            System.out.printf("%-17s%-7s%-6s%s\n", "IP", "Port", "Mode", "Role");
            for (String controller : controllers) {
                String[] infos = Stream.of(controller.split("[ \t]+"))
                        .filter(i -> !i.isEmpty()).toArray(String[]::new);
                System.out.printf("%-17s%-7s%-6s%s\n", infos[0], infos[1], infos[2], infos[3]);
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void setController(String ip, String port) {
        try {
            String reply = commander.addCmd("enable", "configure")
                    .addMainCmd("openflow controller " + ip + " " + port)
                    .addCmd("exit", "exit")
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
            String reply = commander.addCmd("enable", "configure")
                    .addMainCmd("no openflow controller " + ip)
                    .addCmd("exit", "exit")
                    .sendCmd()
                    .recvCmd();
            System.out.print(reply);
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void getFlows() {
        // The 4 spaces is intended to scroll down all output of this command,
        // because the interactive interface dosen't provide an "all" option.
        // I just assumed scroll 4 times is quite enough in most cases.
        try {
            String reply = commander.addCmd("enable")
                    .addMainCmd("show openflow installed flows", " ", " ", " ", " ")
                    .addCmd("exit")
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
            String reply = commander.addCmd("enable")
                    .addMainCmd("show openflow installed groups")
                    .addCmd("exit")
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
            String reply = commander.addCmd("enable")
                    .addMainCmd("show logging buffered", "q")
                    .addCmd("exit")
                    .sendCmd()
                    .recvCmd();

            String title = String.format(ANSI_GREEN + ANSI_BOLD + "\n%s -- %s\n" + ANSI_RESET, ip, model);
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
        try {
            String reply = commander.addCmd("enable", "configure")
                    .addCmd("vxlan enable")
                    .addMainCmd("vxlan source-interface loopback " + loopbackId)
                    .addCmd("exit", "exit")
                    .sendCmd()
                    .recvCmd();
            System.out.print(reply);
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void setVxlanVlan(String vnid, String vid) {
        try {
            String reply = commander.addCmd("enable", "configure")
                    .addCmd("vxlan enable")
                    .addMainCmd("vxlan " + vnid + " vlan " + vid)
                    .addCmd("exit", "exit")
                    .sendCmd()
                    .recvCmd();
            System.out.print(reply);
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void setVxlanVtep(String vnid, String ip, String mac) {
        try {
            String reply = commander.addCmd("enable", "configure")
                    .addCmd("vxlan enable")
                    .addMainCmd("vxlan " + vnid + " vtep " + ip + (mac.isEmpty() ? "" : " tenant-system " + mac))
                    .addCmd("exit", "exit")
                    .sendCmd()
                    .recvCmd();
            System.out.print(reply);
        } catch (Exception e) {
            return;
        }
    }
}
