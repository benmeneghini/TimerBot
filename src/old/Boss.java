package model;

import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Boss {

    private String name;
    private int spawnTime; // in minutes
    private boolean isTimed = false;
    private boolean isDue = false;
    private int currentTime; // in minutes
    private int window;
    private List<String> nicks;

    public Boss(String name, int spawnTime, int window, List<String> nicks) {
        this.name = name;
        this.spawnTime = spawnTime;
        this.window = window;
        this.nicks = nicks;
    }

    public String getName() {
        return name;
    }

    public int getSpawnTime() {
        return spawnTime;
    }

    public boolean getIsTimed() {
        return isTimed;
    }

    public void setIsTimed(boolean bool) {
        isTimed = bool;
    }

    public boolean getIsDue() {
        return isDue;
    }

    public void setIsDue(boolean bool) {
        isDue = bool;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int minutes) {
        currentTime = minutes;
    }

    public void incrementCurrentTime() {
        currentTime -= 1;
    }

    public int getWindow() {
        return window;
    }

    public List<String> getNicks() {
        return nicks;
    }

    public String getFormattedTime() {
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int time = this.currentTime;

        while (time >= 1440) {
            days += 1;
            time -= 1440;
        }
        while (time >= 60) {
            hours += 1;
            time -= 60;
        }
        minutes += time;
        String formatted = "";
        if (days > 0) {
            if (hours > 0) {
                if (minutes > 0) {
                    formatted += days + " days, " + hours + " hours, " + minutes + " minutes.";
                } else {
                    formatted += days + " days, " + hours + " hours.";
                }
            } else if (minutes > 0) {
                formatted += days + " days, " + minutes + " minutes.";
            } else {
                formatted += days + " days.";
            }
        } else if (hours > 0) {
            if (minutes > 0) {
                formatted += hours + " hours, " + minutes + " minutes.";
            } else {
                formatted += hours + " hours.";
            }
        } else {
            formatted += minutes + " minutes.";
        }
        return formatted;
    }

}
