package ru.rerumu.coub_loader.factories;

import ru.rerumu.coub_loader.api.CoubChannelAPI;

public class CoubChannelAPIFactory {

    public CoubChannelAPI getCoubChannelAPI(String channel){
        return new CoubChannelAPI(channel);
    }
}
