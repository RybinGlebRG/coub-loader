package ru.rerumu.coub_loader.factories;

import org.apache.commons.lang3.RandomStringUtils;
import ru.rerumu.coub_loader.repositories.VideoRepository;
import ru.rerumu.coub_loader.services.URILoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VideoRepositoryFactory {

    private final Path appDir;

    public VideoRepositoryFactory(Path appDir){
        this.appDir = appDir;
    }

    public VideoRepository getVideoRepository(URILoader uriLoader) throws IOException {

        Path repositoryDir = appDir.resolve(RandomStringUtils.randomAlphabetic(10));
        Files.createDirectory(repositoryDir);
        return new VideoRepository(repositoryDir,uriLoader);
    }
}
