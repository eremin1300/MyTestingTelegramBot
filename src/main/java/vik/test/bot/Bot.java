package vik.test.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import vik.test.App;
import vik.test.models.DailyDomains;
import vik.test.models.User;

import static com.google.common.collect.Iterables.size;

@Component
public class Bot extends TelegramLongPollingBot {
    static final Logger logger = LoggerFactory.getLogger(App.class);
    @Autowired
    private MessageService messageReporter;
    @Autowired
    private DBService dbConnector;
    @Autowired
    private DomainService domainConnector;

    @Value("${bot.name}")
    private String BOT_NAME;
    @Value("${bot.token}")
    private String TOKEN;


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message msg = update.getMessage();
            dbConnector.takeData(msg);
            String answer = messageReporter.getAnswer(msg);
            sendMsg(msg, answer);
            dbConnector.addMessageToTable(msg, answer);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    private synchronized void sendMsg(Message msg, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText(text);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            logger.warn(e.getMessage());
        }
    }

    //можно было бы оставить только метод ниже но мне так было удобно )
    protected synchronized void sendMsg(Long chatId, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(chatId);
        s.setText(text);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            logger.warn(e.getMessage());
        }
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    private void downloadAndSend() {
        logger.info("Start downloading domains");
        domainConnector.downloadDomains();
        sendMessageWithDomains();
    }

    private void sendMessageWithDomains() {
        Iterable<DailyDomains> domains = dbConnector.getDomains();
        Iterable<User> users = dbConnector.getUsers();
        String text = "Собрано " + size(domains) + " доменов";
        for (User user : users) {
            sendMsg(user.getChatId(), text);
        }
    }
}
