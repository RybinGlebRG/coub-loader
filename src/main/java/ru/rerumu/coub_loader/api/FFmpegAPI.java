package ru.rerumu.coub_loader.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.exceptions.MergeException;
import ru.rerumu.coub_loader.services.ProcessRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FFmpegAPI {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Path ffmpegPath;

    public FFmpegAPI(Path ffmpegPath){
        this.ffmpegPath = ffmpegPath;
    }

//    public void merge(Path confPath, Path audio, Path result) throws MergeException {
//        try {
//            ProcessRunner processRunner = new ProcessRunner(
//                    List.of(ffmpegPath.toString(), "-y", "-v", "error",
//                            "-f", "concat", "-safe", "0",
//                            "-i", "file:" + confPath, "-i", "file:" + audio,
////                            "-c", "copy",
//                            "-shortest", "file:" + result)
//            );
//            processRunner.run();
//        } catch (IOException | ExecutionException | InterruptedException e){
//            logger.error(e.getMessage(), e);
//            throw new MergeException();
//        }
//    }

    public void merge(Path video, Path output) throws MergeException {
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
                            .videoStreamLoop(1)
                            .video(video)
                            .output(output)
                            .build()
            );
            processRunner.run();
        } catch (IOException | ExecutionException | InterruptedException e){
            logger.error(e.getMessage(), e);
            throw new MergeException();
        }
    }

    public void merge(Path video, Path audio, Path output) throws MergeException {
        try {
            ProcessRunner processRunner = new ProcessRunner(
                    new CommandBuilder()
                            .ffmpegPath(ffmpegPath)
                            .verbose("error")
                            .videoStreamLoop(-1)
                            .video(video)
                            .audio(audio)
                            .output(output)
                            .build()
            );
            processRunner.run();
        } catch (IOException | ExecutionException | InterruptedException e){
            logger.error(e.getMessage(), e);
            throw new MergeException();
        }
    }

    private static class CommandBuilder{

        private String verbose;
        private Path ffmpegPath;
//        private Path confPath;

        private Path video;

        private Path audio;

        private Integer videoStreamLoop;
        private Path output;

        public CommandBuilder verbose(String level){
            this.verbose = level;
            return this;
        }
        public CommandBuilder ffmpegPath(Path path){
            this.ffmpegPath = path;
            return this;
        }
        public CommandBuilder output(Path path){
            this.output = path;
            return this;
        }

        public CommandBuilder video(Path path){
            this.video = path;
            return this;
        }
        public CommandBuilder audio(Path path){
            this.audio = path;
            return this;
        }
        public CommandBuilder videoStreamLoop(int val){
            this.videoStreamLoop = val;
            return this;
        }

        // TODO: write
        public List<String> build(){
            List<String> res = new ArrayList<>();

            if (ffmpegPath == null || video == null || videoStreamLoop == null || output == null){
                throw new IllegalArgumentException();
            }

            res.add(ffmpegPath.toString());
            res.add("-y");

            if (verbose != null){
                res.add("-v");
                res.add(verbose);
            }

            res.add("-stream_loop");
            res.add(Integer.toString(videoStreamLoop));

            res.add("-i");
            res.add(String.format("file:%s",video.toString()));

            if (audio != null) {
                res.add("-i");
                res.add(String.format("file:%s", audio.toString()));

                res.add("-c:a");
                res.add("copy");
            }

            res.add("-c:v");
            res.add("libx264");
            res.add("-shortest");
            res.add("-fflags");
            res.add("+shortest");
            res.add("-max_interleave_delta");
            res.add("100M");
            res.add("-movflags");
            res.add("+faststart");

            res.add(String.format("file:%s", output.toString()));

            return res;
        }
    }
}
