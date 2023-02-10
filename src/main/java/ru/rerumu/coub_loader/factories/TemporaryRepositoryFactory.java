package ru.rerumu.coub_loader.factories;

import org.apache.commons.lang3.RandomStringUtils;
import ru.rerumu.coub_loader.api.FFmpegAPI;
import ru.rerumu.coub_loader.repositories.AudioRepository;
import ru.rerumu.coub_loader.repositories.CoubRepository;
import ru.rerumu.coub_loader.repositories.VideoRepository;
import ru.rerumu.coub_loader.services.StreamMerger;
import ru.rerumu.coub_loader.services.URILoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TemporaryRepositoryFactory {
    private final Path appDir;
    private final LocalDateTime localDateTime= LocalDateTime.now();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy__hhmmss");

    public TemporaryRepositoryFactory(Path appDir){
        this.appDir = appDir;
    }

    public AudioRepository getAudioRepository(URILoader uriLoader) throws IOException {

        Path repositoryDir = appDir.resolve("audio__"+localDateTime.format(formatter)+"_+"+RandomStringUtils.randomAlphabetic(10));
        Files.createDirectory(repositoryDir);
        return new AudioRepository(repositoryDir,uriLoader);
    }

    public VideoRepository getVideoRepository(URILoader uriLoader) throws IOException {

        Path repositoryDir = appDir.resolve("video__"+localDateTime.format(formatter)+"_+"+RandomStringUtils.randomAlphabetic(10));
        Files.createDirectory(repositoryDir);
        return new VideoRepository(repositoryDir,uriLoader);
    }

    public StreamMerger getStreamMerger(FFmpegAPI fFmpegAPI) throws IOException {
        Path tmpDir = appDir.resolve("ffmpeg__"+localDateTime.format(formatter)+"_+"+RandomStringUtils.randomAlphabetic(10));
        Files.createDirectory(tmpDir);
        return new StreamMerger(tmpDir,fFmpegAPI);
    }

    public CoubRepository getCoubRepository() throws IOException {
        return new CoubRepository(appDir);
    }

}
