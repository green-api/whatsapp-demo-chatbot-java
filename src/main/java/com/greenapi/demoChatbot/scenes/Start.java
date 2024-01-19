package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;

public class Start extends ExtendedScene {

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {

        answerWithText(incomingMessage, stringsYml.getText("select_language"));

        return activateNextScene(currentState, new MainMenu());
    }
}
