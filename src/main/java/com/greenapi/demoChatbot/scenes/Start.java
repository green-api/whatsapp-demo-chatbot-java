package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.demoChatbot.util.LogBuilder;
import com.greenapi.demoChatbot.util.SessionManager;
import com.greenapi.demoChatbot.util.YmlReader;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class Start extends Scene {
    @Autowired
    private MainMenu mainMenuScene;

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        log.info(LogBuilder.build(incomingMessage, "IncomingMessageHandler in StartScene handles"));
        try {
            SessionManager.isSessionExpired(currentState);
            log.info(LogBuilder.build(incomingMessage, "Starting MainMenuScene..."));
            answerWithText(incomingMessage, YmlReader.getString(new String[]{"select_language"}), false);

            return activateNextScene(currentState, mainMenuScene);
        } catch (Exception e) {
            log.error(e.getStackTrace());
            answerWithText(incomingMessage, YmlReader.getString(new String[]{"sorry_message"}));

            return currentState;
        }
    }
}
