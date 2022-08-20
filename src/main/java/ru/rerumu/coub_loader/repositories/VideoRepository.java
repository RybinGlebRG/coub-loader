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

public class VideoRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Path repositoryDir;
    private final URILoader uriLoader;

    public VideoRepository(Path repositoryDir, URILoader uriLoader){
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

    public Path getVideo(Coub coub) throws URISyntaxException, IOException {
        clearRepository();
        URI videoLink = coub.getVideoLink();
        String extension = FilenameUtils.getExtension(videoLink.toURL().getPath());
        if (!List.of("mp4").contains(extension)){
            throw new IllegalArgumentException();
        }
        Path target = repositoryDir.resolve(coub.getId() + "." + extension);
        uriLoader.load(videoLink,target);
        return target;
    }
}
