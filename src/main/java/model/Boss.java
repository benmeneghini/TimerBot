package model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Boss {
    private String name;
    private List<String> nicks;
    private BossType type;
    private int respawn;
    private int window;

    private int currentTime;
    private boolean isTimed;
    private boolean isDue;

    @JsonCreator
    public Boss(@JsonProperty("name") String name, @JsonProperty("nicknames") List<String> nicks,
            @JsonProperty("type") BossType type, @JsonProperty("respawn") int respawn,
            @JsonProperty("window") int window) {
        this.name = name;
        this.nicks = nicks;
        this.type = type;
        this.respawn = respawn;
        this.window = window;
    }

    public String getName() {
        return name;
    }

    public List<String> getNicks() {
        return nicks;
    }

    public BossType getType() {
        return type;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public boolean getIsTimed() {
        return isTimed;
    }

    public boolean getIsDue() {
        return isDue;
    }

    public void setIsTimed(boolean isTimed) {
        this.isTimed = isTimed;
    }

    public void setIsDue(boolean isDue) {
        this.isDue = isDue;
    }

    public void decrementTime() {
        currentTime -= 1;
    }

    public BossType getBossType() {
        return type;
    }

    public int getWindow() {
        return window;
    }

    public int getRespawn() {
        return respawn;
    }

    public String getFormattedTime() {
        int totalMinutes = this.currentTime;

        // Calculate days, hours, and minutes
        int days = totalMinutes / (24 * 60);
        int remainingMinutes = totalMinutes % (24 * 60);
        int hours = remainingMinutes / 60;
        int minutes = remainingMinutes % 60;

        // Build the formatted string
        StringBuilder formattedTime = new StringBuilder();
        if (days > 0) {
            formattedTime.append(days).append(" ").append(days > 1 ? "days" : "day").append(" ");
        }
        if (hours > 0 || days > 0) {
            formattedTime.append(hours).append(" ").append(hours > 1 ? "hours" : "hour").append(" ");
        }
        formattedTime.append(minutes).append(" ").append(minutes > 1 ? "minutes" : "minute");

        return formattedTime.toString();
    }
}
