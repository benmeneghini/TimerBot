package service;

import model.Boss;
import model.Server;
import control.CommandController;

public class RespawnTimer implements Runnable {

    private Boss boss;
    private int durationInMinutes;
    private CommandController controllerClass;
    private Server server;

    public RespawnTimer(Boss boss, int durationInMinutes, CommandController controllerClass, Server server) {
        this.boss = boss;
        this.durationInMinutes = durationInMinutes;
        this.controllerClass = controllerClass;
        this.server = server;
    }

    @Override
    public void run() {
        controllerClass.addRespawnTimer(this);
        boss.setIsTimed(true);
        boss.setCurrentTime(durationInMinutes);

        /**
         * Times the respawn time
         */
        while (durationInMinutes > 0 && boss.getIsTimed()) {
            boss.decrementTime();

            /**
             * If the time is 3 minutes, ping the boss
             */
            if (durationInMinutes == 3) {
                server.ping(boss);
                for (Server s : server.getSyncedServers()) {
                    s.ping(boss);
                }
            }

            durationInMinutes--;

            try {
                Thread.sleep(60000); // Sleep for 1 minute.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        controllerClass.removeRespawnTimer(this);
        if (boss.getIsTimed()) {
            new Thread(new WindowTimer(boss, boss.getWindow(), controllerClass, server)).start();
        }
        return;
    }

    public Boss getBoss() {
        return boss;
    }

    public void cancel() {
        Thread.currentThread().interrupt();
    }

}
