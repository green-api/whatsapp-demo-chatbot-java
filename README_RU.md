# whatsapp-demo-chatbot-java

- [Documentation in English](README.md).

Пример чатбота написанного на java с использованием API сервиса для Whatsapp [green-api.com](https://green-api.com/en/).
Чатбот наглядно демонстрирует использование API для отправки текстовых сообщений, файлов, картинок, локаций и контактов.


## Содержание

* [Установка среды для запуска чатбота](#установка-среды-для-запуска-чатбота)
* [Запуск чатбота](#запуск-чатбота)
* [Настройка чатбота](#настройка-чатбота)
* [Использование](#использование)
* [Структура кода](#структура-кода)
* [Управление сообщениями](#управление-сообщениями)


## Установка среды для запуска чатбота

Чтобы запустить проект вам понадобиться любая IDE.
Откройте ваш редактор кода и создайте новый проект из системы контроля версий. Для этого нажмите `file - new - Project from Version Control System`.
В открывшимся окне введи адрес проекта:

```
https://github.com/green-api/whatsapp-demo-chatbot-java.git
```

Среда для запуска чатбота готова, теперь необходимо произвести настройку и запустить чатбот на вашем аккаунте Whatsapp.

## Запуск чатбота

Для того, чтобы настроить чатбот на своем аккаунте Whatsapp, Вам необходимо перейти в [личный кабинет](https://console.green-api.com/) и зарегистрироваться. Для новых пользователей предоставлена [инструкция](https://green-api.com/docs/before-start/) для настройки аккаунта и получения необходимых для работы чатбота параметров, а именно:
```
idInstance
apiTokenInstance
```

Не забудьте включить все уведомления в настройках инстанса.
После получения данных параметров, найдите класс [`BotStarter`](src/main/java/com/greenapi/demoChatbot/BotStarter.java) и введите `idInstance` и `apiTokenInstance` в сигнатуру метода `createBot()`.
Инициализация данных необходима для связывания бота с Вашим Whatsapp аккаунтом:

```java
var bot = botFactory.createBot(
    "{{INSTANCE}}",
    "{{TOKEN}}");
```

Далее можно запускать программу, для этого нажмите пуск в интерфейсе IDE или введите следующий запрос в командной строке:
```
mvn clean install exec:java -Dexec.mainClass=com.greenapi.demoChatbot.BotStarter
```
Бот должен быть запущен.

## Настройка чатбота

По умолчанию чатбот использует ссылки для выгрузки файлов из сети, однако пользователи могут добавить свои ссылки на файлы, одну для файла любого расширения pdf / docx /... и одну для картинки.

Ссылки должны вести на файлы из облачного хранилища или открытого доступа. В классе [`Endpoints`](src/main/java/com/greenapi/demoChatbot/scenes/Endpoints.java) следующий код:
```java
case "2" -> {
    answerWithUrlFile(incomingMessage,
    YmlReader.getString(new String[]{"send_file_message", lang.getValue()}) +
    YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
    "https://images.rawpixel.com/image_png_1100/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIzLTExL3Jhd3BpeGVsb2ZmaWNlMTlfcGhvdG9fb2ZfY29yZ2lzX2luX2NocmlzdG1hc19zd2VhdGVyX2luX2FfcGFydF80YWM1ODk3Zi1mZDMwLTRhYTItYWM5NS05YjY3Yjg1MTFjZmUucG5n.png",
    "corgi.png");

    return currentState;
    }
```
Добавьте ссылку на файл любого расширения в качестве третьего параметра метода `answerWithUrlFile` и задайте имя файлу в четвертом параметре. Имя файла должно содержать расширение, например "somefile.pdf".
Данная строка после изменения будет в следующем формате:
```java
case "2" -> {
    answerWithUrlFile(incomingMessage,
    YmlReader.getString(new String[]{"send_file_message", lang.getValue()}) +
    YmlReader.getString(new String[]{"links", lang.getValue(), "send_file_documentation"}),
    "https://...somefile.pdf",
    "somefile.pdf");
```

Все изменения должны быть сохранены, после чего можно запускать чатбот. Для запуска чатбота вернитесь к [пункту 2](#запуск-чатбота).

## Использование

Если предыдущие шаги были выполнены, то на вашем аккаунте Whatsapp должен работать чатбот. Важно помнить, что пользователь должен быть авторизован в [личном кабинете](https://console.green-api.com/).

Теперь вы можете отправлять сообщения чатботу!

Чатбот откликнется на любое сообщение отправленное на аккаунт.
Так как чатбот поддерживает 2 языка - русский и английский - то прежде чем поприветствовать собеседника, чатбот попросит выбрать язык общения:
```
1 - English
2 - Русский
```
Ответьте 1 или 2, чтобы выбрать язык для дальнейшего общения. После того как вы отправите 2, чатбот пришлет приветственное сообщение на русском языке:
```
Добро пожаловать в GREEN-API чатбот, пользователь! GREEN-API предоставляет отправку данных следующих видов. Выберите цифру из списка, чтобы проверить как работает метод отправки

1. Текстовое сообщение 📩
2. Файл 📋
3. Картинка 🖼
4. Контакт 📱
5. Геолокация 🌎
6. ...

Чтобы вернуться в начало напишите стоп
```
Выбрав число из списка и отправив его, чатбот ответит каким API был отправлен данный тип сообщения и поделится ссылкой на информацию об API.

Например, отправив 1, пользователь получит в ответ:
```
Это сообщение отправлено через sendMessage метод

Чтобы узнать как работает метод, пройдите по ссылке
https://green-api.com/docs/api/sending/SendMessage/
```
Если отправить что-то помимо чисел 1-11, то чатбот лаконично ответит:
```
Извините, я не совсем вас понял, напишите меню, чтобы посмотреть возможные опции
```
Так же пользователь может вызвать меню, отправив сообщение содержащее "меню". И отправив "стоп", пользователь завершит беседу с чатботом и получит сообщение:
```
Спасибо за использование чатбота GREEN-API, пользователь!
```

## Структура кода

Основной файл чатбота это [`BotStarter`](src/main/java/com/greenapi/demoChatbot/BotStarter.java), в нем находится метод `main` и с него начинается выполнение программы. В этом классе происходит инициализация объекта бота при помощи класса `BotFactory`, установка первой сцены и запуск бота.

```java
public static void main(String[] args) {
    var context = SpringApplication.run(BotStarter.class, args); //Инициация контекста Spring приложения
    var botFactory = context.getBean(BotFactory.class);          //Импорт BotFactory bean из контекста

    var bot = botFactory.createBot(                             //Инициализация бота с параметрами INSTANCE и TOKEN
    "{{INSTANCE}}",
    "{{TOKEN}}");

    bot.setStartScene(new Start());                             //Установка стартовой сцены бота

    bot.startReceivingNotifications();                          //Запуск бота
    }
```

Класс `BotFactory` это Bean который сконфигурирован в [`BotConfig`](src/main/java/com/greenapi/demoChatbot/BotConfig.java). Его задача создать объект бота.
В конфигурации `BotConfig` вы можете более тонко настроить класс `RestTemplate` для отправки запросов или подставить свою имплементацию `StateManager` если она у вас есть.
В данном примере `BotConfig` использует стандартные, рекомендованные значения по умолчанию.

```java
@Configuration
public class BotConfig {

    @Bean
    public RestTemplate restTemplate() {         //Стандартный Spring класс для отправки http запросов. 
        return new RestTemplateBuilder().build();
    }

    @Bean
    public StateManager stateManager() {         //StateManager - интерфейс для работы с состоянием. 
        return new StateManagerHashMapImpl();    //StateManagerHashMapImpl его стандартная реализация. Вы можете написать свою и подставить если вы не хотите хранить данные в hashmap.
    }

    @Bean
    public BotFactory botFactory(RestTemplate restTemplate, StateManager stateManager) {   //BotFactory bean для инициализации объекта бота
        return new BotFactory(restTemplate, stateManager);
    }
}
```

Данный бот использует паттерн сцен для организации кода. Это значит что логика чатбота разделена на фрагменты (сцены), сцена соответствует определенному состоянию диалога и отвечает за обработку ответа.

Для каждого диалога одновременно активна может быть только одна сцена.

Например, первая сцена [`Start`](src/main/java/com/greenapi/demoChatbot/scenes/Start.java) отвечает за приветственное сообщение. Вне зависимости от текста сообщения, бот спрашивает какой язык удобен пользователю и включает следующую сцену, которая отвечает за обработку ответа.

Всего в боте 3 сцены:

Сцена [`Start`](src/main/java/com/greenapi/demoChatbot/scenes/Start.java) - отвечает на любое входящее сообщение, отправляет список доступных языков. Запускает сцену `MainMenu`.
Сцена [`MainMenu`](src/main/java/com/greenapi/demoChatbot/scenes/MainMenu.java) - обрабатывает выбор пользователя и отправляет текст главного меню на выбранном языке. Запускает сцену `Endpoints`
Сцена [`Endpoints`](src/main/java/com/greenapi/demoChatbot/scenes/Endpoints.java) - выполняет выбранный пользователем метод и отправляет описание метода на выбранном языке.

Класс [`SessionManager`](src/main/java/com/greenapi/demoChatbot/util/SessionManager.java) содержит метод `isSessionExpired` который возвращает `true` если пользователь не пишут боту более 2 минут. Он используется, чтобы снова устанавливать стартовую сцену, если боту долго не пишут.
Класс [`YmlReader`](src/main/java/com/greenapi/demoChatbot/util/YmlReader.java) содержит метод `getString()` который возвращает строки из файла `strings.xml` по ключам. Этот файл используется для хранения текстов ответов бота.

## Управление сообщениями

Как и указывает чатбот в ответах, все сообщения отправлены через API. Документацию по методам отправки сообщений можно найти на сайте [green-api.com/docs/api/sending](https://green-api.com/docs/api/sending/).

Что касается получения сообщений, то сообщения вычитываются через HTTP API. Документацию по методам получения сообщений можно найти на сайте [green-api.com/docs/api/receiving/technology-http-api](https://green-api.com/docs/api/receiving/technology-http-api/).

Чатбот использует библиотеку [whatsapp-chatbot-java](https://github.com/green-api/whatsapp-chatbot-java), где уже интегрированы методы отправки и получения сообщений, поэтому сообщения вычитываются автоматически, а отправка обычных текстовых сообщений упрощена.

Например, чатбот автоматически отправляет сообщение контакту, от которого получил сообщение:
```java
    answerWithText(incomingMessage, YmlReader.getString(new String[]{"select_language"}));
```
Однако другие методы отправки можно вызвать напрямую из библиотеки [whatsapp-api-client-java](https://github.com/green-api/whatsapp-api-client-java). Как, например, при получении аватара:
```java
    greenApi.service.getAvatar(incomingMessage.getSenderData().getChatId());
```

## Лицензия

Лицензировано на условиях [Creative Commons Attribution-NoDerivatives 4.0 International (CC BY-ND 4.0)](https://creativecommons.org/licenses/by-nd/4.0/).

[LICENSE](https://github.com/green-api/whatsapp-demo-chatbot-java/blob/master/LICENCE).
