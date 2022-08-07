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
import java.nio.file.Path;
import java.util.Optional;

// TODO: video and audio same extension?
public class URIRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final URILoader uriLoader;

    public URIRepository(URILoader uriLoader){
        this.uriLoader = uriLoader;
    }

    public Path saveVideo(Coub coub, Path tmpDir) throws URISyntaxException, IOException {
        URI videoLink = coub.getVideoLink();
        String extension = FilenameUtils.getExtension(videoLink.toURL().getPath());
        Path target = tmpDir.resolve(coub.getId() + "." + extension);
        uriLoader.load(videoLink,target);
        return target;
    }

    public Optional<Path> saveAudio(Coub coub, Path tmpDir) throws URISyntaxException, IOException {
        try {
            URI audioLink = coub.getAudioLink();
            String extension = FilenameUtils.getExtension(audioLink.toURL().getPath());
            Path target = tmpDir.resolve(coub.getId() + "." + extension);
            uriLoader.load(audioLink,target);
            return Optional.of(target);
        } catch (NoAudioLinkException e) {
            logger.warn("No audio");
            return Optional.empty();
        }
    }
}
