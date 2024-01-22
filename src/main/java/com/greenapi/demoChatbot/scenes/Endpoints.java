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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Endpoints extends Scene {

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
                    "https://images.rawpixel.com/image_png_1100/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIzLTExL3Jhd3BpeGVsb2ZmaWNlMTlfcGhvdG9fb2ZfY29yZ2lzX2luX2NocmlzdG1hc19zd2VhdGVyX2luX2FfcGFydF80YWM1ODk3Zi1mZDMwLTRhYTItYWM5NS05YjY3Yjg1MTFjZmUucG5n.png",
                    "corgi.png");

                return currentState;
            }
            case "3" -> {
                answerWithUrlFile(incomingMessage,
                    YmlReader.getString(new String[]{"send_image_message", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
                    "https://images.rawpixel.com/image_png_1100/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIzLTExL3Jhd3BpeGVsb2ZmaWNlMTlfcGhvdG9fb2ZfY29yZ2lzX2luX2NocmlzdG1hc19zd2VhdGVyX2luX2FfcGFydF80YWM1ODk3Zi1mZDMwLTRhYTItYWM5NS05YjY3Yjg1MTFjZmUucG5n.png",
                    "corgi.jpg");

                return currentState;
            }
            case "4" -> {

            }
            case "5" -> {

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
                answerWithText(incomingMessage, YmlReader.getString(new String[]{"send_poll_message", lang.getValue()}));

                var options = new ArrayList<Option>();
                options.add(new Option(YmlReader.getString(new String[]{"poll_option", lang.getValue(), "o1"})));
                options.add(new Option(YmlReader.getString(new String[]{"poll_option", lang.getValue(), "o2"})));
                options.add(new Option(YmlReader.getString(new String[]{"poll_option", lang.getValue(), "o3"})));

                answerWithPoll(incomingMessage, YmlReader.getString(new String[]{"poll_name", lang.getValue()}),
                    options, false);

                return currentState;
            }
            case "9" -> {
                answerWithText(incomingMessage, YmlReader.getString(new String[]{"send_avatar_message", lang.getValue(), "avatar"}));

                var avatar = greenApi.service.getAvatar(incomingMessage.getSenderData().getChatId());

                if (avatar.getBody().getUrlAvatar() != null) {
                    answerWithUrlFile(incomingMessage,
                        YmlReader.getString(new String[]{"send_avatar_message", lang.getValue(), "avatar_exist"}),
                        avatar.getBody().getUrlAvatar(), "avatar");
                } else {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"send_avatar_message", lang.getValue(), "avatar_not_exist"}));
                }

                return currentState;
            }
            case "10" -> {
                answerWithText(incomingMessage, YmlReader.getString(new String[]{"send_link_message", lang.getValue(), "with_preview"}));

                greenApi.sending.sendMessage(OutgoingMessage.builder()
                    .chatId(incomingMessage.getSenderData().getChatId())
                    .message(YmlReader.getString(new String[]{"send_link_message", lang.getValue(), "without_preview"}))
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

                greenApi.groups.setGroupPicture(ChangeGroupPictureReq.builder()
                    .groupId(group.getBody().getChatId())
                    .file(new File("src/main/resources/img.png"))
                    .build());

                greenApi.sending.sendMessage(OutgoingMessage.builder()
                    .chatId(group.getBody().getChatId())
                    .message(YmlReader.getString(new String[]{"create_group_message", lang.getValue()}))
                    .build());
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

        if (isYes) {
            answerWithText(pollUpdate, YmlReader.getString(new String[]{"poll_response", lang.getValue(), "if_yes"}));
        }
        if (isNo) {
            answerWithText(pollUpdate, YmlReader.getString(new String[]{"poll_response", lang.getValue(), "if_no"}));
        }
        if (isNothing) {
            answerWithText(pollUpdate, YmlReader.getString(new String[]{"poll_response", lang.getValue(), "if_nothing"}));
        }
    }
}
