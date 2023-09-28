package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String url;
    private final String API_TOKEN;
    private final HttpClient httpClient;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.url = url;
        httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url + "/register")).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        API_TOKEN = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI urlSave = URI.create(url + "/" + "save" + "/" + key + "?" + "API_TOKEN=" + API_TOKEN);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(urlSave).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
    }

    public String load(String key) throws IOException, InterruptedException {
        URI urlLoad = URI.create(url + "/" + "load" + "/" + key + "?" + "API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().uri(urlLoad).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
