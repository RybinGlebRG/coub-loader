package ru.rerumu.coub_loader.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rerumu.coub_loader.api.CoubChannelAPI;
import ru.rerumu.coub_loader.factories.CoubChannelAPIFactory;
import ru.rerumu.coub_loader.models.Coub;

import java.io.IOException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LikesLoaderTest {

    @Mock
    CoubProcessor coubProcessor;

    @Mock
    CoubChannelAPIFactory coubChannelAPIFactory;

    @Mock
    CoubChannelAPI coubChannelAPI;

    @Test
    void shouldLoad() throws Exception{
        Mockito.when(coubChannelAPIFactory.getCoubChannelAPI(Mockito.any()))
                .thenReturn(coubChannelAPI);
        Mockito.when(coubChannelAPI.getTotalPages())
                .thenReturn(1);
        Coub coub = Mockito.mock(Coub.class);
        Coub coub1 = Mockito.mock(Coub.class);
        List<Coub> coubList = List.of(coub,coub1);

        Mockito.when(coubChannelAPI.getPage(Mockito.anyInt()))
                .thenReturn(coubList);

        LikesLoader likesLoader = new LikesLoader(coubProcessor, coubChannelAPIFactory);

        likesLoader.load("Test");

        Mockito.verify(coubChannelAPIFactory).getCoubChannelAPI("Test");
        Mockito.verify(coubChannelAPI).getTotalPages();
        Mockito.verify(coubChannelAPI).getPage(1);
        Mockito.verify(coubProcessor).process(coub);
        Mockito.verify(coubProcessor).process(coub1);
    }

    @Test
    void shouldLoadWithError()throws Exception{
        Mockito.when(coubChannelAPIFactory.getCoubChannelAPI(Mockito.any()))
                .thenReturn(coubChannelAPI);
        Mockito.when(coubChannelAPI.getTotalPages())
                .thenReturn(1);
        Coub coub = Mockito.mock(Coub.class);
        Coub coub1 = Mockito.mock(Coub.class);
        List<Coub> coubList = List.of(coub,coub1);

        Mockito.when(coubChannelAPI.getPage(Mockito.anyInt()))
                .thenReturn(coubList);
        Mockito.doThrow(new IOException()).when(coubProcessor).process(coub);

        LikesLoader likesLoader = new LikesLoader(coubProcessor, coubChannelAPIFactory);

        likesLoader.load("Test");

        Mockito.verify(coubChannelAPIFactory).getCoubChannelAPI("Test");
        Mockito.verify(coubChannelAPI).getTotalPages();
        Mockito.verify(coubChannelAPI).getPage(1);
        Mockito.verify(coubProcessor).process(coub);
        Mockito.verify(coubProcessor).process(coub1);
    }

}