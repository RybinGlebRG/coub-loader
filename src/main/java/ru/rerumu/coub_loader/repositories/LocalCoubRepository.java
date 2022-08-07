package ru.rerumu.coub_loader.repositories;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.exceptions.NoAudioLinkException;
import ru.rerumu.coub_loader.models.Coub;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalCoubRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Path metaDir;
    private final Path coubDir;
    private final List<Long> presentCoubs;



    public LocalCoubRepository(Path repositoryDir) throws IOException {
        this.metaDir = repositoryDir.resolve("coub_meta");
        this.coubDir = repositoryDir.resolve("coub");
        try (Stream<Path> pathStream = Files.walk(metaDir)) {
            presentCoubs = pathStream
                    .filter(path->!path.equals(metaDir))
                    .map(path -> Long.parseLong(FilenameUtils.removeExtension(path.getFileName().toString())))
                    .collect(Collectors.toCollection(ArrayList::new));

        }
    }

    public boolean contains(Coub coub){
        return presentCoubs.contains(coub.getId());
    }

    public void saveMeta(Coub coub) throws IOException {
        Files.writeString(
                metaDir.resolve(coub.getId()+".json"),
                coub.getRaw().toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    public void saveMerged(Path merged) throws IOException {
        Path target = coubDir.resolve(merged.getFileName());
        Files.move(merged,target);
    }
}
