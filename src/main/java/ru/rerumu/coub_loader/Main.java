package ru.rerumu.coub_loader;

import ru.rerumu.coub_loader.api.FFmpegAPI;
import ru.rerumu.coub_loader.factories.CoubChannelAPIFactory;
import ru.rerumu.coub_loader.repositories.AudioRepository;
import ru.rerumu.coub_loader.repositories.CoubRepository;
import ru.rerumu.coub_loader.repositories.VideoRepository;
import ru.rerumu.coub_loader.services.URILoader;
import ru.rerumu.coub_loader.services.CoubProcessor;
import ru.rerumu.coub_loader.services.LikesLoader;
import ru.rerumu.coub_loader.services.StreamMerger;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args){
        try {
            Configuration configuration = new Configuration();

            CoubRepository coubRepository = new CoubRepository(
                    Paths.get(configuration.getProperty("coub_repository.dir"))
            );
            FFmpegAPI ffmpegAPI = new FFmpegAPI(Paths.get(configuration.getProperty("ffmpeg.path")));
            StreamMerger streamMerger = new StreamMerger(
                    Paths.get(configuration.getProperty("stream_merger.tmp_dir")),
                    ffmpegAPI
            );
            URILoader uriLoader = new URILoader();
            AudioRepository audioRepository = new AudioRepository(
                    Paths.get(configuration.getProperty("audio_repository.dir")),
                    uriLoader
            );
            VideoRepository videoRepository = new VideoRepository(
                    Paths.get(configuration.getProperty("video_repository.dir")),
                    uriLoader
            );
            CoubProcessor coubProcessor = new CoubProcessor(
                    coubRepository,
                    streamMerger,
                    audioRepository,
                    videoRepository
                    );
            LikesLoader likesLoader = new LikesLoader(
                    coubProcessor,
                    new CoubChannelAPIFactory()
            );

            likesLoader.load(configuration.getProperty("channel"));

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
