package ru.rerumu.coub_loader.models;

import org.json.JSONObject;
import ru.rerumu.coub_loader.exceptions.NoAudioLinkException;

import java.net.URI;
import java.net.URISyntaxException;

public class Coub {

    private final JSONObject raw;

    public Coub(JSONObject raw){
        this.raw = raw;
    }

    public long getId(){
        return raw.getLong("id");
    }

    public URI getVideoLink() throws URISyntaxException {
        JSONObject video = raw
                .getJSONObject("file_versions")
                .getJSONObject("html5")
                .getJSONObject("video");
        URI uri;
        if (video.has("higher")){
            uri = new URI(video.getJSONObject("higher").getString("url"));
        } else {
            uri = new URI(video.getJSONObject("high").getString("url"));
        }
        return uri;
    }

    public URI getAudioLink() throws URISyntaxException, NoAudioLinkException {
        JSONObject html5 = raw
                .getJSONObject("file_versions")
                .getJSONObject("html5");

        if (html5.has("audio")){
            return new URI(html5.getJSONObject("audio").getJSONObject("high").getString("url"));
        }
        throw new NoAudioLinkException();
    }

    public JSONObject getRaw() {
        return raw;
    }
}
