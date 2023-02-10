package ru.rerumu.coub_loader.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.exceptions.MergeException;
import ru.rerumu.coub_loader.services.ProcessRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
//                            "-c", "copy",
                            "-shortest", "file:" + result)
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
//                    List.of(ffmpegPath.toString(),"-y", "-v", "error",
//                            "-f", "concat", "-safe", "0",
//                            "-i", "file:"+confPath,
////                            "-c", "copy",
//                            "-shortest", "file:"+result)
                    new CommandBuilder()
                            .ffmpegPath(ffmpegPath)
                            .verbose("error")
                            .confPath(confPath)
                            .resultPath(result)
                            .build()
            );
            processRunner.run();
        } catch (IOException | ExecutionException | InterruptedException e){
            logger.error(e.getMessage(), e);
            throw new MergeException();
        }
    }

    private static class CommandBuilder{

        private Map.Entry<String,String> verbose;
        private Path ffmpegPath;
        private Path confPath;
        private Path result;

        public CommandBuilder verbose(String level){
            this.verbose = new AbstractMap.SimpleEntry<String,String>("-v",level);
            return this;
        }

        public CommandBuilder ffmpegPath(Path path){
            this.ffmpegPath = path;
            return this;
        }

        public CommandBuilder confPath(Path path){
            this.confPath = path;
            return this;
        }

        public CommandBuilder resultPath(Path path){
            this.result = path;
            return this;
        }

        // TODO: write
        public List<String> build(){
            List<String> res = new ArrayList<>();

            if (ffmpegPath == null){
                throw new IllegalArgumentException();
            } else {
                res.add(ffmpegPath.toString());
            }

            res.add("-y");
            if (verbose != null){
                res.add(verbose.getKey());
                res.add(verbose.getValue());
            }
            res.add("-f");
            res.add("concat");
            res.add("-safe");
            res.add("0");
            res.add("-i");
            res.add("file:"+confPath.toString());
            res.add("-shortest");
            res.add("file:"+result.toString());
            return res;
        }
    }
}
