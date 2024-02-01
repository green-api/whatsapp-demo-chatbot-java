package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.demoChatbot.util.Language;
import com.greenapi.demoChatbot.util.SessionManager;
import com.greenapi.demoChatbot.util.YmlReader;

public class MainMenu extends Scene {

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
                        YmlReader.getString(new String[]{"welcome_message", Language.ENG.getValue()}) +
                            incomingMessage.getSenderData().getSenderName() +
                            YmlReader.getString(new String[]{"menu", Language.ENG.getValue()})
                    );

                    return activateNextScene(currentState, new Endpoints());
                }
                case "2" -> {
                    currentState.getData().put("lang", Language.RU);
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"welcome_message", Language.RU.getValue()}) +
                            incomingMessage.getSenderData().getSenderName() +
                            YmlReader.getString(new String[]{"menu", Language.RU.getValue()})
                    );

                    return activateNextScene(currentState, new Endpoints());
                }
                case "3" -> {
                    currentState.getData().put("lang", Language.HE);
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"welcome_message", Language.HE.getValue()}) +
                            incomingMessage.getSenderData().getSenderName() +
                            YmlReader.getString(new String[]{"menu", Language.HE.getValue()})
                    );

                    return activateNextScene(currentState, new Endpoints());
                }
                case "4" -> {
                    currentState.getData().put("lang", Language.ES);
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"welcome_message", Language.ES.getValue()}) +
                            incomingMessage.getSenderData().getSenderName() +
                            YmlReader.getString(new String[]{"menu", Language.ES.getValue()})
                    );

                    return activateNextScene(currentState, new Endpoints());
                }
                case "5" -> {
                    currentState.getData().put("lang", Language.AR);
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"welcome_message", Language.AR.getValue()}) +
                            incomingMessage.getSenderData().getSenderName() +
                            YmlReader.getString(new String[]{"menu", Language.AR.getValue()})
                    );

                    return activateNextScene(currentState, new Endpoints());
                }
                default -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"specify_language"}));

                    return currentState;
                }
            }
        }

        return currentState;
    }
}
