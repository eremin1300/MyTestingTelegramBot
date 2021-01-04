package vik.test.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class MessageService {
    private final String HELLO = "Hello, ";
    private final String HELLO_START = "Hello! This is simple bot!";
    private final String DONT_UNDERSTAND = "I am sorry, i don't understand you";

    @Autowired
    private DBService dbConnector;

    public String getAnswer(Message msg) {
        String txt = msg.getText();
        String userName = msg.getFrom().getFirstName();
        if (txt.equals("/start")) {
            dbConnector.addMessageToTable(msg, HELLO_START);
            return HELLO_START;
        } else if (txt.toLowerCase().equals("hello")) {
            dbConnector.addMessageToTable(msg, HELLO + userName);
            return HELLO + userName;
        } else if (txt.toLowerCase().equals("hi")) {
            dbConnector.addMessageToTable(msg, HELLO + userName);
            return HELLO + userName;
        } else {
            dbConnector.addMessageToTable(msg, DONT_UNDERSTAND);
            return DONT_UNDERSTAND;
        }
    }
}
