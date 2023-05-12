package ru.rerumu.coub_loader.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
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
class AudioRepositoryTest {

    @Mock
    URILoader uriLoader;
    @Mock
    Coub coub;

    @TempDir
    Path tempDir;

    @Test
    void getAudio()throws Exception {
        URI audioLink = new URI("https://example.com/test-audio.mp3");

        Mockito.when(coub.getAudioLink()).thenReturn(audioLink);
        Mockito.when(coub.getId()).thenReturn(1111L);

        AudioRepository audioRepository = new AudioRepository(tempDir, uriLoader);
        Optional<Path> audioPath = audioRepository.getAudio(coub);

        Path shouldTarget = tempDir.resolve("1111.mp3");
        Mockito.verify(uriLoader).load(audioLink,shouldTarget);

        Assertions.assertTrue(audioPath.isPresent());
        Assertions.assertEquals(shouldTarget,audioPath.get());
    }

    @Test
    void getAudioNoAudio()throws Exception {
        Mockito.when(coub.getAudioLink()).thenThrow(new NoAudioLinkException());

        AudioRepository audioRepository = new AudioRepository(tempDir, uriLoader);
        Optional<Path> audioPath = audioRepository.getAudio(coub);

        Assertions.assertFalse(audioPath.isPresent());
    }

}