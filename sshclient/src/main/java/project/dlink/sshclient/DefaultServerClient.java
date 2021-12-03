package project.dlink.sshclient;

public class DefaultServerClient extends SshExecClient implements ServerClient {
    public DefaultServerClient(String ip, String port, String username, String password) {
        super(ip, port, username, password);
        model = "SERVER";
    }

    @Override
    public void execCommand(String cmd) {
        try {
            sendCmd(cmd);
            System.out.print(recvCmd());
        } catch (Exception e) {
            return;
        }
    }
}
