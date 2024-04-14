package service;

import model.Boss;
import model.Server;
import control.CommandController;

public class WindowTimer implements Runnable {

    private Boss boss;
    private int durationInMinutes;
    private CommandController controllerClass;
    private Server server;

    public WindowTimer(Boss boss, int durationInMinutes, CommandController controllerClass, Server server) {
        this.boss = boss;
        this.durationInMinutes = durationInMinutes;
        this.controllerClass = controllerClass;
        this.server = server;
    }

    @Override
    public void run() {
        controllerClass.addWindowTimer(this);
        boss.setIsTimed(true);
        boss.setIsDue(true);
        boss.setCurrentTime(durationInMinutes);

        /**
         * Times the window time
         */
        while (durationInMinutes > 0 && boss.getIsTimed() && boss.getIsDue()) {
            boss.decrementTime();
            durationInMinutes--;
            try {
                Thread.sleep(60000); // Sleep for 1 minute.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }

        controllerClass.removeWindowTimer(this);
        boss.setIsDue(false);
        boss.setIsTimed(false);
        return;
    }

    public Boss getBoss() {
        return boss;
    }

    public void cancel() {
        Thread.currentThread().interrupt();
    }

}
