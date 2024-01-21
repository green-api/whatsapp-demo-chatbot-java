package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.demoChatbot.util.YmlReader;

public class Start extends Scene {

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {

        answerWithText(incomingMessage, YmlReader.getString(new String[]{"select_language"}));

        return activateNextScene(currentState, new MainMenu());
    }
}
