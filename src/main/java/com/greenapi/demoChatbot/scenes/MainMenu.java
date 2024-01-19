package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.demoChatbot.util.Language;
import com.greenapi.demoChatbot.util.SessionManager;

public class MainMenu extends ExtendedScene {

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        if (SessionManager.isSessionExpired(currentState)) {
            return activateStartScene(currentState);
        }

        var messageText = getText(incomingMessage);
        if (messageText.isPresent()) {
            switch (messageText.get()) {
                case "1" -> {
                    currentState.getData().put("lang", Language.ENG);
                    answerWithText(incomingMessage,
                        stringsYml.getText("welcome_message", Language.ENG) +
                            incomingMessage.getSenderData().getSenderName() +
                            stringsYml.getText("menu", Language.ENG)
                    );

                    return activateNextScene(currentState, new Endpoints());
                }
                case "2" -> {
                    currentState.getData().put("lang", Language.RU);
                    answerWithText(incomingMessage,
                        stringsYml.getText("welcome_message", Language.RU) +
                            incomingMessage.getSenderData().getSenderName() +
                            stringsYml.getText("menu", Language.RU)
                    );

                    return activateNextScene(currentState, new Endpoints());
                }
                default -> {
                    answerWithText(incomingMessage, stringsYml.getText("specify_language"));

                    return currentState;
                }
            }
        }

        return currentState;
    }
}
