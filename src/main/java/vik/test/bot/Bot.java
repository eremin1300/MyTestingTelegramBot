package vik.test.bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
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
import vik.test.models.Messages;
import vik.test.models.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

import static com.google.common.collect.Iterables.size;

@Component
public class Bot extends TelegramLongPollingBot {
    static final Logger logger = LoggerFactory.getLogger(App.class);
    private final String HELLO = "Hello, ";
    private final String HELLO_START = "Hello! This is simple bot!";
    private final String DONT_UNDERSTAND = "I am sorry, i don't understand you";
    private int dataLength = 0;
    @Autowired
    private vik.test.repository.UsersRepository usersRepository;
    @Autowired
    private vik.test.repository.DailyDomainsRepo dailyDomainsRepo;
    @Autowired
    private vik.test.repository.MessageRepo messageRepo;

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
            Message msg = update.getMessage(); // Это нам понадобится
            String txt = msg.getText();
            Integer id = msg.getFrom().getId();
            String userName = msg.getFrom().getFirstName();
            if (isUserExist(id))
                updateUserData(msg);
            else createUser(msg);

            if (txt.equals("/start")) {
                sendMsg(msg, HELLO_START);
                addMessageToTable(msg, HELLO_START);
            } else if (txt.toLowerCase().equals("hello")) {
                sendMsg(msg, HELLO + userName);
                addMessageToTable(msg, HELLO + userName);
            } else if (txt.toLowerCase().equals("hi")) {
                sendMsg(msg, HELLO + userName);
                addMessageToTable(msg, HELLO + userName);
            } else {
                sendMsg(msg, DONT_UNDERSTAND);
                addMessageToTable(msg, DONT_UNDERSTAND);
            }


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
    private synchronized void sendMsg(Long chatId, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(chatId);
        s.setText(text);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            logger.warn(e.getMessage());
        }
    }

    private boolean isUserExist(Integer id) {
        if (usersRepository.findById(id) != null) {
            return true;
        } else return false;
    }

    private void createUser(Message msg) {
        Integer id = msg.getFrom().getId();
        String userName = msg.getFrom().getFirstName();
        Long chatId = msg.getChatId();
        Date date = new Date();
        usersRepository.save(new User(id, userName, chatId, date));
    }

    private void addMessageToTable(Message msg, String answer) {
        Date date = new Date();
        User user = usersRepository.findById(msg.getFrom().getId());
        messageRepo.save(new Messages(user, msg.getText(), answer, date));
    }

    private void updateUserData(Message msg) {
        Date date = new Date();
        User user = usersRepository.findById(msg.getFrom().getId());
        user.setLastMessageAt(date);
        usersRepository.save(user);
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    private void reportCurrentTime() {
        logger.info("Start downloading domains");
        downloadDomains();
        sendMessageWithDomains();
    }

    //загрузка доменов и сразу преобразование в сущности, сохранение в базу
    private void downloadDomains() {
        dailyDomainsRepo.deleteAll();
        Gson gson = new GsonBuilder().create();
        try {
            URL oracle = new URL("https://backorder.ru/json/?order=desc&expired=1&by=hotness&page=1&items=50");
            DailyDomains[] data = gson.fromJson(new InputStreamReader(oracle.openStream()), DailyDomains[].class);
            for (int i = 0; i < data.length; i++) {
                dailyDomainsRepo.save(data[i]);
            }
        } catch (JsonSyntaxException | JsonIOException | IOException e) {
            logger.warn(e.getMessage());
        }
    }

    private void sendMessageWithDomains() {
        Iterable<DailyDomains> domains = dailyDomainsRepo.findAll();
        Iterable<User> users = usersRepository.findAll();
        String text = "Собрано " + size(domains) + " доменов";
        for (User user : users) {
            sendMsg(user.getChatId(), text);
        }
    }
}
