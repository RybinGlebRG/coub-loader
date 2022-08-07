package ru.rerumu.coub_loader.api;

import org.json.JSONObject;
import ru.rerumu.coub_loader.exceptions.HttpErrorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class CoubLikesAPI {

    private final int page=1;
    private final int perPage=15;

    private final String cookie;

    public CoubLikesAPI(String cookie){
        this.cookie =cookie;
    }


    public void getTotalPages() throws URISyntaxException, IOException, InterruptedException, HttpErrorException {
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(
                new URI(String.format("https://coub.com/api/v2/timeline/likes?per_page=%s&page=%s&order_by=id",perPage,page))
                )
                .header("cookie",cookie)
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
    }
}
