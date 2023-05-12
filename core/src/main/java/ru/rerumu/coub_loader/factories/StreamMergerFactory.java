package ru.rerumu.coub_loader.factories;

import org.apache.commons.lang3.RandomStringUtils;
import ru.rerumu.coub_loader.api.FFmpegAPI;
import ru.rerumu.coub_loader.services.StreamMerger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StreamMergerFactory {

    private final Path appDir;

    public StreamMergerFactory(Path appDir){
        this.appDir = appDir;
    }

    public StreamMerger getStreamMerger(FFmpegAPI fFmpegAPI) throws IOException {
        Path tmpDir = appDir.resolve(RandomStringUtils.randomAlphabetic(10));
        Files.createDirectory(tmpDir);
        return new StreamMerger(tmpDir,fFmpegAPI);
    }
}
