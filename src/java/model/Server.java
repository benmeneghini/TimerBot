package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Boss;

import net.dv8tion.jda.api.entities.MessageChannel;

public class Server {

    private String name;
    private String id;
    private List<Server> syncedServers = new ArrayList<Server>();
    private MessageChannel channel;

    private List<Boss> bosses = new ArrayList();

    private String wardenRole = null; // These roles will be used to ping players across different servers.
    private String metRole = null;
    private String frozenRole = null;
    private String dlRole = null;
    private String edlRole = null;
    private String ringsRole = null;
    private String midsRole = null;
    private String egsRole = null;

    public Server(String name, String id) {
        this.name = name;
        this.id = id;
        initializeBosses();
    }

    /**
     * Initializes the bosses of this server by reading from a JSON file.
     */
    private void initializeBosses() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Read the JSON file and map it to an array of Boss objects
            // Boss[] jsonBosses = mapper.readValue(new File("src/java/model/Bosses.json"),
            // Boss[].class);
            Boss[] jsonBosses = mapper.readValue(new File("/home/container/Bosses.json"), Boss[].class);

            for (Boss boss : jsonBosses) {
                // bosses.put(boss.getName(),
                // new ArrayList<>(List.of(boss.getIsTimed(), boss.getIsDue(),
                // boss.getCurrentTime())));
                bosses.add(boss);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Syncs the bosses of this server with the bosses of another server.
     * 
     * @param server to sync
     */
    private void syncServer(Server server) {
        server.bosses = this.bosses;

        syncedServers.add(server);
        server.syncedServers.add(this);
    }

    /**
     * Unsyncs the bosses of this server.
     * 
     * @param server to unsync from
     */
    public void unsyncServer() {
        initializeBosses();

        for (Server s : syncedServers) {
            syncedServers.remove(s);
            s.syncedServers.remove(this);
        }
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public void setChannel(MessageChannel channel) {
        this.channel = channel;
    }

    public List<Boss> getBosses() {
        return bosses;
    }

    public String getWardenRole() {
        return wardenRole;
    }

    public void setWardenRole(String wardenRole) {
        this.wardenRole = wardenRole;
    }

    public String getMetRole() {
        return metRole;
    }

    public void setMetRole(String metRole) {
        this.metRole = metRole;
    }

    public String getFrozenRole() {
        return frozenRole;
    }

    public void setFrozenRole(String frozenRole) {
        this.frozenRole = frozenRole;
    }

    public String getDlRole() {
        return dlRole;
    }

    public void setDlRole(String dlRole) {
        this.dlRole = dlRole;
    }

    public String getEdlRole() {
        return edlRole;
    }

    public void setEdlRole(String edlRole) {
        this.edlRole = edlRole;
    }

    public String getRingsRole() {
        return ringsRole;
    }

    public void setRingsRole(String ringsRole) {
        this.ringsRole = ringsRole;
    }

    public String getMidsRole() {
        return midsRole;
    }

    public void setMidsRole(String midsRole) {
        this.midsRole = midsRole;
    }

    public String getEgsRole() {
        return egsRole;
    }

    public void setEgsRole(String egsRole) {
        this.egsRole = egsRole;
    }

    /**
     * Pings the role associated with the boss that is due in 3 minutes.
     * 
     * @param boss to ping
     */
    public void ping(Boss boss) {
        String pingRole = null;

        switch (boss.getBossType()) {
            case WARDEN:
                if (wardenRole != null)
                    pingRole = wardenRole;
                break;
            case METEORIC:
                if (metRole != null)
                    pingRole = metRole;
                break;
            case FROZEN:
                if (frozenRole != null)
                    pingRole = frozenRole;
                break;
            case DL:
                if (dlRole != null)
                    pingRole = dlRole;
                break;
            case EDL:
                if (edlRole != null)
                    pingRole = edlRole;
                break;
            case RING:
                if (ringsRole != null)
                    pingRole = ringsRole;
                break;
            case MIDRAID:
                if (midsRole != null)
                    pingRole = midsRole;
                break;
            case ENDGAME:
                if (egsRole != null)
                    pingRole = egsRole;
                break;
        }

        if (pingRole != null) {
            channel.sendTyping().queue();
            channel.sendMessage(pingRole + " " + boss.getName() + " due in 3 minutes!").queue();
        }
    }

    public List<Server> getSyncedServers() {
        return syncedServers;
    }

    public String getServerId() {
        return id;
    }

    public String getServerName() {
        return name;
    }

}
