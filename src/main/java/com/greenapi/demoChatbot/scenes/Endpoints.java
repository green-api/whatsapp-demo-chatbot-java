package com.greenapi.demoChatbot.scenes;

import com.greenapi.chatbot.pkg.Scene;
import com.greenapi.chatbot.pkg.state.State;
import com.greenapi.client.pkg.models.Contact;
import com.greenapi.client.pkg.models.Option;
import com.greenapi.client.pkg.models.notifications.MessageWebhook;
import com.greenapi.client.pkg.models.notifications.PollUpdateMessageWebhook;
import com.greenapi.client.pkg.models.notifications.messages.messageData.PollUpdateMessageData;
import com.greenapi.client.pkg.models.request.OutgoingMessage;
import com.greenapi.demoChatbot.util.Language;
import com.greenapi.demoChatbot.util.SessionManager;
import com.greenapi.demoChatbot.util.YmlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.ArrayList;

@Component
public class Endpoints extends Scene {

    @Autowired
    private Environment environment;
    @Autowired
    private CreateGroup createGroupScene;

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        try {
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
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_text_documentation"}),
                        false);
                    ;
                }
                case "2" -> {
                    answerWithUrlFile(incomingMessage,
                        YmlReader.getString(new String[]{"send_file_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
                        environment.getProperty("link_1"),
                        "corgi.pdf",
                        false);
                    ;
                }
                case "3" -> {
                    answerWithUrlFile(incomingMessage,
                        YmlReader.getString(new String[]{"send_file_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
                        environment.getProperty("link_2"),
                        "corgi.jpg",
                        false);
                    ;
                }
                case "4" -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"send_audio_message", lang.getValue()}) +
                        YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}), false);

                    answerWithUrlFile(incomingMessage, "", environment.getProperty("link_3"), "audio.mp3");
                    ;
                }
                case "5" -> {
                    answerWithUrlFile(incomingMessage,
                        YmlReader.getString(new String[]{"send_video_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
                        environment.getProperty("link_4"),
                        "video.mp4", false);
                    ;
                }
                case "6" -> {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"send_contact_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_contact_documentation"}),
                        false);

                    answerWithContact(incomingMessage, Contact.builder()
                        .firstName(incomingMessage.getSenderData().getSenderName())
                        .phoneContact(Long.valueOf(incomingMessage.getSenderData().getSender().replaceAll("@c\\.us", "")))
                        .build(), false);
                    ;
                }
                case "7" -> {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"send_location_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_location_documentation"}),
                        false);

                    answerWithLocation(incomingMessage, "", "", 35.888171, 14.440230,
                        false);
                    ;
                }
                case "8" -> {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"send_poll_message", lang.getValue()}) + "\n" +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_poll_documentation"}),
                        false);

                    var options = new ArrayList<Option>();
                    options.add(new Option(YmlReader.getString(new String[]{"poll_option_1", lang.getValue()})));
                    options.add(new Option(YmlReader.getString(new String[]{"poll_option_2", lang.getValue()})));
                    options.add(new Option(YmlReader.getString(new String[]{"poll_option_3", lang.getValue()})));

                    answerWithPoll(incomingMessage, YmlReader.getString(new String[]{"poll_question", lang.getValue()}),
                        options, false);
                    ;
                }
                case "9" -> {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"get_avatar_message", lang.getValue(), "avatar"}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "get_avatar_documentation"}),
                        false);

                    var avatar = greenApi.service.getAvatar(incomingMessage.getSenderData().getSender());

                    if (avatar.getBody().getUrlAvatar() != null) {
                        answerWithUrlFile(incomingMessage,
                            YmlReader.getString(new String[]{"avatar_found", lang.getValue()}),
                            avatar.getBody().getUrlAvatar(), "avatar", false);
                    } else {
                        answerWithText(incomingMessage,
                            YmlReader.getString(new String[]{"avatar_not_found", lang.getValue()}), false);
                    }
                    ;
                }
                case "10" -> {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"send_link_message_preview", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_link_documentation"}),
                        false);

                    greenApi.sending.sendMessage(OutgoingMessage.builder()
                        .chatId(incomingMessage.getSenderData().getChatId())
                        .message(YmlReader.getString(new String[]{"send_link_message_no_preview", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_link_documentation"}))
                        .linkPreview(false)
                        .build());
                    ;
                }
                case "11" -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"add_to_contact", lang.getValue()}), false);

                    answerWithContact(incomingMessage, Contact.builder()
                        .firstName(YmlReader.getString(new String[]{"bot_name", lang.getValue()}))
                        .phoneContact(Long.valueOf(incomingMessage.getInstanceData().getWid().replaceAll("@c\\.us", "")))
                        .build(), false);
                    ;

                    return activateNextScene(currentState, createGroupScene);
                }
                case "12" -> {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"send_quoted_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_quoted_message_documentation"}));
                }
                case "13" -> {
                    answerWithUploadFile(incomingMessage, Paths.get("src/main/resources/assets/about_java.jpg").toFile(),
                        new StringBuilder()
                            .append(YmlReader.getString(new String[]{"about_java_chatbot", lang.getValue()}))
                            .append(YmlReader.getString(new String[]{"link_to_docs", lang.getValue()}))
                            .append(YmlReader.getString(new String[]{"links", lang.getValue(), "chatbot_documentation"}))
                            .append(YmlReader.getString(new String[]{"link_to_source_code", lang.getValue()}))
                            .append(YmlReader.getString(new String[]{"links", lang.getValue(), "chatbot_source_code"}))
                            .append(YmlReader.getString(new String[]{"link_to_green_api", lang.getValue()}))
                            .append(YmlReader.getString(new String[]{"links", lang.getValue(), "greenapi_website"}))
                            .append(YmlReader.getString(new String[]{"link_to_console", lang.getValue()}))
                            .append(YmlReader.getString(new String[]{"links", lang.getValue(), "greenapi_console"}))
                            .append(YmlReader.getString(new String[]{"link_to_youtube", lang.getValue()}))
                            .append(YmlReader.getString(new String[]{"links", lang.getValue(), "youtube_channel"}))
                            .toString(),
                        false);
                }
                case "stop", "стоп", "Stop", "Стоп" -> {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"stop_message", lang.getValue()}) +
                            incomingMessage.getSenderData().getSenderName());

                    return activateStartScene(currentState);
                }
                case "menu", "меню", "Menu", "Меню" -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"menu", lang.getValue()}));
                }
                default -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"not_recognized_message", lang.getValue()}));
                }
            }

        } catch (Exception e) {
            answerWithText(incomingMessage, YmlReader.getString(new String[]{"sorry_message"}));
        }

        return currentState;
    }

    private void processPollUpdate(PollUpdateMessageWebhook pollUpdate, Language lang) {
        try {
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
            answerWithText(pollUpdate, messageText, false);
        } catch (Exception e) {
            answerWithText(pollUpdate, YmlReader.getString(new String[]{"sorry_message"}));
        }
    }
}
