package ru.rerumu.coub_loader.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class URILoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldLoad()throws Exception{
        Path srcPath = tempDir.resolve("test.txt");
        byte[] srcByte = new byte[1024];
        new Random().nextBytes(srcByte);
        Files.write(
                srcPath,
                srcByte,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        URI uri = new URI("file:///"+srcPath.toString().replace('\\','/'));

        URILoader uriLoader = new URILoader();

        Path targetPath = tempDir.resolve("target.txt");
        uriLoader.load(uri,targetPath);

        byte[] dstBytes = Files.readAllBytes(targetPath);

        Assertions.assertArrayEquals(srcByte,dstBytes);
    }

}