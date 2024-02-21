package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.demoChatbot.util.Language;
import com.greenapi.demoChatbot.util.LogBuilder;
import com.greenapi.demoChatbot.util.SessionManager;
import com.greenapi.demoChatbot.util.YmlReader;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;

@Component
@AllArgsConstructor
@Log4j2
public class MainMenu extends Scene {

    private Endpoints endpointsScene;

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        log.info(LogBuilder.build(incomingMessage, "IncomingMessageHandler in MainMenuScene handles"));
        try {
            if (SessionManager.isSessionExpired(currentState)) {
                answerWithText(incomingMessage, YmlReader.getString(new String[]{"select_language"}), false);
                log.info(LogBuilder.build(incomingMessage, "Session expired = true, Starting MainMenuScene..."));
                return currentState;
            }

            var messageText = getText(incomingMessage);
            if (messageText.isPresent()) {
                switch (messageText.get()) {
                    case "1" -> sendMainMenu(incomingMessage, currentState, Language.ENG);
                    case "2" -> sendMainMenu(incomingMessage, currentState, Language.KZ);
                    case "3" -> sendMainMenu(incomingMessage, currentState, Language.RU);
                    case "4" -> sendMainMenu(incomingMessage, currentState, Language.ES);
                    case "5" -> sendMainMenu(incomingMessage, currentState, Language.HE);
                    case "6" -> sendMainMenu(incomingMessage, currentState, Language.AR);
                    default -> {
                        answerWithText(incomingMessage, YmlReader.getString(new String[]{"specify_language"}), false);
                        return currentState;
                    }
                }
            }
            return activateNextScene(currentState, endpointsScene);

        } catch (Exception e) {
            log.error(e.getStackTrace());
            answerWithText(incomingMessage, YmlReader.getString(new String[]{"sorry_message"}),false);
            return currentState;
        }
    }

    private void sendMainMenu(MessageWebhook incomingMessage, State currentState, Language language) {
        currentState.getData().put("lang", language);

        File welcomeFile;
        if (language == Language.RU) {
            welcomeFile = Paths.get("src/main/resources/assets/welcome_ru.png").toFile();
        } else {
            welcomeFile = Paths.get("src/main/resources/assets/welcome_en.png").toFile();
        }

        answerWithUploadFile(incomingMessage, welcomeFile,
            YmlReader.getString(new String[]{"welcome_message", language.getValue()}) +
                "*" + incomingMessage.getSenderData().getSenderName() + "*!" + "\n" +
                YmlReader.getString(new String[]{"menu", language.getValue()}), false
        );
    }
}
