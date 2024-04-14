package service;

import controller.Listener;
import model.Boss;
import model.Server;

public class Timer implements Runnable {

    private Boss boss;
    private int time; // in minutes.
    private Listener controllerClass;
    private Server server;

    public Timer(Boss boss, int time, Listener controllerClass, Server server) {
        this.boss = boss;
        this.time = time;
        this.controllerClass = controllerClass;
        this.server = server;
    }

    @Override
    public void run() {
        controllerClass.addTimer(this);
        boss.setIsTimed(true);
        boss.setCurrentTime(time);
        while (time > 3 && boss.getIsTimed()) {
            boss.incrementCurrentTime();
            time -= 1;
            try {
                Thread.sleep(60000); // Sleep for 1 minute.
            } catch (InterruptedException e) {
            }
        }
        if (time > 0 && boss.getIsTimed()) {
            server.ping(boss);
            for (Server i : server.getSyncedServers()) {
                i.ping(boss);
            }
        }
        while (time > 0 && boss.getIsTimed()) {
            boss.incrementCurrentTime();
            time -= 1;
            try {
                Thread.sleep(60000); // Sleep for 1 minute.
            } catch (InterruptedException e) {
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        if (time == 0) { // Normal run through. This avoids some issues i forgot.
            time = boss.getWindow();
            boss.setCurrentTime(boss.getWindow());
            boss.setIsDue(true);
        }
        while (time > 0 && boss.getIsTimed() && boss.getIsDue()) {
            boss.incrementCurrentTime();
            time -= 1;
            try {
                Thread.sleep(60000); // Sleep for 1 minute.
            } catch (InterruptedException e) {
            }
        }
        controllerClass.removeTimer(this);
        if (time == 0) {
            boss.setIsDue(false);
            boss.setIsTimed(false);
        }
    }

    public Boss getBoss() {
        return boss;
    }

    public void cancel() {
        time = -10000;
    }

}
