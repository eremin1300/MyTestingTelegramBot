package vik.test.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import vik.test.models.DailyDomains;
import vik.test.models.Messages;
import vik.test.models.User;
import vik.test.repositorys.DailyDomainsRepo;
import vik.test.repositorys.MessageRepo;
import vik.test.repositorys.UsersRepository;

import java.util.Date;

@Service
public class DBService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private DailyDomainsRepo dailyDomainsRepo;

    protected boolean isUserExist(Integer id) {
        if (usersRepository.findById(id) != null) {
            return true;
        } else return false;
    }

    protected void createUser(Message msg) {
        Integer id = msg.getFrom().getId();
        String userName = msg.getFrom().getFirstName();
        Long chatId = msg.getChatId();
        Date date = new Date();
        usersRepository.save(new User(id, userName, chatId, date));
    }

    protected void addMessageToTable(Message msg, String answer) {
        Date date = new Date();
        User user = usersRepository.findById(msg.getFrom().getId());
        messageRepo.save(new Messages(user, msg.getText(), answer, date));
    }

    protected void updateUserData(Message msg) {
        Date date = new Date();
        User user = usersRepository.findById(msg.getFrom().getId());
        user.setLastMessageAt(date);
        usersRepository.save(user);
    }

    protected Iterable<User> getUsers() {
        Iterable<User> users = usersRepository.findAll();
        return users;
    }

    protected Iterable<DailyDomains> getDomains() {
        Iterable<DailyDomains> domains = dailyDomainsRepo.findAll();
        return domains;
    }

    public void takeData(Message msg) {
        if (isUserExist(msg.getFrom().getId()))
            updateUserData(msg);
        else createUser(msg);
    }

    public void clearDailyDomains() {
        dailyDomainsRepo.deleteAll();
    }

    public void saveDailyDomains(DailyDomains datum) {
        dailyDomainsRepo.save(datum);
    }
}
