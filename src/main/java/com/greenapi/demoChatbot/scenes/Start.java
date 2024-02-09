package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.demoChatbot.util.YmlReader;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Start extends Scene {
    @Autowired
    private MainMenu mainMenuScene;

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {

        answerWithText(incomingMessage, YmlReader.getString(new String[]{"select_language"}), false);

        return activateNextScene(currentState, mainMenuScene);
    }
}
