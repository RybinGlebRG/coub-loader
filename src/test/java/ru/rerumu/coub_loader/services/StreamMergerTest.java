package ru.rerumu.coub_loader.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rerumu.coub_loader.api.FFmpegAPI;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ExtendWith(MockitoExtension.class)
class StreamMergerTest {
    @Mock
    FFmpegAPI ffmpegAPI;

    @TempDir
    Path tmpDir;

    @Test
    void shouldMerge() throws Exception {
        Path video = Paths.get("/tst.mp4");
        Path audio = Paths.get("/tst.mp3");
        StreamMerger streamMerger = new StreamMerger(tmpDir, ffmpegAPI);


        Mockito.doAnswer(invocationOnMock -> Files.createFile(tmpDir.resolve("temp_res.mkv")))
                .when(ffmpegAPI).merge(Mockito.any(), Mockito.any(), Mockito.any());

        Path res = streamMerger.merge(1111L, video, audio);

        Files.exists(tmpDir.resolve("conf.txt"));
        Assertions.assertEquals(tmpDir.resolve("1111.mkv"),res);

        String shouldString = String.format("file 'file:%s'\n", video.toString()).repeat(1000);
        String resString = Files.readString(tmpDir.resolve("conf.txt"), StandardCharsets.UTF_8);
        Assertions.assertEquals(shouldString,resString);

        Mockito.verify(ffmpegAPI).merge(tmpDir.resolve("conf.txt"), audio, tmpDir.resolve("temp_res.mkv"));
    }

    @Test
    void shouldMergeNoAudio() throws Exception {
        Path video = Paths.get("/tst.mp4");
        StreamMerger streamMerger = new StreamMerger(tmpDir, ffmpegAPI);

        Mockito.doAnswer(invocationOnMock -> Files.createFile(tmpDir.resolve("temp_res.mkv")))
                .when(ffmpegAPI).merge(Mockito.any(), Mockito.any());

        Path res = streamMerger.merge(1111L, video);

        Files.exists(tmpDir.resolve("conf.txt"));
        Assertions.assertEquals(tmpDir.resolve("1111.mkv"),res);

        String shouldString = String.format("file 'file:%s'\n", video.toString());
        String resString = Files.readString(tmpDir.resolve("conf.txt"), StandardCharsets.UTF_8);
        Assertions.assertEquals(shouldString,resString);

        Mockito.verify(ffmpegAPI).merge(tmpDir.resolve("conf.txt"), tmpDir.resolve("temp_res.mkv"));
    }

    @Test
    void shouldClear()throws Exception{
        Files.createFile(tmpDir.resolve("test.txt"));
        Files.createFile(tmpDir.resolve("test_res.mkv"));
        Files.createFile(tmpDir.resolve("test.mkv"));


        Path video = Paths.get("/tst.mp4");
        Path audio = Paths.get("/tst.mp3");
        StreamMerger streamMerger = new StreamMerger(tmpDir, ffmpegAPI);


        Mockito.doAnswer(invocationOnMock -> Files.createFile(tmpDir.resolve("temp_res.mkv")))
                .when(ffmpegAPI).merge(Mockito.any(), Mockito.any(), Mockito.any());

        Path res = streamMerger.merge(1111L, video, audio);

        Assertions.assertFalse(Files.exists(tmpDir.resolve("test.txt")));
        Assertions.assertFalse(Files.exists(tmpDir.resolve("test_res.mkv")));
        Assertions.assertFalse(Files.exists(tmpDir.resolve("test.mkv")));
    }

}