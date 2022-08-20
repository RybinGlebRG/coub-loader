package ru.rerumu.coub_loader.api;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.rerumu.coub_loader.exceptions.HttpErrorException;
import ru.rerumu.coub_loader.models.Coub;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CoubChannelAPI {

    private final int page=1;
    private final int perPage=25;

    private final String channel;

    public CoubChannelAPI(String channel){
        this.channel = channel;
    }


    public int getTotalPages() throws URISyntaxException, IOException, InterruptedException, HttpErrorException {
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(
                        new URI(String.format("https://coub.com/api/v2/timeline/channel/%s?per_page=%d&page=%d&order_by=id",
                                channel,
                                perPage,
                                1))
                )
                .GET()
                .build();

        HttpResponse<String> httpResponse = HttpClient
                .newBuilder()
                .build()
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = httpResponse.statusCode();
        if (statusCode != 200){
            throw  new HttpErrorException();
        }
        String body = httpResponse.body();
        JSONObject jsonObject = new JSONObject(body);
        int totalPages = jsonObject.getInt("total_pages");
        return totalPages;
    }

    public List<Coub> getPage(int n) throws URISyntaxException, IOException, InterruptedException, HttpErrorException {
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(
                        new URI(String.format("https://coub.com/api/v2/timeline/channel/%s?per_page=%d&page=%d&order_by=id",
                                channel,
                                perPage,
                                n))
                )
                .GET()
                .build();

        HttpResponse<String> httpResponse = HttpClient
                .newBuilder()
                .build()
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
        int statusCode = httpResponse.statusCode();
        if (statusCode != 200){
            throw  new HttpErrorException();
        }
        String body = httpResponse.body();
        JSONObject page = new JSONObject(body);
        JSONArray coubs = page.getJSONArray("coubs");
        List<Coub> coubList = new ArrayList<>();
        for (int i=0;i<coubs.length();i++){
            coubList.add(new Coub(coubs.getJSONObject(i)));
        }

        return coubList;
    }
}
