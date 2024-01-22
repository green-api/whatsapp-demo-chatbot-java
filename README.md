# whatsapp-demo-chatbot-java

- [Документация на русском](README_RU.md).

An example of a chatbot written in java using the service API for Whatsapp [greenapi.com](https://greenapi.com/en/).
The chatbot clearly demonstrates the use of the API to send text messages, files, pictures, locations and contacts.


## Content

* [Installing the environment for running the chatbot](#setting-up-the-environment-for-running-the-chatbot)
* [Launch chatbot](#launch-a-chatbot)
* [Chatbot setup](#setting-up-a-chatbot)
* [Usage](#usage)
* [Code structure](#code-structure)
* [Message management](#message-management)


## Setting up the environment for running the chatbot

To run the project you will need any IDE.
Open your code editor and create a new project from source control. To do this, click `file - new - Project from Version Control System`.
In the window that opens, enter the project address:

```
https://github.com/green-api/whatsapp-demo-chatbot-java.git
```

The environment for launching the chatbot is ready, now you need to configure and launch the chatbot on your Whatsapp account.

## Launch a chatbot

In order to set up a chatbot on your Whatsapp account, you need to go to [your personal account](https://console.green-api.com/) and register. For new users, [instructions](https://green-api.com/docs/before-start/) are provided for setting up an account and obtaining the parameters necessary for the chatbot to work, namely:
```
idInstance
apiTokenInstance
```

Don't forget to enable all notifications in your instance settings.
After receiving these parameters, find the class [`BotStarter`](src/main/java/com/greenapi/demoChatbot/BotStarter.java) and enter `idInstance` and `apiTokenInstance` into the signature of the `createBot()` method.
Data initialization is necessary to link the bot with your Whatsapp account:

```java
var bot = botFactory.createBot(
     "{{INSTANCE}}",
     "{{TOKEN}}");
```

You can then run the program by clicking start in the IDE interface or entering the following query on the command line:
```
mvn clean install exec:java -Dexec.mainClass=com.greenapi.demoChatbot.BotStarter
```
The bot must be running.

## Setting up a chatbot

By default, the chatbot uses links to download files from the network, but users can add their own links to files, one for a file of any extension pdf / docx /... and one for a picture.

Links must lead to files from cloud storage or public access. In the class [`Endpoints`](src/main/java/com/greenapi/demoChatbot/scenes/Endpoints.java) the following code:
```java
case "2" -> {
     answerWithUrlFile(incomingMessage,
     YmlReader.getString(new String[]{"send_file_message", lang.getValue()}) +
     YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
     "https://images.rawpixel.com/image_png_1100/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIzLTExL3Jhd3BpeGVsb2ZmaWNlMTlfcGhvdG9fb2ZfY29yZ2lzX2luX2NocmlzdG1hc19zd2Vhd GVyX2luX2FfcGFydF80YWM1ODk3Zi1mZDMwLTRhYTItYWM5NS05YjY3Yjg1MTFjZmUucG5n.png",
     "corgi.png");

     return currentState;
     }
```
Add a link to a file of any extension as the third parameter of the `answerWithUrlFile` method and specify the file name in the fourth parameter. The file name must contain an extension, for example "somefile.pdf".
This line after modification will be in the following format:
```java
case "2" -> {
     answerWithUrlFile(incomingMessage,
     YmlReader.getString(new String[]{"send_file_message", lang.getValue()}) +
     YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
     "https://...somefile.pdf",
     "somefile.pdf");
```

All changes must be saved, after which you can launch the chatbot. To launch the chatbot, return to [step 2](#launch-a-chatbot).

## Usage

If the previous steps have been completed, then the chatbot should be working on your Whatsapp account. It is important to remember that the user must be authorized in [personal account](https://console.green-api.com/).

Now you can send messages to the chatbot!

The chatbot will respond to any message sent to your account.
Since the chatbot supports 2 languages - Russian and English - before greeting the interlocutor, the chatbot will ask you to select a language of communication:
```
1 - English
2 - Russian
```
Answer 1 or 2 to select the language for further communication. After you send 1, the chatbot will send a welcome message in English:
```
Welcome to GREEN-API chatbot, user! GREEN-API provides the following types of data sending. Select a number from the list to check how the sending method works

1. Text message 📩
2. File 📋
3. Picture 🖼
4. Contact 📱
5. Geolocation 🌎
6. ...

To return to the beginning write stop
```
By selecting a number from the list and sending it, the chatbot will answer which API sent this type of message and share a link to information about the API.

For example, by sending 1, the user will receive in response:
```
This message was sent by sendMessage method

To find out how the method works, follow the link
https://green-api.com/docs/api/sending/SendMessage/
```
If you send something other than numbers 1-11, the chatbot will succinctly answer:
```
Sorry, I didn't quite understand you, write a menu to see the possible options
```
The user can also call up the menu by sending a message containing “menu”. And by sending “stop”, the user will end the conversation with the chatbot and receive the message:
```
Thank you for using the GREEN-API chatbot, user!
```

## Code structure

The main file of the chatbot is [`BotStarter`](src/main/java/com/greenapi/demoChatbot/BotStarter.java), it contains the `main` method and program execution begins with it. In this class, the bot object is initialized using the `BotFactory` class, the first scene is set, and the bot is launched.

```java
public static void main(String[] args) {
     var context = SpringApplication.run(BotStarter.class, args); //Initiate the Spring application context
     var botFactory = context.getBean(BotFactory.class); //Import BotFactory bean from context

     var bot = botFactory.createBot( //Initialize the bot with INSTANCE and TOKEN parameters
     "{{INSTANCE}}",
     "{{TOKEN}}");

     bot.setStartScene(new Start()); //Setting the bot's starting scene

     bot.startReceivingNotifications(); //Start the bot
     }
```

The `BotFactory` class is a Bean that is configured in [`BotConfig`](src/main/java/com/greenapi/demoChatbot/BotConfig.java). His task is to create a bot object.
In the `BotConfig` configuration you can fine-tune the `RestTemplate` class for sending requests or substitute your own `StateManager` implementation if you have one.
In this example, `BotConfig` uses standard, recommended default values.

```java
@Configuration
public class BotConfig {

     @Bean
     public RestTemplate restTemplate() { //Standard Spring class for sending http requests.
         return new RestTemplateBuilder().build();
     }

     @Bean
     public StateManager stateManager() { //StateManager - interface for working with state.
         return new StateManagerHashMapImpl(); //StateManagerHashMapImpl is its standard implementation. You can write your own and substitute it if you do not want to store the data in a hashmap.
     }

     @Bean
     public BotFactory botFactory(RestTemplate restTemplate, StateManager stateManager) { //BotFactory bean to initialize the bot object
         return new BotFactory(restTemplate, stateManager);
     }
}
```

This bot uses a scene pattern to organize its code. This means that the chatbot logic is divided into fragments (scenes), the scene corresponds to a certain state of the dialogue and is responsible for processing the response.

Only one scene can be active at a time for each dialogue.

For example, the first scene [`Start`](src/main/java/com/greenapi/demoChatbot/scenes/Start.java) is responsible for the welcome message. Regardless of the text of the message, the bot asks what language is convenient for the user and includes the following scene, which is responsible for processing the response.

There are 3 scenes in the bot:

Scene [`Start`](src/main/java/com/greenapi/demoChatbot/scenes/Start.java) - responds to any incoming message, sends a list of available languages. Launches the `MainMenu` scene.
Scene [`MainMenu`](src/main/java/com/greenapi/demoChatbot/scenes/MainMenu.java) - processes the user's selection and sends the main menu text in the selected language. Launches the `Endpoints` scene
Scene [`Endpoints`](src/main/java/com/greenapi/demoChatbot/scenes/Endpoints.java) - executes the user-selected method and sends a description of the method in the selected language.

The [`SessionManager`](src/main/java/com/greenapi/demoChatbot/util/SessionManager.java) class contains the `isSessionExpired` method which returns `true` if the user does not write to the bot for more than 2 minutes. It is used to set the starting scene again if the bot has not been contacted for a long time.
The [`YmlReader`](src/main/java/com/greenapi/demoChatbot/util/YmlReader.java) class contains the `getString()` method which returns strings from the `strings.xml` file by key. This file is used to store the texts of the bot's responses.

## Message management

As the chatbot indicates in its responses, all messages are sent via the API. Documentation on message sending methods can be found at [greenapi.com/en/docs/api/sending](https://greenapi.com/en/docs/api/sending/).

As for receiving messages, messages are read through the HTTP API. Documentation on methods for receiving messages can be found at [greenapi.com/en/docs/api/receiving/technology-http-api](https://greenapi.com/en/docs/api/receiving/technology-http-api/).

The chatbot uses the library [whatsapp-chatbot-java](https://github.com/green-api/whatsapp-chatbot-java), where methods for sending and receiving messages are already integrated, so messages are read automatically and sending regular text messages is simplified .

For example, the chatbot automatically sends a message to the contact from whom it received the message:
```java
     answerWithText(incomingMessage, YmlReader.getString(new String[]{"select_language"}));
```
However, other send methods can be called directly from the [whatsapp-api-client-java](https://github.com/green-api/whatsapp-api-client-java) library. Like, for example, when receiving an avatar:
```java
     greenApi.service.getAvatar(incomingMessage.getSenderData().getChatId());
```

## License

Licensed under [Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)](https://creativecommons.org/licenses/by-nd/4.0/).

[LICENSE](https://github.com/green-api/whatsapp-demo-chatbot-java/blob/master/LICENCE).