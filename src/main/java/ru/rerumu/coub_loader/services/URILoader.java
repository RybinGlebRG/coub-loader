package ru.rerumu.coub_loader.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class URILoader {

    public void load(URI link, Path target) throws IOException {
        try (InputStream inputStream = link.toURL().openStream()) {
            Files.copy(inputStream, target);
        }
    }
}
