package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.client.pkg.models.request.ChangeGroupPictureReq;
import com.greenapi.client.pkg.models.request.CreateGroupReq;
import com.greenapi.demoChatbot.util.Language;
import com.greenapi.demoChatbot.util.YmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.Collections;

@Component
public class CreateGroup extends Scene {

    @Autowired
    private MainMenu mainMenu;

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
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
                        .groupId(group.getBody().getChatId())
                        .file(Paths.get("assets/group_avatar.jpg").toFile())
                        .build());

                    if (setGroupPicture.getStatusCode().is2xxSuccessful()) {
                        if (setGroupPicture.getBody().getSetGroupPicture()) {
                            answerWithText(incomingMessage, YmlReader.getString(new String[]{"send_group_message", lang.getValue()}) +
                                YmlReader.getString(new String[]{"links", lang.getValue(), "create_group_documentation"}), false);
                        } else {
                            answerWithText(incomingMessage, YmlReader.getString(new String[]{"send_group_message_set_picture_false", lang.getValue()}) +
                                YmlReader.getString(new String[]{"links", lang.getValue(), "create_group_documentation"}), false);
                        }
                        answerWithText(incomingMessage, YmlReader.getString(new String[]{"group_created_message", lang.getValue()}) +
                            group.getBody().getGroupInviteLink(), false);
                    }
                }
                case "0" -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"menu", lang.getValue()}), false);
                    return activateNextScene(currentState, mainMenu);
                }
            }
        } catch (Exception e) {
            answerWithText(incomingMessage, YmlReader.getString(new String[]{"sorry_message"}), false);
        }

        return currentState;
    }
}
