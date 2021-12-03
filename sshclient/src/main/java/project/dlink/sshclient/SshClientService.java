/*
 * Copyright 2019-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package project.dlink.sshclient;

public interface SshClientService {
    int ALL_CLIENTS_OPERATION_INDEX = -1;

    void printDevices();
    void getController(int index);
    void setController(int index, String ip, String port);
    void unsetController(int index, String ip);
    void getFlows(int index);
    void getGroups(int index);
    void getLogs(int index, String filename);
    void execCommand(int index, String cmd);
    void setSsid(int index, String ifname, String ssid);

    void setVxlanSourceInterfaceLoopback(int index, String loopbackId);
    void setVxlanVlan(int index, String vnid, String vid);
    void setVxlanVtep(int index, String vnid, String ip, String mac);
}
