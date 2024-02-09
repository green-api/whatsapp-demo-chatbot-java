package com.greenapi.demoChatbot;

import com.greenapi.chatbot.pkg.BotFactory;
import com.greenapi.client.pkg.models.request.InstanceSettingsReq;
import com.greenapi.demoChatbot.scenes.Start;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotStarter {

    public static void main(String[] args) {
        var context = SpringApplication.run(BotStarter.class, args);
        var botFactory = context.getBean(BotFactory.class);
        var startScene = context.getBean(Start.class);
        var instanceId = context.getEnvironment().getProperty("user_id");
        var token = context.getEnvironment().getProperty("api_token_id");

        var bot = botFactory.createBot(instanceId, token);

        bot.greenApi.account.setSetting(InstanceSettingsReq.builder()
            .incomingWebhook("yes")
            .outgoingMessageWebhook("yes")
            .outgoingAPIMessageWebhook("yes")
            .pollMessageWebhook("yes")
            .markIncomingMessagesReaded("yes")
            .build());

        bot.setStartScene(startScene);

        bot.startReceivingNotifications();
    }
}