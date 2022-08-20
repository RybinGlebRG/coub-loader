package ru.rerumu.coub_loader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.exceptions.CoubAlreadyProcessedException;
import ru.rerumu.coub_loader.exceptions.MergeException;
import ru.rerumu.coub_loader.models.Coub;
import ru.rerumu.coub_loader.repositories.AudioRepository;
import ru.rerumu.coub_loader.repositories.CoubRepository;
import ru.rerumu.coub_loader.repositories.VideoRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Optional;

public class CoubProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CoubRepository coubRepository;
    private final StreamMerger streamMerger;
    private final AudioRepository audioRepository;
    private final VideoRepository videoRepository;

    public CoubProcessor(CoubRepository coubRepository,
                         StreamMerger streamMerger,
                         AudioRepository audioRepository,
                         VideoRepository videoRepository){
        this.coubRepository = coubRepository;
        this.streamMerger = streamMerger;
        this.audioRepository = audioRepository;
        this.videoRepository = videoRepository;
    }

    public void process(Coub coub)
            throws CoubAlreadyProcessedException,
            IOException,
            URISyntaxException,
            MergeException {
        logger.info(String.format("Processing coubId='%d'",coub.getId()));
        if (coubRepository.contains(coub)){
            logger.error("Coub already processed");
            throw new CoubAlreadyProcessedException();
        }

        Path video = videoRepository.getVideo(coub);
        logger.debug(String.format("Got video '%s'",video.toString()));
        Optional<Path> audio = audioRepository.getAudio(coub);
        logger.debug(String.format("Got audio '%s'", audio.map(Path::toString).orElse("empty (no audio)")));

        Path merged;
        if (audio.isPresent()){
            logger.debug("Merging video with audio");
            merged = streamMerger.merge(coub.getId(),video,audio.get());
        } else {
            logger.debug("Merging video only");
            merged = streamMerger.merge(coub.getId(),video);
        }
        logger.debug("Finished merging");
        coubRepository.saveMerged(merged);
        logger.debug("Saved merged");
        coubRepository.saveMeta(coub);
        logger.debug("Saved meta");
        logger.info("Finished processing");
    }
}
