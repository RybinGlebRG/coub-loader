package ru.rerumu.coub_loader.factories;

import org.apache.commons.lang3.RandomStringUtils;
import ru.rerumu.coub_loader.repositories.AudioRepository;
import ru.rerumu.coub_loader.services.URILoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AudioRepositoryFactory {

    private final Path appDir;

    public AudioRepositoryFactory(Path appDir){
        this.appDir = appDir;
    }

    public AudioRepository getAudioRepository(URILoader uriLoader) throws IOException {

        Path repositoryDir = appDir.resolve(RandomStringUtils.randomAlphabetic(10));
        Files.createDirectory(repositoryDir);
        return new AudioRepository(repositoryDir,uriLoader);
    }
}
