package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.Contact;
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

                    var setGroupPicture = greenApi.groups.setGroupPicture(ChangeGroupPictureReq.builder()
                        .groupId(Objects.requireNonNull(group.getBody()).getChatId())
                        .file(Paths.get("src/main/resources/assets/group_avatar.jpg").toFile())
                        .build());

                    if (setGroupPicture.getStatusCode().is2xxSuccessful()) {
                        if (Objects.requireNonNull(setGroupPicture.getBody()).getSetGroupPicture()) {
                            try {
                                greenApi.sending.sendMessage(OutgoingMessage.builder()
                                    .chatId(group.getBody().getChatId())
                                    .message(YmlReader.getString(new String[]{"send_group_message", lang.getValue()}) +
                                        YmlReader.getString(new String[]{"links", lang.getValue(), "create_group_documentation"}))
                                    .build());
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        } else {
                            try {
                                greenApi.sending.sendMessage(OutgoingMessage.builder()
                                    .chatId(group.getBody().getChatId())
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
                case "0" -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"menu", lang.getValue()}), false);
                    return activateNextScene(currentState, endpoints);
                }
                case "menu", "меню", "Menu", "Меню" -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"add_to_contact", lang.getValue()}), false);

                    answerWithContact(incomingMessage, Contact.builder()
                        .firstName(YmlReader.getString(new String[]{"bot_name", lang.getValue()}))
                        .phoneContact(Long.valueOf(incomingMessage.getInstanceData().getWid().replaceAll("@c\\.us", "")))
                        .build(), false);

                    return currentState;
                }
                default -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"not_recognized_message", lang.getValue()}));
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
