package ru.rerumu.coub_loader.repositories;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.models.Coub;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoubRepository {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Path metaDir;
    private final Path coubDir;

    private final Path metaFile;
    private final List<Long> presentCoubs;



    public CoubRepository(Path repositoryDir) throws IOException {
        this.metaDir = repositoryDir.resolve("coub_meta");
        if (Files.notExists(metaDir)) {
            Files.createDirectory(metaDir);
        }
        this.coubDir = repositoryDir.resolve("coub");
        if (Files.notExists(coubDir)) {
            Files.createDirectory(coubDir);
        }
        this.metaFile = repositoryDir.resolve("meta.lst");
        try(BufferedReader bufferedReader = Files.newBufferedReader(metaFile,StandardCharsets.UTF_8)){
            presentCoubs = bufferedReader.lines()
                    .mapToLong(Long::valueOf)
                    .boxed()
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
        Files.writeString(
                metaFile,
                Long.toString(coub.getId())+"\n",
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND,
                StandardOpenOption.WRITE
        );
    }

    public void saveMerged(Path merged) throws IOException {
        Path target = coubDir.resolve(merged.getFileName());
        Files.move(merged,target);
    }
}
