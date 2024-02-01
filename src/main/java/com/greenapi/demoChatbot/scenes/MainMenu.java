package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.demoChatbot.util.Language;
import com.greenapi.demoChatbot.util.SessionManager;
import com.greenapi.demoChatbot.util.YmlReader;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainMenu extends Scene {

    @Autowired
    private Endpoints endpointsScene;

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        if (SessionManager.isSessionExpired(currentState)) {
            return activateStartScene(currentState);
        }

        var messageText = getText(incomingMessage);
        if (messageText.isPresent()) {
            switch (messageText.get()) {
                case "1" -> {
                    return sendMainMenu(incomingMessage, currentState, Language.ENG);
                }
                case "2" -> {
                    return sendMainMenu(incomingMessage, currentState, Language.KZ);
                }
                case "3" -> {
                    return sendMainMenu(incomingMessage, currentState, Language.RU);
                }
                case "4" -> {
                    return sendMainMenu(incomingMessage, currentState, Language.HE);
                }
                case "5" -> {
                    return sendMainMenu(incomingMessage, currentState, Language.ES);
                }
                case "6" -> {
                    return sendMainMenu(incomingMessage, currentState, Language.AR);
                }
                default -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"specify_language"}));

                    return currentState;
                }
            }
        }

        return currentState;
    }

    private State sendMainMenu(MessageWebhook incomingMessage, State currentState, Language language) {
        currentState.getData().put("lang", language);
        answerWithText(incomingMessage,
            YmlReader.getString(new String[]{"welcome_message", language.getValue()}) +
                incomingMessage.getSenderData().getSenderName() + "\n" +
                YmlReader.getString(new String[]{"menu",language.getValue()})
        );

        return activateNextScene(currentState, endpointsScene);
    }
}
