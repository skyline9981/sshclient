package project.dlink.sshclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.net.config.Config;
import org.onosproject.core.ApplicationId;

import java.util.ArrayList;
import java.util.HashMap;

public class SshClientConfig extends Config<ApplicationId> {

    public ArrayList<SshClient> clients;
    public HashMap<Integer, SwitchClient> switchClients;
    public HashMap<Integer, ServerClient> serverClients;
    public HashMap<Integer, ApClient> apClients;

    private int[] maxWidth = {0, 0, 0, 0};

    private static final String CLIENT_INFOS = "clientInfos";
    private static final String IP = "ip";
    private static final String PORT = "port";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String MODEL = "model";

    @Override
    public boolean isValid() {
        for (JsonNode jsonNode : node.get(CLIENT_INFOS)) {
            ObjectNode info = jsonNode.deepCopy();
            if (!isIpAddress(info, IP, FieldPresence.MANDATORY)) {
                return false;
            } else if (!isTpPort(info, PORT, FieldPresence.OPTIONAL)) {
                return false;
            } else if (!hasFields(info, USERNAME, PASSWORD, MODEL)) {
                return false;
            } else if (!isValidModel(info.get(MODEL).asText())) {
                return false;
            }
        }
        return true;
    }

    public void parseConfig() {
        clients = new ArrayList<SshClient>();
        switchClients = new HashMap<Integer, SwitchClient>();
        serverClients = new HashMap<Integer, ServerClient>();
        apClients = new HashMap<Integer, ApClient>();
        int index = 0;

        for (JsonNode jsonNode : node.get(CLIENT_INFOS)) {
            String ip = jsonNode.path(IP).asText();
            String port = jsonNode.path(PORT).asText("22");
            String username = jsonNode.path(USERNAME).asText();
            String password = jsonNode.path(PASSWORD).asText();
            String model = jsonNode.path(MODEL).asText();

            maxWidth[0] = (maxWidth[0] > ip.length()) ? maxWidth[0] : ip.length();
            maxWidth[1] = (maxWidth[1] > port.length()) ? maxWidth[1] : port.length();
            maxWidth[2] = (maxWidth[2] > username.length()) ? maxWidth[2] : username.length();
            maxWidth[3] = (maxWidth[3] > model.length()) ? maxWidth[3] : model.length();

            SshClient client = null;
            switch (SshClientManager.DeviceModel.valueOf(model)) {
                case DGS_3630:
                    client = new Dgs3630Client(ip, port, username, password);
                    switchClients.put(index++, (Dgs3630Client) client);
                    break;
                case DXS_5000:
                    client = new Dxs5000Client(ip, port, username, password);
                    switchClients.put(index++, (Dxs5000Client) client);
                    break;
                case SERVER:
                    client = new DefaultServerClient(ip, port, username, password);
                    serverClients.put(index++, (DefaultServerClient) client);
                    break;
                case DIR_835:
                    client = new Dir835Client(ip, port, username, password);
                    apClients.put(index++, (Dir835Client) client);
                    break;
            }
            clients.add(client);
        }
    }

    public int[] getMaxWidth() {
        return maxWidth;
    }

    private boolean isValidModel(String model) {
        try {
            SshClientManager.DeviceModel.valueOf(model);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
