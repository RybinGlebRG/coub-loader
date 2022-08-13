package ru.rerumu.coub_loader.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rerumu.coub_loader.exceptions.CoubAlreadyProcessedException;
import ru.rerumu.coub_loader.models.Coub;
import ru.rerumu.coub_loader.repositories.LocalCoubRepository;
import ru.rerumu.coub_loader.repositories.URIRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CoubProcessorTest {

    @Mock
    LocalCoubRepository localCoubRepository;
    @Mock
    StreamMerger streamMerger;
    @Mock
    URIRepository uriRepository;
    @Mock
    Coub coub;

    @TempDir
    Path tempDir;


    @Test
    void process()throws Exception {
        Path video = Mockito.mock(Path.class);
        Path audioPath = Mockito.mock(Path.class);
        Path merged = Mockito.mock(Path.class);

        CoubProcessor coubProcessor = new CoubProcessor(
                localCoubRepository,
                streamMerger,
                tempDir,
                uriRepository
        );

        Mockito.when(localCoubRepository.contains(Mockito.any()))
                        .thenReturn(false);
        Mockito.when(uriRepository.saveVideo(Mockito.any(),Mockito.any()))
                        .thenReturn(video);
        Mockito.when(uriRepository.saveAudio(Mockito.any(), Mockito.any()))
                        .thenReturn(Optional.of(audioPath));
        Mockito.when(streamMerger.merge(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                        .thenReturn(merged);


        coubProcessor.process(coub);

        InOrder inOrder = Mockito.inOrder(localCoubRepository, streamMerger, uriRepository);

        inOrder.verify(localCoubRepository).contains(coub);
        inOrder.verify(uriRepository).saveVideo(coub,tempDir);
        inOrder.verify(uriRepository).saveAudio(coub,tempDir);
        inOrder.verify(streamMerger).merge(Mockito.anyLong(),Mockito.eq(video),Mockito.eq(audioPath));
        inOrder.verify(localCoubRepository).saveMerged(merged);
        inOrder.verify(localCoubRepository).saveMeta(coub);

        Mockito.verify(streamMerger, Mockito.never()).merge(Mockito.anyLong(),Mockito.any());
    }

    @Test
    void processNoAudio()throws Exception {
        Path video = Mockito.mock(Path.class);
        Path merged = Mockito.mock(Path.class);

        CoubProcessor coubProcessor = new CoubProcessor(
                localCoubRepository,
                streamMerger,
                tempDir,
                uriRepository
        );

        Mockito.when(localCoubRepository.contains(Mockito.any()))
                .thenReturn(false);
        Mockito.when(uriRepository.saveVideo(Mockito.any(),Mockito.any()))
                .thenReturn(video);
        Mockito.when(uriRepository.saveAudio(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(streamMerger.merge(Mockito.anyLong(), Mockito.any()))
                .thenReturn(merged);


        coubProcessor.process(coub);

        InOrder inOrder = Mockito.inOrder(localCoubRepository, streamMerger, uriRepository);

        inOrder.verify(localCoubRepository).contains(coub);
        inOrder.verify(uriRepository).saveVideo(coub,tempDir);
        inOrder.verify(uriRepository).saveAudio(coub,tempDir);
        inOrder.verify(streamMerger).merge(Mockito.anyLong(),Mockito.eq(video));
        inOrder.verify(localCoubRepository).saveMerged(merged);
        inOrder.verify(localCoubRepository).saveMeta(coub);

        Mockito.verify(streamMerger, Mockito.never()).merge(Mockito.anyLong(),Mockito.any(),Mockito.any());
    }

    @Test
    void processAlreadyProcessed()throws Exception {
        CoubProcessor coubProcessor = new CoubProcessor(
                localCoubRepository,
                streamMerger,
                tempDir,
                uriRepository
        );

        Mockito.when(localCoubRepository.contains(Mockito.any()))
                .thenReturn(true);

        Assertions.assertThrows(
                CoubAlreadyProcessedException.class,
                ()->coubProcessor.process(coub)
        );

        InOrder inOrder = Mockito.inOrder(localCoubRepository);

        inOrder.verify(localCoubRepository).contains(coub);

    }


    @Test
    void processClearTmp()throws Exception {
        Path video = Mockito.mock(Path.class);
        Path audioPath = Mockito.mock(Path.class);
        Path merged = Mockito.mock(Path.class);

        Files.createFile(tempDir.resolve("test.mp4"));
        Files.createFile(tempDir.resolve("test.mp3"));

        CoubProcessor coubProcessor = new CoubProcessor(
                localCoubRepository,
                streamMerger,
                tempDir,
                uriRepository
        );

        Mockito.when(localCoubRepository.contains(Mockito.any()))
                .thenReturn(false);
        Mockito.when(uriRepository.saveVideo(Mockito.any(),Mockito.any()))
                .thenReturn(video);
        Mockito.when(uriRepository.saveAudio(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(audioPath));
        Mockito.when(streamMerger.merge(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(merged);


        coubProcessor.process(coub);

        Assertions.assertFalse(Files.exists(tempDir.resolve("test.mp4")));
        Assertions.assertFalse(Files.exists(tempDir.resolve("test.mp3")));

    }
}