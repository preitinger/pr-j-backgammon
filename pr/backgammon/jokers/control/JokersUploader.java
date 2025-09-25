package pr.backgammon.jokers.control;

import java.io.IOException;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import pr.http.HttpSessionClient;

public class JokersUploader {
    /**
     * set to false in production!
     */
    private static final boolean MANUAL_COOKIE_OVERRIDE = true;
    private static final String GLOBAL_ACC_ID = "68ac2854f60b95ed625d6f22";
    private final HttpSessionClient client = new HttpSessionClient(MANUAL_COOKIE_OVERRIDE);

    private static final int VERSION_MAIN = 0, VERSION_SUB = 20;

    public JokersUploader() throws IOException, InterruptedException {
        var builder = addVersion(Json.createObjectBuilder()
                .add("type", "login")
                .add("req", Json.createObjectBuilder()
                        .add("email", "peter.reitinger@gmail.com")
                        .add("passwd", "a")
                        .add("force", true)));

        // String json = """
        // {
        // "type": "login",
        // "req": { "email": "peter.reitinger@gmail.com", "passwd": "a", "force": true
        // },
        // "version": { "main": 0, "sub": 20 }
        // }
        // """;
        var resp = client.postJson("http://localhost:3001/api/countRolls/myUser", builder.build().toString());
        System.out.println("resp: " + resp);
        var reader = Json.createReader(new StringReader(resp));
        
        JsonValue val = reader.readObject();
        switch (val.getValueType()) {
            case OBJECT:
                var valAsObj = val.asJsonObject();
                if (!"login".equals(valAsObj.getString("type"))) {
                    throw new IllegalStateException("Login was not successful");
                }
                var res = valAsObj.getJsonObject("res");
                if (!"success".equals(res.getString("type"))) {
                    throw new IllegalStateException("Login was not successful");
                }
                break;
            default:
                throw new IllegalStateException("Login was not successful");
        }
    }

    public void send(String player1, String player2, AllJokers jokers1, AllJokers jokers2)
            throws IOException, InterruptedException {

        client.postJson("http://localhost:3001/api/countJokers/upload",
                jokerUploadRequest(GLOBAL_ACC_ID, player1, player2, jokers1, jokers2));
    }

    public void close() {
        client.close();
    }

    private static String jokerUploadRequest(String globalAccId, String player1, String player2, AllJokers jokers1,
            AllJokers jokers2) {
        JsonArray jokers1Json = jokers1.toJson();
        JsonArray jokers2Json = jokers2.toJson();

        JsonObject json = addVersion(Json.createObjectBuilder()
                .add("globalAccId", globalAccId)
                .add("name1", player1)
                .add("name2", player2)
                .add("counts1", jokers1Json)
                .add("counts2", jokers2Json)).build();

        return json.toString();
    }

    private static JsonObjectBuilder addVersion(JsonObjectBuilder b) {
        b.add("version", Json.createObjectBuilder().add("main", 0).add("sub", 20));
        return b;
    }

    private void sendLogin() {

    }
}
