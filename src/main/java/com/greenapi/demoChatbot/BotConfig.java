package com.greenapi.demoChatbot;

import com.greenapi.chatbot.pkg.BotFactory;
import com.greenapi.chatbot.pkg.state.StateManager;
import com.greenapi.chatbot.pkg.state.StateManagerHashMapImpl;
import com.greenapi.demoChatbot.scenes.Endpoints;
import com.greenapi.demoChatbot.scenes.MainMenu;
import com.greenapi.demoChatbot.scenes.Start;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BotConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Bean
    public StateManager stateManager() {
        return new StateManagerHashMapImpl();
    }

    @Bean
    public BotFactory botFactory(RestTemplate restTemplate, StateManager stateManager) {
        return new BotFactory(restTemplate, stateManager);
    }
}
