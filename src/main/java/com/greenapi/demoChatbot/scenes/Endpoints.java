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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

@Component
@Log4j2
public class Endpoints extends Scene {

    private final Environment environment;
    private final CreateGroup createGroupScene;
    private final MainMenu mainMenu;

    @Autowired
    public Endpoints(Environment environment, CreateGroup createGroup) {
        this.environment = environment;
        this.createGroupScene = createGroup;
        this.mainMenu = new MainMenu(this);
    }

    @Override
    public State processIncomingMessage(MessageWebhook incomingMessage, State currentState) {
        try {
            if (SessionManager.isSessionExpired(currentState)) {
                answerWithText(incomingMessage, YmlReader.getString(new String[]{"select_language"}), false);
                return activateNextScene(currentState, mainMenu);
            }

            var lang = (Language) currentState.getData().get("lang");
            var text = getText(incomingMessage);

            if (incomingMessage instanceof PollUpdateMessageWebhook pollUpdate) {
                processPollUpdate(pollUpdate, lang);
                return currentState;
            }

            if (text.isEmpty()) {
                return currentState;
            }

            Boolean linkPreview = true;
            String linkPreviewEnv = YmlReader.getString(new String[]{"link_preview"});
            if (linkPreviewEnv.toLowerCase().equals("false")) {
                linkPreview = false;
            }


            switch (text.get()) {
                case "1" -> {
                    greenApi.sending.sendMessage(OutgoingMessage.builder()
                        .chatId(incomingMessage.getSenderData().getChatId())
                        .message(YmlReader.getString(new String[]{"send_text_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_text_documentation"}))
                        .linkPreview(linkPreview)
                        .build());
                    
                    return currentState;
                }
                case "2" -> {
                    answerWithUrlFile(incomingMessage,
                        YmlReader.getString(new String[]{"send_file_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
                        "https://storage.yandexcloud.net/sw-prod-03-test/ChatBot/corgi.pdf",
                        "corgi.pdf",
                        false);
                    return currentState;
                }
                case "3" -> {
                    answerWithUrlFile(incomingMessage,
                        YmlReader.getString(new String[]{"send_image_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
                        "https://storage.yandexcloud.net/sw-prod-03-test/ChatBot/corgi.jpg",
                        "corgi.jpg",
                        false);
                    return currentState;
                }
                case "4" -> {
                    greenApi.sending.sendMessage(OutgoingMessage.builder()
                        .chatId(incomingMessage.getSenderData().getChatId())
                        .message(YmlReader.getString(new String[]{"send_audio_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}))
                        .linkPreview(linkPreview)
                        .build());

                    answerWithUrlFile(incomingMessage,               
                        (lang == Language.RU) 
                        ? "https://storage.yandexcloud.net/sw-prod-03-test/ChatBot/Audio_bot.mp3" 
                        : "https://storage.yandexcloud.net/sw-prod-03-test/ChatBot/Audio_bot_eng.mp3", 
                        "audio.mp3", 
                        false);
                    return currentState;
                }
                case "5" -> {
                    answerWithUrlFile(incomingMessage,
                        YmlReader.getString(new String[]{"send_video_message", lang.getValue()}) + YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
                        (lang == Language.RU) 
                        ? "https://storage.yandexcloud.net/sw-prod-03-test/ChatBot/Video_bot_ru.mp4" 
                        : "https://storage.yandexcloud.net/sw-prod-03-test/ChatBot/Video_bot_eng.mp4", 
                        "video.mp4", 
                        false);
                    return currentState;
                }
                case "6" -> {
                    greenApi.sending.sendMessage(OutgoingMessage.builder()
                        .chatId(incomingMessage.getSenderData().getChatId())
                        .message(YmlReader.getString(new String[]{"send_contact_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_contact_documentation"}))
                        .linkPreview(linkPreview)
                        .build());
                    
                    answerWithContact(incomingMessage, Contact.builder()
                        .firstName(incomingMessage.getSenderData().getSenderName())
                        .phoneContact(Long.valueOf(incomingMessage.getSenderData().getSender().replaceAll("@c\\.us", "")))
                        .build(), false);
                    return currentState;
                }
                case "7" -> {
                    greenApi.sending.sendMessage(OutgoingMessage.builder()
                        .chatId(incomingMessage.getSenderData().getChatId())
                        .message(YmlReader.getString(new String[]{"send_location_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_location_documentation"}))
                        .linkPreview(linkPreview)
                        .build());

                    answerWithLocation(incomingMessage, "", "", 35.888171, 14.440230,
                        false);
                    return currentState;
                }
                case "8" -> {
                    greenApi.sending.sendMessage(OutgoingMessage.builder()
                        .chatId(incomingMessage.getSenderData().getChatId())
                        .message(YmlReader.getString(new String[]{"send_poll_message", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_poll_as_buttons"}) +
                            YmlReader.getString(new String[]{"send_poll_message_1", lang.getValue()}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "send_poll_documentation"}))
                        .linkPreview(linkPreview)
                        .build());

                    var options = new ArrayList<Option>();
                    options.add(new Option(YmlReader.getString(new String[]{"poll_option_1", lang.getValue()})));
                    options.add(new Option(YmlReader.getString(new String[]{"poll_option_2", lang.getValue()})));
                    options.add(new Option(YmlReader.getString(new String[]{"poll_option_3", lang.getValue()})));

                    answerWithPoll(incomingMessage, YmlReader.getString(new String[]{"poll_question", lang.getValue()}),
                        options, false, false);
                    return currentState;
                }
                case "9" -> {
                    greenApi.sending.sendMessage(OutgoingMessage.builder()
                        .chatId(incomingMessage.getSenderData().getChatId())
                        .message(YmlReader.getString(new String[]{"get_avatar_message", lang.getValue(), "avatar"}) +
                            YmlReader.getString(new String[]{"links", lang.getValue(), "get_avatar_documentation"}))
                        .linkPreview(linkPreview)
                        .build());

                    var avatar = greenApi.service.getAvatar(incomingMessage.getSenderData().getSender());

                    if (avatar.getBody().getUrlAvatar() != "") {
                        answerWithUrlFile(incomingMessage,
                            YmlReader.getString(new String[]{"avatar_found", lang.getValue()}),
                            avatar.getBody().getUrlAvatar(), "avatar", false);
                    } else {
                        answerWithText(incomingMessage,
                            YmlReader.getString(new String[]{"avatar_not_found", lang.getValue()}), false);
                    }
                    return currentState;
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
                    return currentState;
                }
                case "11" -> {
                    greenApi.sending.sendMessage(OutgoingMessage.builder()
                        .chatId(incomingMessage.getSenderData().getChatId())
                        .message(YmlReader.getString(new String[]{"add_to_contact", lang.getValue()}))
                        .linkPreview(linkPreview)
                        .build());
                                            
                    answerWithContact(incomingMessage, Contact.builder()
                        .firstName(YmlReader.getString(new String[]{"bot_name", lang.getValue()}))
                        .phoneContact(Long.valueOf(incomingMessage.getInstanceData().getWid().replaceAll("@c\\.us", "")))
                        .build(), false);

                    return activateNextScene(currentState, createGroupScene);
                }
                case "12" -> {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"send_quoted_message", lang.getValue()}));

                    greenApi.sending.sendMessage(OutgoingMessage.builder()
                        .chatId(incomingMessage.getSenderData().getChatId())
                        .message(YmlReader.getString(new String[]{"links", lang.getValue(), "send_quoted_message_documentation"}))
                        .linkPreview(linkPreview)
                        .build());

                    return currentState;
                }
                case "13" -> {
                    answerWithUrlFile(incomingMessage,
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
                            "https://raw.githubusercontent.com/green-api/whatsapp-demo-chatbot-java/refs/heads/master/src/main/resources/assets/about_java.jpg",
                            "about_java.jpg",
                            false
                        );
                    return currentState;
                }
                case "stop", "стоп", "Stop", "Стоп", "0" -> {
                    answerWithText(incomingMessage,
                        YmlReader.getString(new String[]{"stop_message", lang.getValue()}) +
                            "*" + incomingMessage.getSenderData().getSenderName() + "*!", false);

                    return activateStartScene(currentState);
                }
                case "menu", "меню", "Menu", "Меню" -> {                  
                    String welcomeFileURL;
                    if (lang == Language.RU) {
                        welcomeFileURL = "https://raw.githubusercontent.com/green-api/whatsapp-demo-chatbot-java/refs/heads/master/src/main/resources/assets/welcome_ru.jpg";
                    } else {
                        welcomeFileURL = "https://raw.githubusercontent.com/green-api/whatsapp-demo-chatbot-java/refs/heads/master/src/main/resources/assets/welcome_en.jpg";
                    }

                    answerWithUrlFile(incomingMessage,
                        YmlReader.getString(new String[]{"menu", lang.getValue()}),
                        welcomeFileURL,
                    "welcome.jpg",
                    false);

                    return currentState;
                }
                default -> {
                    answerWithText(incomingMessage, YmlReader.getString(new String[]{"not_recognized_message", lang.getValue()}));
                    return currentState;
                }
            }
        } catch (Exception e) {
            log.error(e);
            answerWithText(incomingMessage, YmlReader.getString(new String[]{"sorry_message"}));
            return currentState;
        }
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
            log.error(e.getStackTrace());
            answerWithText(pollUpdate, YmlReader.getString(new String[]{"sorry_message"}), false);
        }
    }
}
