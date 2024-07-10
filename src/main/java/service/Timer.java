package service;

import java.util.List;

import model.Server;
import control.CommandController;

public class Timer implements Runnable {

    private CommandController controllerClass;

    public Timer(CommandController controllerClass) {
        this.controllerClass = controllerClass;
    }

    @Override
    public void run() {
        while (true) {
            try {
                updateTimers();
                Thread.sleep(60000); // Sleep for 1 minute.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Updates the timers of all servers.
     */
    private void updateTimers() {
        List<Server> servers = controllerClass.getServers();

        for (Server s : servers) {
            s.updateTimers();
        }
    }
    
}
