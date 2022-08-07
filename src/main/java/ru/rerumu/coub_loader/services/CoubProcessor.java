package ru.rerumu.coub_loader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.exceptions.CoubAlreadyProcessedException;
import ru.rerumu.coub_loader.models.Coub;
import ru.rerumu.coub_loader.repositories.LocalCoubRepository;
import ru.rerumu.coub_loader.repositories.URIRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoubProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final LocalCoubRepository localCoubRepository;
    private final StreamMerger streamMerger;
    private final Path tmpDir;
    private final URIRepository uriRepository;

    public CoubProcessor(LocalCoubRepository localCoubRepository,
                         StreamMerger streamMerger,
                         Path tmpDir,
                         URIRepository uriRepository){
        this.localCoubRepository = localCoubRepository;
        this.streamMerger = streamMerger;
        this.tmpDir = tmpDir;
        this.uriRepository = uriRepository;
    }

    private void clearTmp() throws IOException {
        List<Path> filesToDelete;
        try(Stream<Path> pathStream = Files.walk(tmpDir)) {
            filesToDelete = pathStream
                    .filter(path-> !path.equals(tmpDir))
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        for (Path path: filesToDelete){
            Files.delete(path);
        }
    }

    public void process(Coub coub)
            throws CoubAlreadyProcessedException,
            IOException,
            URISyntaxException,
            ExecutionException,
            InterruptedException {
        if (localCoubRepository.contains(coub)){
            throw new CoubAlreadyProcessedException();
        }

        clearTmp();

        Path video = uriRepository.saveVideo(coub,tmpDir);
        Optional<Path> audio = uriRepository.saveAudio(coub,tmpDir);

        Path merged;
        if (audio.isPresent()){
            merged = streamMerger.merge(coub.getId(),video,audio.get());
        } else {
            merged = streamMerger.merge(coub.getId(),video);
        }
        localCoubRepository.saveMerged(merged);
        localCoubRepository.saveMeta(coub);
    }
}
