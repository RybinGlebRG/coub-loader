package ru.rerumu.coub_loader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamMerger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Path tmpDir;
    private final Path ffmpegPath;

    public StreamMerger(Path tmpDir, Path ffmpegPath){
        this.ffmpegPath = ffmpegPath;
        this.tmpDir = tmpDir;
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

    private Path prepareConfig(Path video) throws IOException {
        return prepareConfig(video,1000);
    }

    private Path prepareConfig(Path video, int n) throws IOException {
        Path confPath = tmpDir.resolve("conf.txt");
        Files.writeString(
                confPath,
                String.format("file 'file:%s'\n", video.toString()).repeat(n),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
        return confPath;
    }

    private void runFFmpeg(Path confPath, Path audio, Path tempRes) throws IOException, InterruptedException, ExecutionException {
        ProcessRunner processRunner = new ProcessRunner(
                List.of(ffmpegPath.toString(),"-y", "-v", "error",
                "-f", "concat", "-safe", "0",
                "-i", "file:"+confPath, "-i", "file:"+audio,
                "-c", "copy", "-shortest", "file:"+tempRes)
        );
        processRunner.run();
    }

    private void runFFmpeg(Path confPath, Path tempRes) throws IOException, InterruptedException, ExecutionException {
        ProcessRunner processRunner = new ProcessRunner(
                List.of(ffmpegPath.toString(),"-y", "-v", "error",
                        "-f", "concat", "-safe", "0",
                        "-i", "file:"+confPath,
                        "-c", "copy", "-shortest", "file:"+tempRes)
        );
        processRunner.run();

    }

    public Path merge(long coubId, Path video, Path audio) throws IOException, InterruptedException, ExecutionException {
        clearTmp();
        Path confPath = prepareConfig(video);
        Path tempRes = tmpDir.resolve("temp_res.mkv");
        Path finalRes = tmpDir.resolve(coubId+".mkv");
        runFFmpeg(confPath,audio,tempRes);
        Files.move(tempRes,finalRes);
        return finalRes;
    }

    public Path merge(long coubId, Path video) throws IOException, InterruptedException, ExecutionException {
        clearTmp();
        Path confPath = prepareConfig(video,1);
        Path tempRes = tmpDir.resolve("temp_res.mkv");
        Path finalRes = tmpDir.resolve(coubId+".mkv");
        runFFmpeg(confPath,tempRes);
        Files.move(tempRes,finalRes);
        return finalRes;
    }
}
