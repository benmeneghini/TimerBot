package control;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import service.RespawnTimer;
import service.WindowTimer;

import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.Boss;
import model.Server;

public class CommandController extends ListenerAdapter {

    private String yeekTag = "yeek#0000";
    private String yeekBotTag = "yeeksbot#0000";
    // private List<String> bossNames = new ArrayList<>();
    private List<String> bossNames = Arrays.asList("falgren", "falg",
            "doomclaw", "bonehead", "redbane", "goretusk", "rockbelly", "coppinger", "copp",
            "eye", "swampie", "swampy", "woody", "chained", "chain", "grom", "pyrus", "py",
            "155", "160", "165", "170", "180", "snorri",
            "185", "190", "195", "200", "205", "210", "215", "unox",
            "northring", "north", "eastring", "east", "centrering", "centre", "center", "southring", "south",
            "aggy", "aggorath", "hrung", "hrungir", "mord", "mordris", "mordy", "necro", "necromancer",
            "prot", "proteus", "base", "prime", "gele", "gelebron", "bt", "bloodthorn", "dino", "dhio", "dhiothu",
            "d2");

    private List<RespawnTimer> respawnTimers = new ArrayList<RespawnTimer>();
    private List<WindowTimer> windowTimers = new ArrayList<WindowTimer>();

    private List<Server> servers = new ArrayList<Server>();

    private final String BOSS_ERROR_MESSAGE = "**ERROR:** Boss not found or unrecognised command.\n" +
            "Please try again or type \"help\" for more info";

    private final String RESET_ERROR_MESSAGE = "**ERROR:** Format: reset [boss]";

    private final String SET_ERROR_MESSAGE = "**ERROR:** Format: set [boss] {days}d {hours}h {minutes}m";

    private final String WINDOW_ERROR_MESSAGE = "**ERROR:** Format: window [boss] {days}d {hours}h {minutes}m";

    private final String HELP_MESSAGE = "**Commands:**\n"
            + "*Soon* - Shows a list of all boss timers.\n"
            + "*[Boss]* - Times a specific boss.\n"
            + "*Set [boss] {day}d {hour}h {minute}m* - Sets a boss to a specific time.\n"
            + "*Window [boss] {day}d {hour}h {minute}m* - Sets a boss's window to a specific time.\n"
            + "*Reset [boss]* - Removes the boss's timer.\n"
            + "*Bosslist* - Shows a list of timeable bosses.\n"
            + "*Gametime* - Shows the current game time.\n"
            + "*Wipe* - Removes ALL timers.\n";

    private final String EG_TITLE = "EGS:\n";
    private final String MIDS_TITLE = "MIDS:\n";
    private final String RINGS_TITLE = "RINGS:\n";
    private final String EDL_TITLE = "EDL:\n";
    private final String DL_TITLE = "DL:\n";
    private final String FROZEN_TITLE = "FROZEN:\n";
    private final String MET_TITLE = "METEORIC:\n";
    private final String WARDEN_TITLE = "WARDEN:\n";

    /**
     * Main method for handling commands to the bot
     * 
     * @param event The event that triggered the command
     */
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            System.out.println(
                    "Message received from " + event.getAuthor().getName() + ": " + event.getMessage().getContentRaw());

            // Get the command and arguments
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            String command = args[0];
            String[] subCommands = Arrays.copyOfRange(args, 1, args.length);
            Boss boss = null;
            Server server = findServer(event);

            // Check if the message is from the bot
            if (event.getAuthor().isBot()) {
                System.out.println("Bot message, ignored.");
                return;
            }

            // Check if the message is in the set channel
            if (server != null && !event.getChannel().getId().equalsIgnoreCase(server.getChannel().getId())) {
                System.out.println("Message not in set channel, ignored.");
                return;
            }

            // Check if the command being run is a boss (main use case)
            if (server != null && bossNames.contains(command.toLowerCase()) && subCommands.length == 0) {
                System.out.println("Boss command: " + command);
                boss = findBoss(args[0], event);

                if (boss == null) {
                    event.getChannel().sendTyping().queue();
                    event.getChannel().sendMessage(BOSS_ERROR_MESSAGE).queue();
                } else {
                    time(boss, event);
                    event.getChannel().sendTyping().queue();
                    event.getChannel().sendMessage(boss.getName() + " timed.").queue();
                }
                return;
            }

            // Handle command being run (alternative user flows)
            if (server != null) {
                String[] timeParts;
                int timeToSet;
                switch (command.toLowerCase()) {
                    case "help":
                        if (subCommands.length == 0) {
                            help(event);
                        } else {
                            return;
                        }
                        return;
                    case "bosslist":
                        if (subCommands.length == 0) {
                            bossList(event);
                        } else {
                            return;
                        }
                        return;
                    case "soon":
                        if (subCommands.length == 0) {
                            soon(event);
                        } else {
                            return;
                        }
                        break;
                    case "set":
                        if (args.length <= 2 || args.length >= 6) {
                            event.getChannel().sendTyping().queue();
                            event.getChannel().sendMessage(SET_ERROR_MESSAGE).queue();
                            return;
                        }

                        boss = findBoss(args[1], event);

                        if (boss == null) {
                            event.getChannel().sendTyping().queue();
                            event.getChannel().sendMessage(SET_ERROR_MESSAGE).queue();
                            return;
                        }

                        timeParts = Arrays.copyOfRange(args, 2, args.length);
                        timeToSet = convertToMinutes(timeParts);
                        set(boss, timeToSet, event);
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage(boss.getName() + " set for " + boss.getFormattedTime()).queue();
                        return;
                    case "reset":
                        boss = findBoss(args[1], event);

                        if (boss == null) {
                            event.getChannel().sendTyping().queue();
                            event.getChannel().sendMessage(RESET_ERROR_MESSAGE).queue();
                        }

                        reset(boss, event);
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage("Removed " + boss.getName() + " timer.").queue();
                        return;
                    case "gametime":
                        LocalDateTime utcTime = LocalDateTime.now(ZoneOffset.UTC);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                        String gameTime = utcTime.format(formatter);
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage("Current game time: **" + gameTime + "**").queue();
                        return;
                    case "window":
                        if (args.length <= 2 || args.length >= 6) {
                            event.getChannel().sendTyping().queue();
                            event.getChannel().sendMessage(WINDOW_ERROR_MESSAGE).queue();
                            return;
                        }

                        boss = findBoss(args[1], event);

                        if (boss == null) {
                            event.getChannel().sendTyping().queue();
                            event.getChannel().sendMessage(WINDOW_ERROR_MESSAGE).queue();
                            return;
                        }

                        timeParts = Arrays.copyOfRange(args, 2, args.length);
                        timeToSet = convertToMinutes(timeParts);
                        window(boss, timeToSet, event);
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage(boss.getName() + "\'s window set for " + boss.getFormattedTime())
                                .queue();
                        return;
                    case "wipe":
                        if (args.length != 1) {
                            return;
                        }
                        wipe(server, event);
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage("Wiped all timers.").queue();
                        return;
                }
            }

            // Check if the message is from yeek
            if (event.getAuthor().getAsTag().equalsIgnoreCase(yeekTag)
                    || event.getAuthor().getAsTag().equalsIgnoreCase(yeekBotTag)) {
                switch (command.toLowerCase()) {
                    case "server":
                        switch (args[1]) {
                            case "new":
                                newServer(args[2], event);
                                break;
                            // case "unsync":
                            // unsyncServer(event);
                            // break;
                            // case "sync":
                            // syncServer(args[2], event);
                            // break;
                            // case "delete":
                            // deleteServer(args[2], event);
                            // break;
                        }
                        break;
                    case "showservers":
                        showServers(event);
                        break;
                    case "ping":
                        if (args[1].equalsIgnoreCase("add")) {
                            addPing(args[2], args[3], event);
                            event.getChannel().sendTyping().queue();
                            event.getChannel().sendMessage("Added ping role for " + args[2] + " bosses.").queue();
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            removePing(args[2], event);
                            event.getChannel().sendTyping().queue();
                            event.getChannel().sendMessage("Removed ping role for " + args[2] + " bosses.").queue();
                        }
                        break;
                    case "setup":
                        setup(args[1], event);
                        break;
                    case "setchannel":
                        setChannel(event);
                        break;
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("**ERROR:** Something unexpected happened. Please try again.").queue();
        }
    }

    /**
     * Method handling the command soon.
     * Prints the list of timed bosses.
     * 
     * @param event The event that triggered the command
     */
    private void soon(MessageReceivedEvent event) {
        boolean hasBosses = false;
        EmbedBuilder soon = new EmbedBuilder();
        Server server = findServer(event);
        String egs = "";
        String mids = "";
        String rings = "";
        String edl = "";
        String dl = "";
        String frozen = "";
        String met = "";
        String warden = "";

        for (Boss boss : server.getBosses()) {
            switch (boss.getBossType()) {
                case ENDGAME:
                    if (boss.getIsDue()) {
                        egs += "__" + boss.getName() + " is DUE!__ Window closes in " + boss.getFormattedTime()
                                + "\n";
                    } else if (boss.getIsTimed()) {
                        egs += boss.getName() + " in " + boss.getFormattedTime() + "\n";
                    }
                    break;
                case MIDRAID:
                    if (boss.getIsDue()) {
                        mids += "__" + boss.getName() + " is DUE!__ Window closes in " + boss.getFormattedTime()
                                + "\n";
                    } else if (boss.getIsTimed()) {
                        mids += boss.getName() + " in " + boss.getFormattedTime() + "\n";
                    }
                    break;
                case RING:
                    if (boss.getIsDue()) {
                        rings += "__" + boss.getName() + " is DUE!__\n";
                    } else if (boss.getIsTimed()) {
                        rings += boss.getName() + " in " + boss.getFormattedTime() + "\n";
                    }
                    break;
                case EDL:
                    if (boss.getIsDue()) {
                        edl += "__" + boss.getName() + " is DUE!__\n";
                    } else if (boss.getIsTimed()) {
                        edl += boss.getName() + " in " + boss.getFormattedTime() + "\n";
                    }
                    break;
                case DL:
                    if (boss.getIsDue()) {
                        dl += "__" + boss.getName() + " is DUE!__\n";
                    } else if (boss.getIsTimed()) {
                        dl += boss.getName() + " in " + boss.getFormattedTime() + "\n";
                    }
                    break;
                case FROZEN:
                    if (boss.getIsDue()) {
                        frozen += "__" + boss.getName() + " is DUE!__\n";
                    } else if (boss.getIsTimed()) {
                        frozen += boss.getName() + " in " + boss.getFormattedTime() + "\n";
                    }
                    break;
                case METEORIC:
                    if (boss.getIsDue()) {
                        met += "__" + boss.getName() + " is DUE!__\n";
                    } else if (boss.getIsTimed()) {
                        met += boss.getName() + " in " + boss.getFormattedTime() + "\n";
                    }
                    break;
                case WARDEN:
                    if (boss.getIsDue()) {
                        warden += "__" + boss.getName() + " is DUE!__\n";
                    } else if (boss.getIsTimed()) {
                        warden += boss.getName() + " in " + boss.getFormattedTime() + "\n";
                    }
                    break;
            }
        }

        if (!egs.isEmpty()) {
            soon.addField(EG_TITLE, egs, false);
            hasBosses = true;
            // timeMessage += EG_TITLE + egs;
        }

        if (!mids.isEmpty()) {
            soon.addField(MIDS_TITLE, mids, false);
            hasBosses = true;
            // timeMessage += MIDS_TITLE + mids;
        }

        if (!rings.isEmpty()) {
            soon.addField(RINGS_TITLE, rings, false);
            hasBosses = true;
            // timeMessage += RINGS_TITLE + rings;
        }

        if (!edl.isEmpty()) {
            soon.addField(EDL_TITLE, edl, false);
            hasBosses = true;
            // timeMessage += EDL_TITLE + edl;
        }

        if (!dl.isEmpty()) {
            soon.addField(DL_TITLE, dl, false);
            hasBosses = true;
            // timeMessage += DL_TITLE + dl;
        }

        if (!frozen.isEmpty()) {
            soon.addField(FROZEN_TITLE, frozen, false);
            hasBosses = true;
            // timeMessage += FROZEN_TITLE + frozen;
        }

        if (!met.isEmpty()) {
            soon.addField(MET_TITLE, met, false);
            hasBosses = true;
            // timeMessage += MET_TITLE + met;
        }

        if (!warden.isEmpty()) {
            soon.addField(WARDEN_TITLE, warden, false);
            hasBosses = true;
            // timeMessage += WARDEN_TITLE + warden;
        }

        if (hasBosses) {
            soon.setTitle("⏳ Boss Times ⏳");
            // soon.setDescription(timeMessage);
            // soon.addField("Creator", "Yeek", false);
            soon.setColor(0x630505);
            soon.setFooter("Pinged by " + event.getMember().getUser().getAsTag(),
                    event.getMember().getUser().getAvatarUrl());

            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessageEmbeds(soon.build()).queue();
        } else {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("No bosses currently timed! :(").queue();
        }
        soon.clear();

    }

    /**
     * Method handling the command that times a boss.
     * 
     * @param boss  The boss to time
     * @param event The event that triggered the command
     */
    public void time(Boss boss, MessageReceivedEvent event) {
        if (boss.getIsTimed() || boss.getIsDue()) {
            reset(boss, event);
        }
        new Thread(new RespawnTimer(boss, boss.getRespawn(), this,
                findServer(event))).start();
    }

    /**
     * Method for setting a boss time.
     * 
     * @param boss  The boss to set
     * @param time  The time to set
     * @param event The event that triggered the command
     */
    public void set(Boss boss, int minutes, MessageReceivedEvent event) {
        if (boss.getIsTimed() || boss.getIsDue()) {
            reset(boss, event);
        }

        if (minutes < 0) {
            int newMinutes = boss.getRespawn() + minutes;
            boss.setCurrentTime(newMinutes);
            new Thread(new RespawnTimer(boss, newMinutes, this, findServer(event))).start();
        } else {
            boss.setCurrentTime(minutes);
            new Thread(new RespawnTimer(boss, minutes, this, findServer(event))).start();
        }
    }

    /**
     * Method for setting a boss's window time.
     * 
     * @param boss  The boss to set
     * @param time  The time to set the window to
     * @param event The event that triggered the command
     */
    public void window(Boss boss, int minutes, MessageReceivedEvent event) {
        if (boss.getIsTimed() || boss.getIsDue()) {
            reset(boss, event);
        }

        if (minutes < 0) {
            int newMinutes = boss.getWindow() + minutes;
            boss.setCurrentTime(newMinutes);
            new Thread(new WindowTimer(boss, newMinutes, this, findServer(event))).start();
        } else {
            boss.setCurrentTime(minutes);
            new Thread(new WindowTimer(boss, minutes, this, findServer(event))).start();
        }
    }

    /**
     * Method handling the command that resets a boss timer.
     * 
     * @param boss  The boss to reset
     * @param event The event that triggered the command
     */
    public void reset(Boss boss, MessageReceivedEvent event) {
        RespawnTimer respawnTimer = null;
        WindowTimer windowTimer = null;
        for (RespawnTimer t : respawnTimers) {
            if (t.getBoss() == boss) {
                t.cancel();
                respawnTimer = t;
                break;
            }
        }

        if (respawnTimer != null) {
            respawnTimers.remove(respawnTimer);
        } else {
            for (WindowTimer t : windowTimers) {
                if (t.getBoss() == boss) {
                    t.cancel();
                    windowTimer = t;
                    break;
                }
            }
            if (windowTimer != null) {
                windowTimers.remove(windowTimer);
            }
        }
        boss.setCurrentTime(0);
        boss.setIsDue(false);
        boss.setIsTimed(false);
    }

    /**
     * Method for wiping all timers from the server
     * 
     * @param server The server to wipe
     * @param event  The event that triggered the command
     */
    public void wipe(Server server, MessageReceivedEvent event) {
        for (Boss boss : server.getBosses()) {
            if (boss.getIsTimed()) {
                reset(boss, event);
            }
        }
    }

    /**
     * Method handling the help command
     * 
     * @param event The event that triggered the command
     */
    public void help(MessageReceivedEvent event) {
        EmbedBuilder help = new EmbedBuilder();
        help.setTitle("Info");
        help.setDescription(HELP_MESSAGE);
        help.setColor(0x2403fc);
        help.setFooter("Pinged by " + event.getMember().getUser().getAsTag(),
                event.getMember().getUser().getAvatarUrl());

        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessageEmbeds(help.build()).queue();
        help.clear();
    }

    /**
     * Method handling the bosslist command
     * 
     * @param event The event that triggered the command
     */
    public void bossList(MessageReceivedEvent event) {
        String bosses = "";
        for (Boss i : findServer(event).getBosses()) {
            bosses += i.getName() + "\n";
        }

        String bossListMessage = "**Boss List:**\n"
                + bosses;

        EmbedBuilder bossList = new EmbedBuilder();
        bossList.setTitle("Bosses");
        bossList.setDescription(bossListMessage);
        bossList.setColor(0x2403fc);
        bossList.setFooter("Pinged by " + event.getMember().getUser().getAsTag(),
                event.getMember().getUser().getAvatarUrl());

        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessageEmbeds(bossList.build()).queue();
        bossList.clear();
    }

    /**
     * Method for finding the server object from the list of servers
     * 
     * @param event The event that triggered the command
     * @return The server object
     */
    public Server findServer(MessageReceivedEvent event) {
        Server server = null;

        for (Server s : servers) {
            if (s.getServerId().equalsIgnoreCase(event.getGuild().getId())) {
                server = s;
                break;
            }
        }
        return server;
    }

    /**
     * Method for finding the associated boss in the command.
     * 
     * @param bossName The name of the boss
     * @param event    The event that triggered the command
     * @return The boss object
     */
    public Boss findBoss(String bossName, MessageReceivedEvent event) {
        Boss boss = null;

        for (Boss b : findServer(event).getBosses()) {
            List<String> nameList = new ArrayList();
            for (String nick : b.getNicks()) {
                nameList.add(nick.toLowerCase());
            }
            nameList.add(b.getName().toLowerCase());

            if (nameList.contains(bossName.toLowerCase())) {
                boss = b;
                break;
            }
        }

        return boss;
    }

    /**
     * Method for converting a string input to minutes
     * 
     * @param input The input string
     * @return The total minutes
     */
    public int convertToMinutes(String[] parts) {

        // Extract the values for days, hours, and minutes
        int days = 0, hours = 0, minutes = 0;
        for (String part : parts) {
            if (part.endsWith("d")) {
                days = Integer.parseInt(part.substring(0, part.length() - 1));
            } else if (part.endsWith("h")) {
                hours = Integer.parseInt(part.substring(0, part.length() - 1));
            } else if (part.endsWith("m")) {
                minutes = Integer.parseInt(part.substring(0, part.length() - 1));
            }
        }

        // Convert days, hours, and minutes to total minutes
        int totalMinutes = days * 24 * 60 + hours * 60 + minutes;
        return totalMinutes;
    }

    /**
     * Method for creating a new server
     * 
     * @param serverName The name of the server
     * @param event      The event that triggered the command
     */
    public void newServer(String serverName, MessageReceivedEvent event) {
        boolean sameName = false;
        for (Server i : servers) {
            if (i.getServerName().equalsIgnoreCase(serverName)) {
                sameName = true;
                break;
            }
        }
        if (sameName) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("There is already a server with that name.").queue();
        } else {
            Server newServer = new Server(serverName, event.getGuild().getId());
            newServer.setChannel(event.getChannel());
            servers.add(newServer); // Creates a new station, with station name
                                    // and server name, then adds to list of
                                    // stations.
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Created new server for this server's timers.").queue();
        }
    }

    // /**
    // * Method for unsyncing a server
    // *
    // * @param event The event that triggered the command
    // */
    // public void unsyncServer(MessageReceivedEvent event) {
    // Server server = findServer(event);
    // server.unsyncServer();
    // event.getChannel().sendTyping().queue();
    // event.getChannel().sendMessage("Removed this server from it's synced
    // timers.").queue();
    // }

    // public void syncServer(String serverName, MessageReceivedEvent event) {
    // Server currentServer = findServer(event);
    // for (Server i : servers) {
    // if (i.getServerName().equalsIgnoreCase(serverName)) {
    // i.syncServer(currentServer);
    // break;
    // }
    // }
    // event.getChannel().sendTyping().queue();
    // event.getChannel().sendMessage("Synced this server's timers with server: " +
    // serverName).queue();
    // }

    /**
     * Method for showing a list of active servers
     * 
     * @param event
     */
    public void showServers(MessageReceivedEvent event) {
        if (servers.size() > 0) {
            String string = "";
            for (Server i : servers) {
                String syncedServers = "";
                for (Server server : i.getSyncedServers()) {
                    syncedServers += "- " + server.getServerName() + "\n";
                }
                String server = "**Server " + i.getServerName() + "**\n"
                        + "__Synced Servers:__\n" + syncedServers + "\n";
                string += server;
            }
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(string).queue();
        } else {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("No servers to show.").queue();
        }
    }

    /**
     * Method for adding a ping role to a boss
     * 
     * @param bossType The type of boss
     * @param roleId   The id of the role
     * @param event    The event that triggered the command
     */
    public void addPing(String bossType, String roleId, MessageReceivedEvent event) {
        if (bossType.equalsIgnoreCase("warden")) {
            findServer(event).setWardenRole(roleId);
        } else if (bossType.equalsIgnoreCase("meteoric")) {
            findServer(event).setMetRole(roleId);
        } else if (bossType.equalsIgnoreCase("frozen")) {
            findServer(event).setFrozenRole(roleId);
        } else if (bossType.equalsIgnoreCase("dl")) {
            findServer(event).setDlRole(roleId);
        } else if (bossType.equalsIgnoreCase("edl")) {
            findServer(event).setEdlRole(roleId);
        } else if (bossType.equalsIgnoreCase("rings")) {
            findServer(event).setRingsRole(roleId);
        } else if (bossType.equalsIgnoreCase("mids")) {
            findServer(event).setMidsRole(roleId);
        } else if (bossType.equalsIgnoreCase("egs")) {
            findServer(event).setEgsRole(roleId);
        }
    }

    /**
     * Method for removing a ping role from a boss
     * 
     * @param bossType The type of boss
     * @param event    The event that triggered the command
     */
    public void removePing(String bossType, MessageReceivedEvent event) {
        if (bossType.equalsIgnoreCase("warden")) {
            findServer(event).setWardenRole(null);
        } else if (bossType.equalsIgnoreCase("meteoric")) {
            findServer(event).setMetRole(null);
        } else if (bossType.equalsIgnoreCase("frozen")) {
            findServer(event).setFrozenRole(null);
        } else if (bossType.equalsIgnoreCase("dl")) {
            findServer(event).setDlRole(null);
        } else if (bossType.equalsIgnoreCase("edl")) {
            findServer(event).setEdlRole(null);
        } else if (bossType.equalsIgnoreCase("rings")) {
            findServer(event).setRingsRole(null);
        } else if (bossType.equalsIgnoreCase("mids")) {
            findServer(event).setMidsRole(null);
        } else if (bossType.equalsIgnoreCase("egs")) {
            findServer(event).setEgsRole(null);
        }
    }

    /**
     * Method for setting up a server
     * 
     * @param server The server to set up
     * @param event  The event that triggered the command
     */
    public void setup(String server, MessageReceivedEvent event) {
        if (server.equalsIgnoreCase("epona")) { // Sets up the server. Only thing left to do is addchannel.
            newServer("Epona", event);
            addPing("Warden", "<@&994520416607535175>", event);
            addPing("Meteoric", "<@&994520484286832680>", event);
            addPing("Frozen", "<@&994520528347992114>", event);
            addPing("Dl", "<@&994520591086387200>", event);
            addPing("Edl", "<@&994520623269285928>", event);
            addPing("Rings", "<@&998189537199128596>", event);
            addPing("Mids", "<@&994520655972286494>", event);
            addPing("Egs", "<@&994520688251650139>", event);
            setChannel(event);
        } else if (server.equalsIgnoreCase("Resurgence")) {
            newServer("Resurgence", event);
            addPing("Warden", "<@&998176083184734329>", event);
            addPing("Meteoric", "<@&998176117716434965>", event);
            addPing("Frozen", "<@&998176144937451550>", event);
            addPing("Dl", "<@&998176174515695746>", event);
            addPing("Edl", "<@&998176198268047370>", event);
            addPing("Rings", "<@&998189198894977074>", event);
            addPing("Mids", "<@&998176225828802611>", event);
            addPing("Egs", "<@&998176264433176696>", event);
            setChannel(event);
        }
    }

    /**
     * Method for setting the channel for the server
     * 
     * @param event The event that triggered the command
     */
    public void setChannel(MessageReceivedEvent event) {
        Server server = findServer(event);
        if (server == null) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Your server is not setup yet!").queue();
        } else {
            server.setChannel(event.getChannel());
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Set " + event.getChannel().getAsMention() + " as the timer channel.")
                    .queue();
        }
    }

    public void addRespawnTimer(RespawnTimer timer) {
        respawnTimers.add(timer);
    }

    public void removeRespawnTimer(RespawnTimer timer) {
        respawnTimers.remove(timer);
    }

    public void addWindowTimer(WindowTimer timer) {
        windowTimers.add(timer);
    }

    public void removeWindowTimer(WindowTimer timer) {
        windowTimers.remove(timer);
    }

}
