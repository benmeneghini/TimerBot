package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Boss;
import model.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import service.Timer;

public class Listener extends ListenerAdapter {

    private List<String> bossStrings = Arrays.asList("falgren", "doomclaw", "bonehead", "redbane", "goretusk",
            "rockbelly", "coppinger", "copp",
            "eye", "swampie", "swampy", "woody", "chained", "chain", "grom", "pyrus",
            "py", "155", "spider", "160", "priest", "165", "king", "170", "bolg", "180", "snorri",
            "185", "190", "195", "200", "205", "210", "215", "unox",
            "north ring", "north", "east ring", "east", "centre ring", "centre", "center", "south ring", "south",
            "aggy", "aggorath", "hrung", "hrungir", "mord", "mordris", "necro", "necromancer",
            "prot", "proteus", "gele", "gelebron", "bt", "bloodthorn", "dino", "dhio", "dhiothu");

    private List<Timer> runningTimers = new ArrayList<Timer>();

    private List<Server> servers = new ArrayList<Server>();

    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase("server") && args.length >= 2 && args.length <= 3
                && (event.getAuthor().getAsTag().equalsIgnoreCase("Yeek's Bot#7154")
                        || event.getAuthor().getAsTag().equalsIgnoreCase("yeek#0000"))) {
            if (args[1].equalsIgnoreCase("new") && args.length == 3) {
                newServer(args[2], event);
            } else if (args[1].equalsIgnoreCase("unsync") && args.length == 2) {
                unsyncServer(event);
            } else if (args[1].equalsIgnoreCase("sync") && args.length == 3) {
                syncServer(args[2], event);
            } else if (args[1].equalsIgnoreCase("delete") && args.length == 3) {
                deleteServer(args[2], event);
            }
        } else if (args[0].equalsIgnoreCase("showservers")
                && (event.getAuthor().getAsTag().equalsIgnoreCase("Yeek's Bot#7154")
                        || event.getAuthor().getAsTag().equalsIgnoreCase("yeek#0000"))
                && args.length == 1) {
            showServers(event);
        } else if (args[0].equalsIgnoreCase("ping") && (event.getAuthor().getAsTag().equalsIgnoreCase("Yeek's Bot#7154")
                || event.getAuthor().getAsTag().equalsIgnoreCase("yeek#0000"))) {
            if (args[1].equalsIgnoreCase("add") && args.length == 4) {
                addPing(args[2], args[3], event);
            } else if (args[1].equalsIgnoreCase("remove") && args.length == 3) {
                removePing(args[2], event);
            }
        } else if (args[0].equalsIgnoreCase("setup")
                && (event.getAuthor().getAsTag().equalsIgnoreCase("Yeek's Bot#7154")
                        || event.getAuthor().getAsTag().equalsIgnoreCase("yeek#0000"))
                && args.length == 2) {
            setup(args[1], event);
        } else if (!(findServer(event) == null) && (!(findServer(event).getChannel() == null))
                && findServer(event).getChannel().equals(event.getChannel()) && !(event.getAuthor().isBot())) {
            if (args[0].equalsIgnoreCase("soon")) { // User typed "soon" command.
                soon(event); // Calls soon method.

            } else if (args[0].equalsIgnoreCase("set")) { // User typed "set" command.
                if (args.length > 2 && args.length < 6) {
                    Boss boss = null; // Gets the boss.
                    for (Boss i : findServer(event).getBosses()) { // Get which boss.
                        if (i.getName().equalsIgnoreCase(args[1]) || i.getNicks().contains(args[1].toLowerCase())) {
                            boss = i;
                            break;
                        }
                    }
                    if ((boss == null)) { // If the user used the incorrect boss name.
                        usageError("set", event);
                    } else {

                        // This part checks if the time is in the correct format.
                        List<String> time = new ArrayList<String>();
                        if (args.length == 3) {
                            if (args[2].endsWith("d") || args[2].endsWith("h") || args[2].endsWith("m")) {
                                time.add(args[2]);
                                set(boss, time, event);
                            } else {
                                usageError("set", event);
                            }
                        } else if (args.length == 4) {
                            if ((args[2].endsWith("d") && args[3].endsWith("h"))
                                    || (args[2].endsWith("d") && args[3].endsWith("m"))) {
                                time.add(args[2]);
                                time.add(args[3]);
                                set(boss, time, event);
                            } else if (args[2].endsWith("h") && args[3].endsWith("m")) {
                                time.add(args[2]);
                                time.add(args[3]);
                                set(boss, time, event);
                            } else {
                                usageError("set", event);
                            }
                        } else if (args.length == 5) {
                            if (args[2].endsWith("d") && args[3].endsWith("h") && args[4].endsWith("m")) {
                                time.add(args[2]);
                                time.add(args[3]);
                                time.add(args[4]);
                                set(boss, time, event);
                            } else {
                                usageError("set", event);
                            }
                        }
                    }
                } else {
                    usageError("set", event);
                }

            } else if (args[0].equalsIgnoreCase("reset")) {
                Boss boss = null; // Gets the boss.
                for (Boss i : findServer(event).getBosses()) { // Get which boss.
                    if (i.getName().equalsIgnoreCase(args[1]) || i.getNicks().contains(args[1].toLowerCase())) {
                        boss = i;
                        break;
                    }
                }
                if (boss == null) { // If the user used the incorrect boss name.
                    usageError("reset", event);
                } else {
                    reset(boss, event);
                }
            } else if (bossStrings.contains(args[0].toLowerCase()) && (args.length == 1 || args.length == 2)) { // Timing
                                                                                                                // a
                                                                                                                // boss
                                                                                                                // command.
                Boss boss = null; // Gets the boss.
                for (Boss i : findServer(event).getBosses()) { // Get which boss.
                    if (i.getName().equalsIgnoreCase(args[0]) || i.getNicks().contains(args[0].toLowerCase())) {
                        boss = i;
                        break;
                    }
                }
                if (boss == null) { // If the user used the incorrect boss name.
                    usageError("boss", event);
                } else {
                    time(boss, event);
                }
            } else if (args[0].equalsIgnoreCase("help") && args.length == 1) {
                help(event);
            } else if (args[0].equalsIgnoreCase("bosslist") && args.length == 1) {
                bossList(event);
            } else {
                usageError("none", event);
            }

        } else if (args[0].equalsIgnoreCase("setchannel")
                && (event.getAuthor().getAsTag().equalsIgnoreCase("Yeek's Bot#7154")
                        || event.getAuthor().getAsTag().equalsIgnoreCase("yeek#0000"))
                && args.length == 1) {
            setChannel(event);
        }

    }

    public void soon(MessageReceivedEvent event) {
        String egs = "**EGS:**\n";
        String mids = "**MIDS:**\n";
        String rings = "**RINGS:**\n";
        String edl = "**EDL:**\n";
        String dl = "**DL:**\n";
        String frozen = "**FROZEN:**\n";
        String met = "**METEORIC:**\n";
        String warden = "**WARDEN:**\n";
        String times = "";

        for (Boss boss : findServer(event).getEgs()) {
            if (boss.getIsDue()) {
                egs += "__" + boss.getName() + " is DUE!__ Window closes in " + boss.getFormattedTime() + "\n";
            } else if (boss.getIsTimed()) {
                egs += boss.getName() + " in " + boss.getFormattedTime() + "\n";
            }
        }
        if (egs.length() > 9) {
            times += egs;
        }

        for (Boss boss : findServer(event).getMids()) {
            if (boss.getIsDue()) {
                mids += "__" + boss.getName() + " is DUE!__ Window closes in " + boss.getFormattedTime() + "\n";
            } else if (boss.getIsTimed()) {
                mids += boss.getName() + " in " + boss.getFormattedTime() + "\n";
            }
        }
        if (mids.length() > 10) {
            times += mids;
        }

        for (Boss boss : findServer(event).getRings()) {
            if (boss.getIsDue()) {
                rings += "__" + boss.getName() + " is DUE!__\n";
            } else if (boss.getIsTimed()) {
                rings += boss.getName() + " in " + boss.getFormattedTime() + "\n";
            }
        }
        if (rings.length() > 11) {
            times += rings;
        }

        for (Boss boss : findServer(event).getEdl()) {
            if (boss.getIsDue()) {
                edl += "__" + boss.getName() + " is DUE!__\n";
            } else if (boss.getIsTimed()) {
                edl += boss.getName() + " in " + boss.getFormattedTime() + "\n";
            }
        }
        if (edl.length() > 9) {
            times += edl;
        }

        for (Boss boss : findServer(event).getDl()) {
            if (boss.getIsDue()) {
                dl += "__" + boss.getName() + " is DUE!__\n";
            } else if (boss.getIsTimed()) {
                dl += boss.getName() + " in " + boss.getFormattedTime() + "\n";
            }
        }
        if (dl.length() > 8) {
            times += dl;
        }

        for (Boss boss : findServer(event).getFrozen()) {
            if (boss.getIsDue()) {
                frozen += "__" + boss.getName() + " is DUE!__\n";
            } else if (boss.getIsTimed()) {
                frozen += boss.getName() + " in " + boss.getFormattedTime() + "\n";
            }
        }
        if (frozen.length() > 12) {
            times += frozen;
        }

        for (Boss boss : findServer(event).getMet()) {
            if (boss.getIsDue()) {
                met += "__" + boss.getName() + " is DUE!__\n";
            } else if (boss.getIsTimed()) {
                met += boss.getName() + " in " + boss.getFormattedTime() + "\n";
            }
        }
        if (met.length() > 14) {
            times += met;
        }

        for (Boss boss : findServer(event).getWarden()) {
            if (boss.getIsDue()) {
                warden += "__" + boss.getName() + " is DUE!__\n";
            } else if (boss.getIsTimed()) {
                warden += boss.getName() + " in " + boss.getFormattedTime() + "\n";
            }
        }
        if (warden.length() > 12) {
            times += warden;
        }

        if (times.length() > 0) {
            EmbedBuilder soon = new EmbedBuilder();
            soon.setTitle("⏳ Boss Times ⏳");
            soon.setDescription(times);
            // soon.addField("Creator", "Yeek", false);
            soon.setColor(0x630505);
            soon.setFooter("Pinged by " + event.getMember().getUser().getAsTag(),
                    event.getMember().getUser().getAvatarUrl());

            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessageEmbeds(soon.build()).queue();
            soon.clear();
        } else {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("No bosses currently timed! :(").queue();
        }

    }

    public void set(Boss boss, List<String> time, MessageReceivedEvent event) {
        if (boss.getIsTimed() || boss.getIsDue()) {
            reset(boss, event);
        }
        int minutes = convertMinutes(time);
        boss.setCurrentTime(minutes);
        new Thread(new Timer(boss, minutes, this, findServer(event))).start();
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(boss.getName() + " set for " + boss.getFormattedTime()).queue();
    }

    public void reset(Boss boss, MessageReceivedEvent event) {
        for (Timer i : runningTimers) {
            if (i.getBoss() == boss) {
                i.cancel();
            }
        }
        boss.setCurrentTime(0);
        boss.setIsDue(false);
        boss.setIsTimed(false);
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage("Removed " + boss.getName() + " timer.").queue();
    }

    public void time(Boss boss, MessageReceivedEvent event) {
        if (boss.getIsTimed() || boss.getIsDue()) {
            reset(boss, event);
        }
        new Thread(new Timer(boss, boss.getSpawnTime(), this, findServer(event))).start();
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(boss.getName() + " timed.").queue();
    }

    public int convertMinutes(List<String> time) {
        int mins = 0;
        if (time.size() == 1) {
            int size = time.get(0).length();
            try {
                int number = Integer.parseInt(time.get(0).substring(0, size - 1));
                if (time.get(0).endsWith("d")) {
                    mins = 1440 * number;
                } else if (time.get(0).endsWith("h")) {
                    mins = 60 * number;
                } else if (time.get(0).endsWith("m")) {
                    mins = number;
                }
            } catch (NumberFormatException ex) {
            }
        } else if (time.size() == 2) {
            try {
                int size = time.get(0).length();
                int number = Integer.parseInt(time.get(0).substring(0, size - 1));
                if (time.get(0).endsWith("d")) {
                    mins += 1440 * number;
                } else if (time.get(0).endsWith("h")) {
                    mins += 60 * number;
                } else if (time.get(0).endsWith("m")) {
                    mins += number;
                }

                size = time.get(1).length();
                number = Integer.parseInt(time.get(1).substring(0, size - 1));
                if (time.get(1).endsWith("d")) {
                    mins += 1440 * number;
                } else if (time.get(1).endsWith("h")) {
                    mins += 60 * number;
                } else if (time.get(1).endsWith("m")) {
                    mins += number;
                }
            } catch (NumberFormatException ex) {
            }
        } else if (time.size() == 3) {
            try {
                int size = time.get(0).length();
                int number = Integer.parseInt(time.get(0).substring(0, size - 1));
                if (time.get(0).endsWith("d")) {
                    mins += 1440 * number;
                } else if (time.get(0).endsWith("h")) {
                    mins += 60 * number;
                } else if (time.get(0).endsWith("m")) {
                    mins += number;
                }

                size = time.get(1).length();
                number = Integer.parseInt(time.get(1).substring(0, size - 1));
                if (time.get(1).endsWith("d")) {
                    mins += 1440 * number;
                } else if (time.get(1).endsWith("h")) {
                    mins += 60 * number;
                } else if (time.get(1).endsWith("m")) {
                    mins += number;
                }

                size = time.get(2).length();
                number = Integer.parseInt(time.get(2).substring(0, size - 1));
                if (time.get(2).endsWith("d")) {
                    mins += 1440 * number;
                } else if (time.get(2).endsWith("h")) {
                    mins += 60 * number;
                } else if (time.get(2).endsWith("m")) {
                    mins += number;
                }
            } catch (NumberFormatException ex) {
            }
        }
        return mins;
    }

    public void setChannel(MessageReceivedEvent event) {
        Server server = findServer(event);
        if (server == null) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Your server is not setup yet!").queue();
        } else {
            findServer(event).setChannel(event.getChannel());
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Set " + event.getChannel().getAsMention() + " as the timer channel.")
                    .queue();
        }
    }

    public void usageError(String command, MessageReceivedEvent event) {
        if (command.equals("set")) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Usage error. Format: set [boss] {days}d {hours}h {minutes}m").queue();
        } else if (command.equals("reset")) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Usage error. Format: reset [boss]").queue();
        } else if (command.equals("boss")) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Usage error. Format: [boss]").queue();
        } else if (command.equals("none")) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Unrecognized command.\n"
                    + "Type \"help\" for more info").queue();
        }
    }

    public void help(MessageReceivedEvent event) {
        String helpMessage = "**Commands:**\n"
                + "*Soon* - Shows a list of all boss timers.\n"
                + "*[Boss]* - Times a specific boss.\n"
                + "*Set [boss] {day}d {hour}h {minute}m* - Sets a boss to a specific time.\n"
                + "*Reset [boss]* - Removes the boss's timer.\n"
                + "*Bosslist - Shows a list of timeable bosses.*\n";

        EmbedBuilder help = new EmbedBuilder();
        help.setTitle("Info");
        help.setDescription(helpMessage);
        help.setColor(0x2403fc);
        help.setFooter("Pinged by " + event.getMember().getUser().getAsTag(),
                event.getMember().getUser().getAvatarUrl());

        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessageEmbeds(help.build()).queue();
        help.clear();
    }

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

    public void addTimer(Timer timer) {
        runningTimers.add(timer);
    }

    public void removeTimer(Timer timer) {
        runningTimers.remove(timer);
    }

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
            servers.add(new Server(serverName, event.getGuild().getId())); // Creates a new station, with station name
                                                                           // and server name, then adds to list of
                                                                           // stations.
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Created new server for this server's timers.").queue();
        }
    }

    public void unsyncServer(MessageReceivedEvent event) {
        Server server = null;
        Server serverRemove = null;
        outerloop: for (Server i : servers) {
            for (Server syncedServer : i.getSyncedServers()) {
                if (syncedServer.getServerId().equalsIgnoreCase(event.getGuild().getId())) {
                    server = i;
                    serverRemove = syncedServer;
                    break outerloop;
                }
            }
        }
        server.unsyncServer(serverRemove);
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage("Removed this server from it's synced timer.").queue();
    }

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

    public void deleteServer(String serverName, MessageReceivedEvent event) {
        Server server = null;
        for (Server i : servers) {
            if (i.getServerName().equalsIgnoreCase(serverName)) {
                server = i;
                break;
            }
        }
        if (!(server == null)) {
            servers.remove(server);
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Server " + serverName + " has been deleted.").queue();
        } else {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Server " + serverName + " doesn't exist.").queue();
        }
    }

    public Server findServer(MessageReceivedEvent event) {
        Server server = null;
        for (Server i : servers) {
            if (i.getServerId().equalsIgnoreCase(event.getGuild().getId())) {
                server = i;
                break;
            }
        }
        return server;
    }

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
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage("Added ping role for " + bossType + " bosses.").queue();
    }

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
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage("Removed ping role for " + bossType + " bosses.").queue();
    }

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
        } else if (server.equalsIgnoreCase("inferno")) {
            newServer("Inferno", event);
            syncServer("Epona", event);
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
        }
    }

}
