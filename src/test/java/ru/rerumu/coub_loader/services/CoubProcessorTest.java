package ru.rerumu.coub_loader.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rerumu.coub_loader.exceptions.CoubAlreadyProcessedException;
import ru.rerumu.coub_loader.models.Coub;
import ru.rerumu.coub_loader.repositories.AudioRepository;
import ru.rerumu.coub_loader.repositories.CoubRepository;
import ru.rerumu.coub_loader.repositories.VideoRepository;

import java.nio.file.Path;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CoubProcessorTest {

    @Mock
    CoubRepository coubRepository;
    @Mock
    StreamMerger streamMerger;
    @Mock
    AudioRepository audioRepository;
    @Mock
    VideoRepository videoRepository;
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
                coubRepository,
                streamMerger,
                audioRepository,
                videoRepository
        );

        Mockito.when(coubRepository.contains(Mockito.any()))
                        .thenReturn(false);
        Mockito.when(videoRepository.getVideo(Mockito.any()))
                        .thenReturn(video);
        Mockito.when(audioRepository.getAudio(Mockito.any()))
                        .thenReturn(Optional.of(audioPath));
        Mockito.when(streamMerger.merge(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                        .thenReturn(merged);


        coubProcessor.process(coub);

        InOrder inOrder = Mockito.inOrder(coubRepository, streamMerger, audioRepository, videoRepository);

        inOrder.verify(coubRepository).contains(coub);
        inOrder.verify(videoRepository).getVideo(coub);
        inOrder.verify(audioRepository).getAudio(coub);
        inOrder.verify(streamMerger).merge(Mockito.anyLong(),Mockito.eq(video),Mockito.eq(audioPath));
        inOrder.verify(coubRepository).saveMerged(merged);
        inOrder.verify(coubRepository).saveMeta(coub);

        Mockito.verify(streamMerger, Mockito.never()).merge(Mockito.anyLong(),Mockito.any());
    }

    @Test
    void processNoAudio()throws Exception {
        Path video = Mockito.mock(Path.class);
        Path merged = Mockito.mock(Path.class);

        CoubProcessor coubProcessor = new CoubProcessor(
                coubRepository,
                streamMerger,
                audioRepository,
                videoRepository
        );

        Mockito.when(coubRepository.contains(Mockito.any()))
                .thenReturn(false);
        Mockito.when(videoRepository.getVideo(Mockito.any()))
                .thenReturn(video);
        Mockito.when(audioRepository.getAudio(Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(streamMerger.merge(Mockito.anyLong(), Mockito.any()))
                .thenReturn(merged);


        coubProcessor.process(coub);

        InOrder inOrder = Mockito.inOrder(coubRepository, streamMerger, audioRepository, videoRepository);

        inOrder.verify(coubRepository).contains(coub);
        inOrder.verify(videoRepository).getVideo(coub);
        inOrder.verify(audioRepository).getAudio(coub);
        inOrder.verify(streamMerger).merge(Mockito.anyLong(),Mockito.eq(video));
        inOrder.verify(coubRepository).saveMerged(merged);
        inOrder.verify(coubRepository).saveMeta(coub);

        Mockito.verify(streamMerger, Mockito.never()).merge(Mockito.anyLong(),Mockito.any(),Mockito.any());
    }

    @Test
    void processAlreadyProcessed()throws Exception {
        CoubProcessor coubProcessor = new CoubProcessor(
                coubRepository,
                streamMerger,
                audioRepository,
                videoRepository
        );

        Mockito.when(coubRepository.contains(Mockito.any()))
                .thenReturn(true);

        Assertions.assertThrows(
                CoubAlreadyProcessedException.class,
                ()->coubProcessor.process(coub)
        );

        InOrder inOrder = Mockito.inOrder(coubRepository);

        inOrder.verify(coubRepository).contains(coub);

    }
}