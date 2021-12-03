package project.dlink.sshclient;

public class Dir835Client extends SshExecClient implements ApClient {
    public Dir835Client(String ip, String port, String username, String password) {
        super(ip, port, username, password);
        model = "DIR_835";
    }

    @Override
    public void setSsid(String ifname, String ssid) {
        try {
            sendCmd("uci set wireless." + ifname + ".ssid=" + ssid);
            sendCmd("uci commit wireless");
            sendCmd("wifi");
            System.out.print(recvCmd());
        } catch (Exception e) {
            return;
        }
    }
}
