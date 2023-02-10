package ru.rerumu.coub_loader.factories;

import org.apache.commons.lang3.RandomStringUtils;
import ru.rerumu.coub_loader.repositories.CoubRepository;
import ru.rerumu.coub_loader.repositories.VideoRepository;
import ru.rerumu.coub_loader.services.URILoader;

import java.io.IOException;
import java.nio.file.Path;

public class CoubRepositoryFactory {

    private final Path appDir;

    public CoubRepositoryFactory(Path appDir){
        this.appDir = appDir;
    }

    public CoubRepository getCoubRepository() throws IOException {
        return new CoubRepository(appDir);
    }
}
