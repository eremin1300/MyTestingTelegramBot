package vik.test.bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import vik.test.App;
import vik.test.models.DailyDomains;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Service
public class DomainService {
    static final Logger logger = LoggerFactory.getLogger(App.class);

    @Autowired
    private DBService dbConnector;

    public void downloadDomains() {
        Gson gson = new GsonBuilder().create();
        try {
            dbConnector.clearDailyDomains();
            URL oracle = new URL("https://backorder.ru/json/?order=desc&expired=1&by=hotness&page=1&items=50");
            DailyDomains[] data = gson.fromJson(new InputStreamReader(oracle.openStream()), DailyDomains[].class);
            for (int i = 0; i < data.length; i++) {
                dbConnector.saveDailyDomains(data[i]);
            }
        } catch (JsonSyntaxException | JsonIOException | IOException e) {
            logger.warn(e.getMessage());
        }
    }
}
