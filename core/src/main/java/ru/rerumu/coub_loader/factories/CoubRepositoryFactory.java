package ru.rerumu.coub_loader.factories;

import ru.rerumu.coub_loader.repositories.CoubRepository;

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
