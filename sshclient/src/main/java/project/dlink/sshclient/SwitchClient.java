package project.dlink.sshclient;

import java.io.FileWriter;

public interface SwitchClient {
    void getController();
    void setController(String ip, String port);
    void unsetController(String ip);
    void getFlows();
    void getGroups();
    void getLogs(FileWriter writer);
    void setVxlanSourceInterfaceLoopback(String loopbackId);
    void setVxlanVlan(String vnid, String vid);
    void setVxlanVtep(String vnid, String ip, String mac);
}
