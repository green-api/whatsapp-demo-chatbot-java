package com.greenapi.demoChatbot;

import com.greenapi.chatbot.pkg.BotFactory;
import com.greenapi.client.pkg.models.request.InstanceSettingsReq;
import com.greenapi.demoChatbot.scenes.Start;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class BotStarter {

    public static void main(String[] args) {
        var context = SpringApplication.run(BotStarter.class, args);
        var botFactory = context.getBean(BotFactory.class);
        var startScene = context.getBean(Start.class);

        var instanceId = context.getEnvironment().getProperty("user_id");
        var token = context.getEnvironment().getProperty("api_token_id");
//        var instanceId = "7103900211";
//        var token = "88f72c51378244468289b680a81dc77bcb3f705de66949ac9e";

        var bot = botFactory.createBot(instanceId, token);

        bot.greenApi.account.setSetting(InstanceSettingsReq.builder()
            .incomingWebhook("yes")
            .outgoingMessageWebhook("yes")
            .outgoingAPIMessageWebhook("yes")
            .pollMessageWebhook("yes")
            .markIncomingMessagesReaded("yes")
            .build());
        log.info("Settings updated by bot");

        bot.setStartScene(startScene);

        bot.startReceivingNotifications();
    }
}