package ru.rerumu.coub_loader.repositories;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.exceptions.NoAudioLinkException;
import ru.rerumu.coub_loader.models.Coub;
import ru.rerumu.coub_loader.services.URILoader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AudioRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Path repositoryDir;
    private final URILoader uriLoader;

    public AudioRepository(Path repositoryDir, URILoader uriLoader){
        this.repositoryDir = repositoryDir;
        this.uriLoader = uriLoader;
    }

    private void clearRepository() throws IOException {
        List<Path> filesToDelete;
        try(Stream<Path> pathStream = Files.walk(repositoryDir)) {
            filesToDelete = pathStream
                    .filter(path-> !path.equals(repositoryDir))
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        for (Path path: filesToDelete){
            Files.delete(path);
        }
    }

    public Optional<Path> getAudio(Coub coub) throws URISyntaxException, IOException {
        clearRepository();
        try {
            URI audioLink = coub.getAudioLink();
            String extension = FilenameUtils.getExtension(audioLink.toURL().getPath());
            if (!List.of("mp3").contains(extension)){
                throw new IllegalArgumentException();
            }
            Path target = repositoryDir.resolve(coub.getId() + "." + extension);
            uriLoader.load(audioLink,target);
            return Optional.of(target);
        } catch (NoAudioLinkException e) {
            logger.warn("No audio");
            return Optional.empty();
        }
    }
}
