package ru.rerumu.coub_loader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.api.CoubChannel;
import ru.rerumu.coub_loader.exceptions.HttpErrorException;
import ru.rerumu.coub_loader.models.Coub;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class LikesLoader {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CoubProcessor coubProcessor;

    public LikesLoader(CoubProcessor coubProcessor) {
        this.coubProcessor = coubProcessor;
    }

    public void load(String channel) throws URISyntaxException, IOException, InterruptedException, HttpErrorException {
        CoubChannel coubChannel = new CoubChannel(channel);
        int totalPages = coubChannel.getTotalPages();
        for (int i = 1; i <= totalPages; i++) {
            List<Coub> coubList = coubChannel.getPage(i);

            for (Coub coub : coubList) {
                try {
                    coubProcessor.process(coub);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }


    }
}
