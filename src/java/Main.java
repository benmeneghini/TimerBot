
import service.Timer;
import javax.security.auth.login.LoginException;

import control.CommandController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        // OTQyNTI1OTE5NjczODYwMTM2.GKqeSu.UFZdnh6mGq-mACZ4xk6HGNAkfkCBrvTlmEQWq8 main
        // OTk0NzU5NDA2NjU2OTQ2MjA2.GiK5GM.bWNRWGV6sR5Xtqq9eMGAbLfqRi9IhcNxsnM2DQ test
        JDA jda = JDABuilder.createDefault("OTQyNTI1OTE5NjczODYwMTM2.GKqeSu.UFZdnh6mGq-mACZ4xk6HGNAkfkCBrvTlmEQWq8")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        jda.getPresence().setStatus(OnlineStatus.ONLINE);
        jda.getPresence().setActivity(Activity.playing("Celtic Heroes"));

        CommandController commandController = new CommandController();

        jda.addEventListener(commandController);

        new Thread(new Timer(commandController)).start();

        System.out.println("Bot is online!");
    }

}
