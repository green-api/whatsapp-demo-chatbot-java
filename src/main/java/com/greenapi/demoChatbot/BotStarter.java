package com.greenapi.demoChatbot;

import com.greenapi.chatbot.pkg.BotFactory;
import com.greenapi.demoChatbot.scenes.Start;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotStarter {

    public static void main(String[] args) {
        var context = SpringApplication.run(BotStarter.class, args);
        var botFactory = context.getBean(BotFactory.class);

        var bot = botFactory.createBot(
            "{{instance}}",
            "{{TOKEN}}");

        bot.setStartScene(new Start());

        bot.startReceivingNotifications();
    }
}