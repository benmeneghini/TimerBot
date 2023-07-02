package modules;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class Main {

    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault("OTQyNTI1OTE5NjczODYwMTM2.GKqeSu.UFZdnh6mGq-mACZ4xk6HGNAkfkCBrvTlmEQWq8").build();

        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        jda.getPresence().setActivity(Activity.playing("Celtic Heroes"));

        jda.addEventListener(new Controller());
    }

}
