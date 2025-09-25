package pr.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class HttpUtil {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /**
     * Sendet einen HTTP-POST mit JSON-Body und gibt den JSON-Response als String zurück.
     *
     * @param url  Ziel-URL
     * @param json JSON-Body (null wird als leer gesendet)
     * @return Response-Body (JSON) als String
     * @throws IOException          bei I/O-Fehlern oder Nicht-2xx-Status
     * @throws InterruptedException wenn der Thread unterbrochen wird
     */
    public static String postHttp(String url, String json) throws IOException, InterruptedException {
        if (url == null || url.isBlank()) throw new IllegalArgumentException("URL darf nicht leer sein.");
        if (json == null) json = "";

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json, */*;q=0.8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = HTTP.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        int status = resp.statusCode();
        if (status < 200 || status >= 300) {
            throw new IOException("POST fehlgeschlagen (" + status + "): " + resp.body());
        }
        return resp.body();
    }
}
