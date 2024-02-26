package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.MapState;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.client.pkg.models.request.ChangeGroupPictureReq;
import com.greenapi.client.pkg.models.request.CreateGroupReq;
import com.greenapi.client.pkg.models.request.OutgoingMessage;
import com.greenapi.demoChatbot.util.Language;
import com.greenapi.demoChatbot.util.LogBuilder;
import com.greenapi.demoChatbot.util.SessionManager;
import com.greenapi.demoChatbot.util.YmlReader;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;

@Component
@Log4j2
public class CreateGroup extends Scene {
    private final Endpoints endpoints;
    private final MainMenu mainMenu;

    @Autowired
    public CreateGroup(Environment environment) {
        this.endpoints = new Endpoints(environment, this);
        this.mainMenu = new MainMenu(endpoints);
    }

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        log.info(LogBuilder.build(incomingMessage, "IncomingMessageHandler in CreateGroupScene handles"));
        if (SessionManager.isSessionExpired(currentState)) {
            answerWithText(incomingMessage, YmlReader.getString(new String[]{"select_language"}), false);
            log.info(LogBuilder.build(incomingMessage, "Session expired = true, Starting MainMenuScene..."));
            return activateNextScene(currentState, mainMenu);
        }

        var lang = (Language) currentState.getData().get("lang");
        var text = getText(incomingMessage);

        if (text.isEmpty()) {
            return currentState;
        }
        try {
            switch (text.get()) {
                case "1" -> {
                    var group = greenApi.groups.createGroup(CreateGroupReq.builder()
                        .chatIds(Collections.singletonList(incomingMessage.getSenderData().getSender()))
                        .groupName(YmlReader.getString(new String[]{"group_name", lang.getValue()}))
                        .build());

                    var groupId = Objects.requireNonNull(group.getBody()).getChatId();

                    stateManager.create(groupId);
                    var groupState = new MapState(currentState.getData());
                    groupState.setScene(endpoints);
                    stateManager.updateStateData(groupId, groupState.getData());

                    var setGroupPicture = greenApi.groups.setGroupPicture(ChangeGroupPictureReq.builder()
                        .groupId(groupId)
                        .file(Paths.get("src/main/resources/assets/group_avatar.jpg").toFile())
                        .build());

                    if (setGroupPicture.getStatusCode().is2xxSuccessful()) {
                        if (Objects.requireNonNull(setGroupPicture.getBody()).getSetGroupPicture()) {
                            try {
                                greenApi.sending.sendMessage(OutgoingMessage.builder()
                                    .chatId(groupId)
                                    .message(YmlReader.getString(new String[]{"send_group_message", lang.getValue()}) +
                                        YmlReader.getString(new String[]{"links", lang.getValue(), "create_group_documentation"}))
                                    .build());
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        } else {
                            try {
                                greenApi.sending.sendMessage(OutgoingMessage.builder()
                                    .chatId(groupId)
                                    .message(YmlReader.getString(new String[]{"send_group_message_set_picture_false", lang.getValue()}) +
                                        YmlReader.getString(new String[]{"links", lang.getValue(), "create_group_documentation"}))
                                    .build());
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        }
                        answerWithText(incomingMessage, YmlReader.getString(new String[]{"group_created_message", lang.getValue()}) +
                            group.getBody().getGroupInviteLink(), false);
                    }
                    return activateNextScene(currentState, endpoints);
                }
                case "menu", "меню", "Menu", "Меню", "0" -> {
                    File welcomeFile;
                    if (lang == Language.RU) {
                        welcomeFile = Paths.get("src/main/resources/assets/welcome_ru.png").toFile();
                    } else {
                        welcomeFile = Paths.get("src/main/resources/assets/welcome_en.png").toFile();
                    }
                    answerWithUploadFile(incomingMessage, welcomeFile, YmlReader.getString(new String[]{"menu", lang.getValue()}), false);

                    return activateNextScene(currentState, endpoints);
                }
                default -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"not_recognized_message", lang.getValue()}), false);
                    return currentState;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            answerWithText(incomingMessage, YmlReader.getString(new String[]{"sorry_message"}), false);
            return currentState;
        }
    }
}
