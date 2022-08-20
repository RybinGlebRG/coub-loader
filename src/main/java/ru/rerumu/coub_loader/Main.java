package ru.rerumu.coub_loader;

import ru.rerumu.coub_loader.api.FFmpegAPI;
import ru.rerumu.coub_loader.factories.CoubChannelAPIFactory;
import ru.rerumu.coub_loader.repositories.LocalCoubRepository;
import ru.rerumu.coub_loader.services.URILoader;
import ru.rerumu.coub_loader.services.CoubProcessor;
import ru.rerumu.coub_loader.services.LikesLoader;
import ru.rerumu.coub_loader.services.StreamMerger;
import ru.rerumu.coub_loader.repositories.URIRepository;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args){
        try {
            Configuration configuration = new Configuration();

            LocalCoubRepository coubRepository = new LocalCoubRepository(
                    Paths.get(configuration.getProperty("coub_repository.dir"))
            );
            FFmpegAPI ffmpegAPI = new FFmpegAPI(Paths.get(configuration.getProperty("ffmpeg.path")));
            StreamMerger streamMerger = new StreamMerger(
                    Paths.get(configuration.getProperty("stream_merger.tmp_dir")),
                    ffmpegAPI
            );
            URILoader uriLoader = new URILoader();
            URIRepository uriRepository = new URIRepository(uriLoader);
            CoubProcessor coubProcessor = new CoubProcessor(
                    coubRepository,
                    streamMerger,
                    Paths.get(configuration.getProperty("coub_processor.tmp_dir")),
                    uriRepository
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
