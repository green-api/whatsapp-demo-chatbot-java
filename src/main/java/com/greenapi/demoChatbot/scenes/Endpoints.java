package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.Contact;
import com.greenapi.client.pkg.models.Option;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.client.pkg.models.notifications.PollUpdateMessageWebhook;
import com.greenapi.client.pkg.models.notifications.messages.messageData.PollUpdateMessageData;
import com.greenapi.client.pkg.models.request.ChangeGroupPictureReq;
import com.greenapi.client.pkg.models.request.CreateGroupReq;
import com.greenapi.client.pkg.models.request.OutgoingMessage;
import com.greenapi.demoChatbot.util.Language;
import com.greenapi.demoChatbot.util.SessionManager;
import com.greenapi.demoChatbot.util.YmlReader;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class Endpoints extends Scene {

    @Autowired
    private Environment environment;

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        if (SessionManager.isSessionExpired(currentState)) {
            return activateStartScene(currentState);
        }

        var lang = (Language) currentState.getData().get("lang");
        var text = getText(incomingMessage);

        if (incomingMessage instanceof PollUpdateMessageWebhook pollUpdate) {
            processPollUpdate(pollUpdate, lang);
        }

        if (text.isEmpty()) {
            return currentState;
        }

        switch (text.get()) {
            case "1" -> {
                answerWithText(incomingMessage,
                    YmlReader.getString(new String[]{"send_text_message", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_text_documentation"}));

                return currentState;
            }
            case "2" -> {
                answerWithUrlFile(incomingMessage,
                    YmlReader.getString(new String[]{"send_file_message", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
                    environment.getProperty("link_1"),
                    "corgi.pdf");

                return currentState;
            }
            case "3" -> {
                answerWithUrlFile(incomingMessage,
                    YmlReader.getString(new String[]{"send_file_message", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
                    environment.getProperty("link_2"),
                    "corgi.jpg");

                return currentState;
            }
            case "4" -> {
                answerWithText(incomingMessage, YmlReader.getString(new String[]{"send_audio_message", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}));

                answerWithUrlFile(incomingMessage, "", environment.getProperty("link_3"), "audio.mp3");

                return currentState;
            }
            case "5" -> {
                answerWithUrlFile(incomingMessage,
                    YmlReader.getString(new String[]{"send_video_message", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
                    environment.getProperty("link_4"),
                    "video.mp4");

                return currentState;
            }
            case "6" -> {
                answerWithText(incomingMessage,
                    YmlReader.getString(new String[]{"send_contact_message", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_contact_documentation"}));

                answerWithContact(incomingMessage, Contact.builder()
                    .firstName(incomingMessage.getSenderData().getSenderName())
                    .phoneContact(Long.valueOf(incomingMessage.getSenderData().getChatId().replaceAll("@c\\.us", "")))
                    .build());

                return currentState;
            }
            case "7" -> {
                answerWithText(incomingMessage,
                    YmlReader.getString(new String[]{"send_location_message", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_location_documentation"}));

                answerWithLocation(incomingMessage, "", "", 35.888171, 14.440230);

                return currentState;
            }
            case "8" -> {
                answerWithText(incomingMessage,
                    YmlReader.getString(new String[]{"send_poll_message", lang.getValue()}) + "\n" +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_poll_documentation"}));

                var options = new ArrayList<Option>();
                options.add(new Option(YmlReader.getString(new String[]{"poll_option_1", lang.getValue()})));
                options.add(new Option(YmlReader.getString(new String[]{"poll_option_2", lang.getValue()})));
                options.add(new Option(YmlReader.getString(new String[]{"poll_option_3", lang.getValue()})));

                answerWithPoll(incomingMessage, YmlReader.getString(new String[]{"poll_question", lang.getValue()}),
                    options, false);

                return currentState;
            }
            case "9" -> {
                answerWithText(incomingMessage,
                    YmlReader.getString(new String[]{"get_avatar_message", lang.getValue(), "avatar"}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "get_avatar_documentation"}));

                var avatar = greenApi.service.getAvatar(incomingMessage.getSenderData().getChatId());

                if (avatar.getBody().getUrlAvatar() != null) {
                    answerWithUrlFile(incomingMessage,
                        YmlReader.getString(new String[]{"avatar_found", lang.getValue()}),
                        avatar.getBody().getUrlAvatar(), "avatar");
                } else {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"avatar_not_found", lang.getValue()}));
                }

                return currentState;
            }
            case "10" -> {
                answerWithText(incomingMessage,
                    YmlReader.getString(new String[]{"send_link_message_preview", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_link_documentation"}));

                greenApi.sending.sendMessage(OutgoingMessage.builder()
                    .chatId(incomingMessage.getSenderData().getChatId())
                    .message(YmlReader.getString(new String[]{"send_link_message_no_preview", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_link_documentation"}))
                    .quotedMessageId(incomingMessage.getIdMessage())
                    .linkPreview(false)
                    .build());

                return currentState;
            }
            case "11" -> {
                var group = greenApi.groups.createGroup(CreateGroupReq.builder()
                    .chatIds(List.of(incomingMessage.getSenderData().getChatId()))
                    .groupName(YmlReader.getString(new String[]{"group_name", lang.getValue()}))
                    .build());

                var setGroupPicture = greenApi.groups.setGroupPicture(ChangeGroupPictureReq.builder()
                    .groupId(group.getBody().getChatId())
                    .file(Paths.get("src/main/resources/assets/Group_avatar.jpg").toFile())
                    .build());

                if (setGroupPicture.getStatusCode().is2xxSuccessful()) {
                    if (setGroupPicture.getBody().getSetGroupPicture()) {
                        greenApi.sending.sendMessage(OutgoingMessage.builder()
                            .chatId(group.getBody().getChatId())
                            .message(YmlReader.getString(new String[]{"send_group_message", lang.getValue()}) +
                                YmlReader.getString(new String[]{"links", lang.getValue(), "create_group_documentation"}))
                            .build());
                    } else {
                        greenApi.sending.sendMessage(OutgoingMessage.builder()
                            .chatId(group.getBody().getChatId())
                            .message(YmlReader.getString(new String[]{"send_group_message_set_picture_false", lang.getValue()}) +
                                YmlReader.getString(new String[]{"links", lang.getValue(), "create_group_documentation"}))
                            .build());
                    }
                }
            }
            case "12" -> {
                answerWithText(incomingMessage,
                    YmlReader.getString(new String[]{"send_quoted_message", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_quoted_message_documentation"}));

                return currentState;
            }
            case "stop", "стоп", "Stop", "Стоп" -> {
                answerWithText(incomingMessage,
                    YmlReader.getString(new String[]{"stop_message", lang.getValue()}) +
                        incomingMessage.getSenderData().getSenderName());

                return activateStartScene(currentState);
            }
            case "menu", "меню", "Menu", "Меню" -> {
                answerWithText(incomingMessage, YmlReader.getString(new String[]{"menu", lang.getValue()}));

                return currentState;
            }
            default -> {
                answerWithText(incomingMessage, YmlReader.getString(new String[]{"not_recognized_message", lang.getValue()}));

                return currentState;
            }
        }
        return currentState;
    }

    private void processPollUpdate(PollUpdateMessageWebhook pollUpdate, Language lang) {
        var votes = pollUpdate.getMessageData().getPollMessageData().getVotes();
        var isYes = votes.get(0).getOptionVoters().contains(new PollUpdateMessageData.Voter(pollUpdate.getSenderData().getSender()));
        var isNo = votes.get(1).getOptionVoters().contains(new PollUpdateMessageData.Voter(pollUpdate.getSenderData().getSender()));
        var isNothing = votes.get(2).getOptionVoters().contains(new PollUpdateMessageData.Voter(pollUpdate.getSenderData().getSender()));
        var messageText = "";

        if (isYes) {
            messageText = YmlReader.getString(new String[]{"poll_answer_1", lang.getValue()});
        }
        if (isNo) {
            messageText = YmlReader.getString(new String[]{"poll_answer_2", lang.getValue()});
        }
        if (isNothing) {
            messageText = YmlReader.getString(new String[]{"poll_answer_3", lang.getValue()});
        }

        greenApi.sending.sendMessage(
            OutgoingMessage.builder()
                .chatId(pollUpdate.getSenderData().getChatId())
                .message(messageText)
                .build());
    }
}
