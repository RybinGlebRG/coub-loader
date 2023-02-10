package ru.rerumu.coub_loader;

import ru.rerumu.coub_loader.api.FFmpegAPI;
import ru.rerumu.coub_loader.factories.*;
import ru.rerumu.coub_loader.repositories.AudioRepository;
import ru.rerumu.coub_loader.repositories.CoubRepository;
import ru.rerumu.coub_loader.repositories.VideoRepository;
import ru.rerumu.coub_loader.services.URILoader;
import ru.rerumu.coub_loader.services.CoubProcessor;
import ru.rerumu.coub_loader.services.LikesLoader;
import ru.rerumu.coub_loader.services.StreamMerger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args){
        try {
            Configuration configuration = new Configuration();

            Path appDir = Paths.get(configuration.getProperty("COUB_LOADER_DIR"));
            FFmpegAPI ffmpegAPI = new FFmpegAPI(Paths.get(configuration.getProperty("COUB_LOADER_FFMPEG")));
            URILoader uriLoader = new URILoader();

            var temporaryRepositoryFactory = new TemporaryRepositoryFactory(appDir);

            StreamMerger streamMerger = temporaryRepositoryFactory.getStreamMerger(ffmpegAPI);
            AudioRepository audioRepository = temporaryRepositoryFactory.getAudioRepository(uriLoader);
            VideoRepository videoRepository = temporaryRepositoryFactory.getVideoRepository(uriLoader);
            CoubRepository coubRepository = temporaryRepositoryFactory.getCoubRepository();

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

            likesLoader.load(configuration.getProperty("COUB_LOADER_CHANNEL"));

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
