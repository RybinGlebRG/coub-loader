package ru.rerumu.coub_loader.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rerumu.coub_loader.exceptions.NoAudioLinkException;
import ru.rerumu.coub_loader.models.Coub;
import ru.rerumu.coub_loader.services.URILoader;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class URIRepositoryTest {

    @Mock
    URILoader uriLoader;
    @Mock
    Coub coub;

    @TempDir
    Path tempDir;

    @Test
    void saveVideo()throws Exception {
        URI videoLink = new URI("https://example.com/test-video.mp4");

        Mockito.when(coub.getVideoLink()).thenReturn(videoLink);
        Mockito.when(coub.getId()).thenReturn(1111L);

        URIRepository uriRepository = new URIRepository(uriLoader);
        Path videoPath = uriRepository.saveVideo(coub, tempDir);

        Path shouldTarget = tempDir.resolve("1111.mp4");
        Mockito.verify(uriLoader).load(videoLink,shouldTarget);

        Assertions.assertEquals(shouldTarget,videoPath);
    }

    @Test
    void saveAudio()throws Exception {
        URI audioLink = new URI("https://example.com/test-audio.mp3");

        Mockito.when(coub.getAudioLink()).thenReturn(audioLink);
        Mockito.when(coub.getId()).thenReturn(1111L);

        URIRepository uriRepository = new URIRepository(uriLoader);
        Optional<Path> audioPath = uriRepository.saveAudio(coub, tempDir);

        Path shouldTarget = tempDir.resolve("1111.mp3");
        Mockito.verify(uriLoader).load(audioLink,shouldTarget);

        Assertions.assertTrue(audioPath.isPresent());
        Assertions.assertEquals(shouldTarget,audioPath.get());
    }

    @Test
    void saveAudioNoAudio()throws Exception {
        Mockito.when(coub.getAudioLink()).thenThrow(new NoAudioLinkException());

        URIRepository uriRepository = new URIRepository(uriLoader);
        Optional<Path> audioPath = uriRepository.saveAudio(coub, tempDir);

        Assertions.assertFalse(audioPath.isPresent());
    }
}