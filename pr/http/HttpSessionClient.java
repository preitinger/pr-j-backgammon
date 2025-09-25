package pr.http;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class HttpSessionClient {

    private final CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    private final HttpClient http = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .cookieHandler(cookieManager)
            .build();

    private final boolean manualCookieOverride;

    public HttpSessionClient() {
        this(false);
    }

    public HttpSessionClient(boolean manualCookieOverride) {
        this.manualCookieOverride = manualCookieOverride;
    }

    public String postJson(String url, String json) throws IOException, InterruptedException {
        URI uri = URI.create(url);
        HttpRequest.Builder b = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Accept", "application/json, */*;q=0.8")
                .POST(HttpRequest.BodyPublishers.ofString(json == null ? "" : json, StandardCharsets.UTF_8));

        if (manualCookieOverride) {
            String cookieHeader = buildCookieHeader(uri); // aktualisiert
            if (!cookieHeader.isEmpty()) {
                b.header("Cookie", cookieHeader);
            }
        }

        HttpResponse<String> resp = http.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IOException("POST " + url + " fehlgeschlagen (" + resp.statusCode() + "): " + resp.body());
        }
        return resp.body();
    }

    public void close() {
        this.http.close();
    }

    private String buildCookieHeader(URI uri) {
        CookieStore store = cookieManager.getCookieStore();

        // 1) Bevorzugt: die laut Java passenden Cookies nehmen
        List<HttpCookie> applicable = store.get(uri);
        if (!applicable.isEmpty()) {
            return applicable.stream()
                    .map(c -> c.getName() + "=" + c.getValue())
                    .collect(Collectors.joining("; "));
        }

        // 2) Fallback: „lässig“ matchen (um z. B. Secure/localhost.local-Fälle im Dev zu überbrücken)
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase();
        boolean isHttps = "https".equalsIgnoreCase(uri.getScheme());

        List<HttpCookie> all = store.getCookies();
        return all.stream()
                .filter(c -> relaxedMatch(c, host, isHttps))
                .map(c -> c.getName() + "=" + c.getValue())
                .distinct()
                .collect(Collectors.joining("; "));
    }

    private boolean relaxedMatch(HttpCookie c, String host, boolean isHttps) {
        // In Dev überschreiben wir Secure (sonst kämen wir ja gar nicht hierher)
        String domain = normalizeDomain(c.getDomain()); // kann „localhost.local“ → „localhost“ machen
        if (domain == null) {
            // Host-only Cookie: wir senden es für den aktuellen Host
            return true;
        }

        // localhost-Sonderfälle
        if (isLocalhostHost(host)) {
            if (domain.equals("localhost") || domain.equals("localhost.local") || domain.equals("localhost.localdomain")) {
                return true;
            }
        }

        // IPs: nur exakter Match
        if (isIp(host)) {
            return host.equalsIgnoreCase(domain);
        }

        // Standard-Domain-Suffix-Match
        return host.equalsIgnoreCase(domain) || host.endsWith("." + domain);
    }

    private String normalizeDomain(String domain) {
        if (domain == null || domain.isBlank()) return null;
        String d = domain.trim().toLowerCase();
        if (d.startsWith(".")) d = d.substring(1);
        // Optional: „localhost.local“ zu „localhost“ normalisieren
        if (d.equals("localhost.local")) return "localhost";
        return d;
    }

    private boolean isLocalhostHost(String host) {
        return "localhost".equals(host) || "127.0.0.1".equals(host) || "::1".equals(host);
    }

    private boolean isIp(String host) {
        // sehr einfache Erkennung
        return host.chars().allMatch(ch -> (ch >= '0' && ch <= '9') || ch == '.') || host.contains(":");
    }

    public CookieStore getCookieStore() {
        return cookieManager.getCookieStore();
    }
}
