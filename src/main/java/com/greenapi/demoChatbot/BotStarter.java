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
        var instanceId = context.getEnvironment().getProperty("sapi_user_id");
        var token = context.getEnvironment().getProperty("sapi_user_token");

        var bot = botFactory.createBot(instanceId, token);

        bot.greenApi.account.setSetting(InstanceSettingsReq.builder()
            .incomingWebhook("yes")
            .outgoingMessageWebhook("yes")
            .outgoingAPIMessageWebhook("yes")
            .build());

        bot.setStartScene(new Start());

        bot.startReceivingNotifications();
    }
}