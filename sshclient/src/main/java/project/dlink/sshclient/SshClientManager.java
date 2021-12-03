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

import com.google.common.collect.ImmutableSet;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.config.ConfigFactory;
import org.onosproject.net.config.NetworkConfigEvent;
import org.onosproject.net.config.NetworkConfigListener;
import org.onosproject.net.config.NetworkConfigRegistry;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.util.Dictionary;
import java.util.Properties;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

import static org.onlab.util.Tools.get;
import static org.onosproject.net.config.basics.SubjectFactories.APP_SUBJECT_FACTORY;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true,
           service = {SshClientService.class},
           property = {
               "someProperty=Some Default String Value",
           })
public class SshClientManager implements SshClientService  {

    private final SshClientPrinter printer = new SshClientPrinter();
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final SshClientConfigListener cfgListener = new SshClientConfigListener();
    private final Set<ConfigFactory> factories = ImmutableSet.of(
            new ConfigFactory<ApplicationId, SshClientConfig>(APP_SUBJECT_FACTORY,
                    SshClientConfig.class, "SshClientConfig") {
                @Override
                public SshClientConfig createConfig() {
                    return new SshClientConfig();
                }
            }
    );

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected NetworkConfigRegistry cfgService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    /** Some configurable property. */
    private String someProperty;
    private ApplicationId appId;
    private ArrayList<SshClient> clients = new ArrayList<SshClient>();
    private HashMap<Integer, SwitchClient> switchClients = new HashMap<Integer, SwitchClient>();
    private HashMap<Integer, ServerClient> serverClients = new HashMap<Integer, ServerClient>();
    private HashMap<Integer, ApClient> apClients = new HashMap<Integer, ApClient>();

    @Activate
    protected void activate() {
        appId = coreService.registerApplication("project.dlink.sshclient");
        cfgService.addListener(cfgListener);
        factories.forEach(cfgService::registerConfigFactory);
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        cfgService.removeListener(cfgListener);
        factories.forEach(cfgService::unregisterConfigFactory);
        log.info("Stopped");
    }

    @Modified
    public void modified(ComponentContext context) {
        Dictionary<?, ?> properties = context != null ? context.getProperties() : new Properties();
        if (context != null) {
            someProperty = get(properties, "someProperty");
        }
        log.info("Reconfigured");
    }

    @Override
    public void printDevices() {
        printer.printDevices();
    }

    @Override
    public void getController(int index) {
        if (index == ALL_CLIENTS_OPERATION_INDEX) {
            switchClients.values().forEach(c -> c.getController());
        } else if (isSwitchClient(index)) {
            switchClients.get(index).getController();
        } else {
            System.out.println("Remote machine should be switch");
        }
    }

    @Override
    public void setController(int index, String ip, String port) {
        if (index == ALL_CLIENTS_OPERATION_INDEX) {
            switchClients.values().forEach(c -> c.setController(ip, port));
        } else if (isSwitchClient(index)) {
            switchClients.get(index).setController(ip, port);
        } else {
            System.out.println("Remote machine should be switch");
        }
    }

    @Override
    public void unsetController(int index, String ip) {
        if (index == ALL_CLIENTS_OPERATION_INDEX) {
            switchClients.values().forEach(c -> c.unsetController(ip));
        } else if (isSwitchClient(index)) {
            switchClients.get(index).unsetController(ip);
        } else {
            System.out.println("Remote machine should be switch");
        }
    }

    @Override
    public void getFlows(int index) {
        if (index == ALL_CLIENTS_OPERATION_INDEX) {
            switchClients.values().forEach(c -> c.getFlows());
        } else if (isSwitchClient(index)) {
            switchClients.get(index).getFlows();
        } else {
            System.out.println("Remote machine should be switch");
        }
    }

    @Override
    public void getGroups(int index) {
        if (index == ALL_CLIENTS_OPERATION_INDEX) {
            switchClients.values().forEach(c -> c.getGroups());
        } else if (isSwitchClient(index)) {
            switchClients.get(index).getGroups();
        } else {
            System.out.println("Remote machine should be switch");
        }
    }

    @Override
    public void getLogs(int index, String filename) {
        try (FileWriter writer = filename != null ? new FileWriter(filename) : null) {
            if (index == ALL_CLIENTS_OPERATION_INDEX) {
                switchClients.values().forEach(c -> c.getLogs(writer));
            } else if (isSwitchClient(index)) {
                switchClients.get(index).getLogs(writer);
            } else {
                System.out.println("Remote machine should be switch");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execCommand(int index, String cmd) {
        if (index == ALL_CLIENTS_OPERATION_INDEX) {
            serverClients.values().forEach(c -> c.execCommand(cmd));
        } else if (isServerClient(index)) {
            serverClients.get(index).execCommand(cmd);
        } else {
            System.out.println("Remote machine should be server");
        }
    }

    @Override
    public void setSsid(int index, String ifname, String ssid) {
        if (index == ALL_CLIENTS_OPERATION_INDEX) {
            apClients.values().forEach(c -> c.setSsid(ifname, ssid));
        } else if (isApClient(index)) {
            apClients.get(index).setSsid(ifname, ssid);
        } else {
            System.out.println("Remote machine should be AP");
        }
    }

    @Override
    public void setVxlanSourceInterfaceLoopback(int index, String loopbackId) {
        if (index == ALL_CLIENTS_OPERATION_INDEX) {
            switchClients.values().stream().filter(c -> c instanceof Dxs5000Client).forEach(c -> c.setVxlanSourceInterfaceLoopback(loopbackId));
        } else if (isDxs5000Client(index)) {
            switchClients.get(index).setVxlanSourceInterfaceLoopback(loopbackId);
        } else {
            System.out.println("Remote machine should be DXS-5000");
        }
    }

    @Override
    public void setVxlanVlan(int index, String vnid, String vid) {
        if (index == ALL_CLIENTS_OPERATION_INDEX) {
            switchClients.values().stream().filter(c -> c instanceof Dxs5000Client).forEach(c -> c.setVxlanVlan(vnid, vid));
        } else if (isDxs5000Client(index)) {
            switchClients.get(index).setVxlanVlan(vnid, vid);
        } else {
            System.out.println("Remote machine should be DXS-5000");
        }
    }

    @Override
    public void setVxlanVtep(int index, String vnid, String ip, String mac) {
        if (index == ALL_CLIENTS_OPERATION_INDEX) {
            switchClients.values().stream().filter(c -> c instanceof Dxs5000Client).forEach(c -> c.setVxlanVtep(vnid, ip, mac));
        } else if (isDxs5000Client(index)) {
            switchClients.get(index).setVxlanVtep(vnid, ip, mac);
        } else {
            System.out.println("Remote machine should be DXS-5000");
        }
    }

    public enum DeviceModel {
        DGS_3630,
        DXS_5000,
        SERVER,
        DIR_835
    }

    private boolean isSwitchClient(int index) {
        return clients.get(index) instanceof SwitchClient;
    }

    private boolean isDxs5000Client(int index) {
        return clients.get(index) instanceof Dxs5000Client;
    }

    private boolean isServerClient(int index) {
        return clients.get(index) instanceof ServerClient;
    }

    private boolean isApClient(int index) {
        return clients.get(index) instanceof ApClient;
    }

    private class SshClientConfigListener implements NetworkConfigListener {
        @Override
        public void event(NetworkConfigEvent event) {
            if ((event.type() == NetworkConfigEvent.Type.CONFIG_ADDED ||
                    event.type() == NetworkConfigEvent.Type.CONFIG_UPDATED) &&
                    event.configClass().equals(SshClientConfig.class)) {
                SshClientConfig config = cfgService.getConfig(appId, SshClientConfig.class);
                if (config != null) {
                    config.parseConfig();
                    clients = config.clients;
                    switchClients = config.switchClients;
                    serverClients = config.serverClients;
                    apClients = config.apClients;
                    printer.updateMaxWidth(config);
                    log.info("Config file uploaded successfully");
                }
            }
        }
    }

    private class SshClientPrinter {
        private static final int INTERVAL = 2;
        private final String[] FIELDS = {"IP", "Port", "Username", "Model"};
        /* Hardcodedly initialize to length of name of each info. */
        private int[] width = Arrays.stream(FIELDS).mapToInt(String::length).toArray();

        public void updateMaxWidth(SshClientConfig config) {
            int maxWidth[] = config.getMaxWidth();
            for (int i = 0; i < maxWidth.length; ++i) {
                width[i] = (maxWidth[i] > width[i]) ? maxWidth[i] : width[i];
            }
        }

        public void printDevices() {
            String fmt = "%-" + String.valueOf(width[0] + INTERVAL) + "s" +
                    "%-" + String.valueOf(width[1] + INTERVAL) + "s" +
                    "%-" + String.valueOf(width[2] + INTERVAL) + "s" +
                    "%-" + String.valueOf(width[3] + INTERVAL) + "s" + "\n";
            System.out.printf("Index  " + fmt, "IP", "Port", "Username", "Model");
            System.out.println("-".repeat(7 + Arrays.stream(width).sum() + INTERVAL * 4));
            for (int i = 0; i < clients.size(); ++i) {
                SshClient client = clients.get(i);
                System.out.printf("%-7d" + fmt, i, client.ip, client.port, client.username, client.model);
            }
        }
    }
}
