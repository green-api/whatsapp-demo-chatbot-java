package com.greenapi.demoChatbot.scenes;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Endpoints extends ExtendedScene {

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
                    stringsYml.getText("send_text_message", lang) +
                        stringsYml.getText("links", lang, "send_text_documentation"));

                return currentState;
            }
            case "2" -> {
                answerWithUrlFile(incomingMessage,
                    stringsYml.getText("send_file_message", lang) +
                        stringsYml.getText("links", lang, "send_file_documentation"),
                    "https://images.rawpixel.com/image_png_1100/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIzLTExL3Jhd3BpeGVsb2ZmaWNlMTlfcGhvdG9fb2ZfY29yZ2lzX2luX2NocmlzdG1hc19zd2VhdGVyX2luX2FfcGFydF80YWM1ODk3Zi1mZDMwLTRhYTItYWM5NS05YjY3Yjg1MTFjZmUucG5n.png",
                    "corgi.png");

                return currentState;
            }
            case "3" -> {
                answerWithUrlFile(incomingMessage,
                    stringsYml.getText("send_image_message", lang) +
                        stringsYml.getText("links", lang, "send_file_documentation"),
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
                    stringsYml.getText("send_contact_message", lang) +
                        stringsYml.getText("links", lang, "send_contact_documentation"));

                answerWithContact(incomingMessage, Contact.builder()
                    .firstName(incomingMessage.getSenderData().getSenderName())
                    .phoneContact(Long.valueOf(incomingMessage.getSenderData().getChatId().replaceAll("@c\\.us", "")))
                    .build());

                return currentState;
            }
            case "7" -> {
                answerWithText(incomingMessage,
                    stringsYml.getText("send_location_message", lang) +
                        stringsYml.getText("links", lang, "send_location_documentation"));

                answerWithLocation(incomingMessage, "", "", 35.888171, 14.440230);

                return currentState;
            }
            case "8" -> {
                answerWithText(incomingMessage, stringsYml.getText("send_poll_message", lang));

                var options = new ArrayList<Option>();
                options.add(new Option(stringsYml.getText("poll_option", lang, "o1")));
                options.add(new Option(stringsYml.getText("poll_option", lang, "o2")));
                options.add(new Option(stringsYml.getText("poll_option", lang, "o3")));

                answerWithPoll(incomingMessage, "Выберите вариант ответа ✔️\nНравится Вам работа демо чатбота?",
                    options, false);

                return currentState;
            }
            case "9" -> {
                answerWithText(incomingMessage, stringsYml.getText("send_avatar_message", lang, "avatar"));

                var avatar = greenApi.service.getAvatar(incomingMessage.getSenderData().getChatId());

                if (avatar.getBody().getUrlAvatar() != null) {
                    answerWithUrlFile(incomingMessage,
                        stringsYml.getText("send_avatar_message", lang, "avatar_exist"), avatar.getBody().getUrlAvatar(), "avatar");
                } else {
                    answerWithText(incomingMessage,
                        stringsYml.getText("send_avatar_message", lang, "avatar_not_exist"));
                }

                return currentState;
            }
            case "10" -> {
                answerWithText(incomingMessage, stringsYml.getText("send_link_message", lang, "with_preview"));

                greenApi.sending.sendMessage(OutgoingMessage.builder()
                    .chatId(incomingMessage.getSenderData().getChatId())
                    .message(stringsYml.getText("send_link_message", lang, "without_preview"))
                    .quotedMessageId(incomingMessage.getIdMessage())
                    .linkPreview(false)
                    .build());

                return currentState;
            }
            case "11" -> {
                var group = greenApi.groups.createGroup(CreateGroupReq.builder()
                    .chatIds(List.of(incomingMessage.getSenderData().getChatId()))
                    .groupName(stringsYml.getText("group_name", lang))
                    .build());

                greenApi.groups.setGroupPicture(ChangeGroupPictureReq.builder()
                    .groupId(group.getBody().getChatId())
                    .file(new File("src/main/resources/img.png"))
                    .build());

                greenApi.sending.sendMessage(OutgoingMessage.builder()
                    .chatId(group.getBody().getChatId())
                    .message(stringsYml.getText("create_group_message", lang))
                    .build());
            }
            case "stop", "стоп", "Stop", "Стоп" -> {
                answerWithText(incomingMessage,
                    stringsYml.getText("stop_message", lang) +
                        incomingMessage.getSenderData().getSenderName());

                return activateStartScene(currentState);
            }
            case "menu", "меню", "Menu", "Меню" -> {
                answerWithText(incomingMessage, stringsYml.getText("menu", lang));

                return currentState;
            }
            default -> {
                answerWithText(incomingMessage, stringsYml.getText("not_recognized_message", lang));

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
            answerWithText(pollUpdate, stringsYml.getText("poll_response", lang, "if_yes"));
        }
        if (isNo) {
            answerWithText(pollUpdate, stringsYml.getText("poll_response", lang, "if_no"));
        }
        if (isNothing) {
            answerWithText(pollUpdate, stringsYml.getText("poll_response", lang, "if_nothing"));
        }
    }
}
