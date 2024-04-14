package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageChannel;

public class Server {

    private String serverName;
    private String serverId;
    private List<Server> syncedServers = new ArrayList<Server>(); // List of servers that use this station.
    private MessageChannel channel;

    private List<Boss> bosses = new ArrayList<Boss>();
    private List<Boss> warden = new ArrayList<Boss>();
    private List<Boss> met = new ArrayList<Boss>();
    private List<Boss> frozen = new ArrayList<Boss>();
    private List<Boss> dl = new ArrayList<Boss>();
    private List<Boss> edl = new ArrayList<Boss>();
    private List<Boss> rings = new ArrayList<Boss>();
    private List<Boss> mids = new ArrayList<Boss>();
    private List<Boss> egs = new ArrayList<Boss>();

    private String wardenRole = null; // These roles will be used to ping players across different servers.
    private String metRole = null;
    private String frozenRole = null;
    private String dlRole = null;
    private String edlRole = null;
    private String ringsRole = null;
    private String midsRole = null;
    private String egsRole = null;

    public Server(String serverName, String serverId) {
        this.serverName = serverName;
        this.serverId = serverId;
        initializeBosses();
    }

    public void syncServer(Server server) { // Syncs a server with this current station, which syncs to other servers using this station.
        syncedServers.add(server);
        server.syncedServers.add(this);
        server.setBosses(this.bosses);
        server.setWarden(this.warden);
        server.setMet(this.met);
        server.setFrozen(this.frozen);
        server.setDl(this.dl);
        server.setEdl(this.edl);
        server.setRings(this.rings);
        server.setMids(this.mids);
        server.setEgs(this.egs);

    }

    public void unsyncServer(Server server) { // Unsyncs the server with this station. This basically creates a new set of bosses for the same server.
        syncedServers.remove(server);
        server.syncedServers.remove(this);
        server.setBosses(new ArrayList<Boss>());
        server.setWarden(new ArrayList<Boss>());
        server.setMet(new ArrayList<Boss>());
        server.setFrozen(new ArrayList<Boss>());
        server.setDl(new ArrayList<Boss>());
        server.setEdl(new ArrayList<Boss>());
        server.setMids(new ArrayList<Boss>());
        server.setEgs(new ArrayList<Boss>());
        server.setRings(new ArrayList<Boss>());
        server.initializeBosses();
    }

    private void initializeBosses() {
        //Warden:
        Boss falgren = new Boss("Falgren", 45, 5, Arrays.asList());
        bosses.add(falgren);
        warden.add(falgren);

        //Meteoric:
        Boss doomclaw = new Boss("Doomclaw", 7, 5, Arrays.asList());
        bosses.add(doomclaw);
        met.add(doomclaw);
        Boss bonehead = new Boss("Bonehead", 15, 5, Arrays.asList());
        bosses.add(bonehead);
        met.add(bonehead);
        Boss redbane = new Boss("Redbane", 20, 5, Arrays.asList());
        bosses.add(redbane);
        met.add(redbane);
        Boss goretusk = new Boss("Goretusk", 20, 5, Arrays.asList());
        bosses.add(goretusk);
        met.add(goretusk);
        Boss rockbelly = new Boss("Rockbelly", 15, 5, Arrays.asList());
        bosses.add(rockbelly);
        met.add(rockbelly);
        Boss coppinger = new Boss("Coppinger", 20, 5, Arrays.asList("copp"));
        bosses.add(coppinger);
        met.add(coppinger);

        //Frozen:
        Boss eye = new Boss("Eye", 30, 5, Arrays.asList());
        bosses.add(eye);
        frozen.add(eye);
        Boss swampie = new Boss("Swampie", 35, 5, Arrays.asList("swampy"));
        bosses.add(swampie);
        frozen.add(swampie);
        Boss woody = new Boss("Woody", 38, 5, Arrays.asList());
        bosses.add(woody);
        frozen.add(woody);
        Boss chained = new Boss("Chained", 43, 5, Arrays.asList("chain"));
        bosses.add(chained);
        frozen.add(chained);
        Boss grom = new Boss("Grom", 45, 5, Arrays.asList());
        bosses.add(grom);
        frozen.add(grom);
        Boss pyrus = new Boss("Pyrus", 58, 5, Arrays.asList("py"));
        bosses.add(pyrus);
        frozen.add(pyrus);

        //DL:
        Boss oneFiftyFive = new Boss("155", 60, 5, Arrays.asList("spider"));
        bosses.add(oneFiftyFive);
        dl.add(oneFiftyFive);
        Boss oneSixty = new Boss("160", 65, 5, Arrays.asList("priest"));
        bosses.add(oneSixty);
        dl.add(oneSixty);
        Boss oneSistyFive = new Boss("165", 70, 5, Arrays.asList("king"));
        bosses.add(oneSistyFive);
        dl.add(oneSistyFive);
        Boss oneSeventy = new Boss("170", 80, 5, Arrays.asList("bolg"));
        bosses.add(oneSeventy);
        dl.add(oneSeventy);
        Boss oneEighty = new Boss("180", 90, 5, Arrays.asList("snorri"));
        bosses.add(oneEighty);
        dl.add(oneEighty);

        //EDL:
        Boss oneEightyFive = new Boss("185", 75, 5, Arrays.asList());
        bosses.add(oneEightyFive);
        edl.add(oneEightyFive);
        Boss oneNinety = new Boss("190", 85, 5, Arrays.asList());
        bosses.add(oneNinety);
        edl.add(oneNinety);
        Boss oneNinetyFive = new Boss("195", 95, 5, Arrays.asList());
        bosses.add(oneNinetyFive);
        edl.add(oneNinetyFive);
        Boss twoHundred = new Boss("200", 105, 5, Arrays.asList());
        bosses.add(twoHundred);
        edl.add(twoHundred);
        Boss twoZeroFive = new Boss("205", 115, 5, Arrays.asList());
        bosses.add(twoZeroFive);
        edl.add(twoZeroFive);
        Boss twoTen = new Boss("210", 125, 5, Arrays.asList());
        bosses.add(twoTen);
        edl.add(twoTen);
        Boss twoFifteen = new Boss("215", 135, 5, Arrays.asList("unox"));
        bosses.add(twoFifteen);
        edl.add(twoFifteen);

        //Rings:
        Boss ringOne = new Boss("North Ring", 225, 5, Arrays.asList("north"));
        bosses.add(ringOne);
        rings.add(ringOne);
        Boss ringTwo = new Boss("East Ring", 225, 5, Arrays.asList("east"));
        bosses.add(ringTwo);
        rings.add(ringTwo);
        Boss ringThree = new Boss("Centre Ring", 225, 5, Arrays.asList("centre", "center"));
        bosses.add(ringThree);
        rings.add(ringThree);
        Boss ringFour = new Boss("South Ring", 225, 5, Arrays.asList("south"));
        bosses.add(ringFour);
        rings.add(ringFour);

        //Midraids:
        Boss aggy = new Boss("Aggy", 1440, 1440, Arrays.asList("aggorath"));
        bosses.add(aggy);
        mids.add(aggy);
        Boss hrung = new Boss("Hrung", 1440, 1440, Arrays.asList("hrungir"));
        bosses.add(hrung);
        mids.add(hrung);
        Boss mord = new Boss("Mord", 1440, 1440, Arrays.asList("mordy", "mordris"));
        bosses.add(mord);
        mids.add(mord);
        Boss necro = new Boss("Necro", 1440, 1440, Arrays.asList("necromancer"));
        bosses.add(necro);
        mids.add(necro);

        //EG raids:
        Boss prot = new Boss("Prot", 1080, 45, Arrays.asList("proteus"));
        bosses.add(prot);
        egs.add(prot);
        Boss gele = new Boss("Gele", 2160, 2160, Arrays.asList("gelebron"));
        bosses.add(gele);
        egs.add(gele);
        Boss bt = new Boss("Bt", 2160, 2160, Arrays.asList("bloodthorn"));
        bosses.add(bt);
        egs.add(bt);
        Boss dino = new Boss("Dino", 2160, 2160, Arrays.asList("dhio", "dhiothu"));
        bosses.add(dino);
        egs.add(dino);
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerId() {
        return serverId;
    }

    public List<Server> getSyncedServers() {
        return syncedServers;
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

    public void setBosses(List<Boss> bosses) {
        this.bosses = bosses;
    }

    public List<Boss> getWarden() {
        return warden;
    }

    public void setWarden(List<Boss> warden) {
        this.warden = warden;
    }

    public List<Boss> getMet() {
        return met;
    }

    public void setMet(List<Boss> met) {
        this.met = met;
    }

    public List<Boss> getFrozen() {
        return frozen;
    }

    public void setFrozen(List<Boss> frozen) {
        this.frozen = frozen;
    }

    public List<Boss> getDl() {
        return dl;
    }

    public void setDl(List<Boss> dl) {
        this.dl = dl;
    }

    public List<Boss> getEdl() {
        return edl;
    }

    public void setEdl(List<Boss> edl) {
        this.edl = edl;
    }

    public List<Boss> getRings() {
        return rings;
    }

    public void setRings(List<Boss> rings) {
        this.rings = rings;
    }

    public List<Boss> getMids() {
        return mids;
    }

    public void setMids(List<Boss> mids) {
        this.mids = mids;
    }

    public List<Boss> getEgs() {
        return egs;
    }

    public void setEgs(List<Boss> egs) {
        this.egs = egs;
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

    public void ping(Boss boss) {
        String pingRole = null;
        if (warden.contains(boss) && !(wardenRole == null)) {
            pingRole = wardenRole;
        } else if (met.contains(boss) && !(metRole == null)) {
            pingRole = metRole;
        } else if (frozen.contains(boss) && !(frozenRole == null)) {
            pingRole = frozenRole;
        } else if (dl.contains(boss) && !(dlRole == null)) {
            pingRole = dlRole;
        } else if (edl.contains(boss) && !(edlRole == null)) {
            pingRole = edlRole;
        } else if (rings.contains(boss) && !(ringsRole == null)) {
            pingRole = ringsRole;
        } else if (mids.contains(boss) && !(midsRole == null)) {
            pingRole = midsRole;
        } else if (egs.contains(boss) && !(egsRole == null)) {
            pingRole = egsRole;
        }
        if (!(pingRole == null)) {
            channel.sendTyping().queue();
            channel.sendMessage(pingRole + " " + boss.getName() + " due in 3 minutes!").queue();
        }
    }

}
