package ru.rerumu.coub_loader.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.exceptions.MergeException;
import ru.rerumu.coub_loader.services.ProcessRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FFmpegAPI {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Path ffmpegPath;

    public FFmpegAPI(Path ffmpegPath){
        this.ffmpegPath = ffmpegPath;
    }

    public void merge(Path confPath, Path audio, Path result) throws MergeException {
        try {
            ProcessRunner processRunner = new ProcessRunner(
                    List.of(ffmpegPath.toString(), "-y", "-v", "error",
                            "-f", "concat", "-safe", "0",
                            "-i", "file:" + confPath, "-i", "file:" + audio,
                            "-c", "copy", "-shortest", "file:" + result)
            );
            processRunner.run();
        } catch (IOException | ExecutionException | InterruptedException e){
            logger.error(e.getMessage(), e);
            throw new MergeException();
        }
    }

    public void merge(Path confPath, Path result) throws MergeException {
        try {
            ProcessRunner processRunner = new ProcessRunner(
                    List.of(ffmpegPath.toString(),"-y", "-v", "error",
                            "-f", "concat", "-safe", "0",
                            "-i", "file:"+confPath,
                            "-c", "copy", "-shortest", "file:"+result)
            );
            processRunner.run();
        } catch (IOException | ExecutionException | InterruptedException e){
            logger.error(e.getMessage(), e);
            throw new MergeException();
        }
    }
}
