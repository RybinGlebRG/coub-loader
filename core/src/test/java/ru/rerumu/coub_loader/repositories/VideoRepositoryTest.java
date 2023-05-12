package ru.rerumu.coub_loader.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rerumu.coub_loader.models.Coub;
import ru.rerumu.coub_loader.services.URILoader;

import java.net.URI;
import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
class VideoRepositoryTest {

    @Mock
    URILoader uriLoader;
    @Mock
    Coub coub;

    @TempDir
    Path tempDir;

    @Test
    void shouldGetVideo()throws Exception{
        URI videoLink = new URI("https://example.com/test-video.mp4");

        Mockito.when(coub.getVideoLink()).thenReturn(videoLink);
        Mockito.when(coub.getId()).thenReturn(1111L);

        VideoRepository videoRepository = new VideoRepository(tempDir,uriLoader);
        Path videoPath = videoRepository.getVideo(coub);

        Path shouldTarget = tempDir.resolve("1111.mp4");
        Mockito.verify(uriLoader).load(videoLink,shouldTarget);

        Assertions.assertEquals(shouldTarget,videoPath);
    }

}