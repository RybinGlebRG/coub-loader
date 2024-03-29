package ru.rerumu.coub_loader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.api.CoubChannelAPI;
import ru.rerumu.coub_loader.exceptions.CoubAlreadyProcessedException;
import ru.rerumu.coub_loader.exceptions.HttpErrorException;
import ru.rerumu.coub_loader.factories.CoubChannelAPIFactory;
import ru.rerumu.coub_loader.models.Coub;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class LikesLoader {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CoubProcessor coubProcessor;
    private final CoubChannelAPIFactory coubChannelAPIFactory;

    public LikesLoader(CoubProcessor coubProcessor, CoubChannelAPIFactory coubChannelAPIFactory) {
        this.coubProcessor = coubProcessor;
        this.coubChannelAPIFactory = coubChannelAPIFactory;
    }

    public void load(String channel) throws URISyntaxException, IOException, InterruptedException, HttpErrorException {
        CoubChannelAPI coubChannel = coubChannelAPIFactory.getCoubChannelAPI(channel);
        int totalPages = coubChannel.getTotalPages();
        for (int i = 1; i <= totalPages; i++) {
            List<Coub> coubList = coubChannel.getPage(i);
            logger.info(String.format("Processing page '%d' out of '%d'",i,totalPages));
            for (Coub coub : coubList) {
                try {
                    coubProcessor.process(coub);
                }
                catch (CoubAlreadyProcessedException ignore){}
                catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }


    }
}
