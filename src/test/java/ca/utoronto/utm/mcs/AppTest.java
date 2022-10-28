package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// TODO Please Write Your Tests For CI/CD In This Class. You will see
// these tests pass/fail on github under github actions.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {
    final static String API_URL = "http://localhost:8080";

    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    @Test
    @Order(1)
    public void exampleTest() {
        assertTrue(true);
    }


    @Test
    @Order(2)
    public void addActorPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("name", "Denzel Washington")
                .put("actorID", "nm1001213");
        HttpResponse<String> confirmRes = sendRequest("/api/v1/addActor", "PUT", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    @Order(3)
    public void addActorFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("name", "Denzel Washington");

        HttpResponse<String> confirmRes = sendRequest("/api/v1/addActor", "PUT", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    @Order(4)
    public void addMoviePass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("name", "Parasite")
                .put("movieID", "nm7001453");
        HttpResponse<String> confirmRes = sendRequest("/api/v1/addMovie", "PUT", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    @Order(5)
    public void addMovieFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("name", "Parasite")
                .put("actorID", "nm7001453");
        HttpResponse<String> confirmRes = sendRequest("/api/v1/addMovie", "PUT", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }
    @Test
    @Order(6)
    public void addRelationshipPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("actorID", "nm1001213")
                .put("movieID", "nm7001453");
        HttpResponse<String> confirmRes = sendRequest("/api/v1/addRelationship", "PUT", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    @Order(7)
    public void addRelationshipFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("actorID", "nm1001213")
                .put("movieID", "nm700322321453");
        HttpResponse<String> confirmRes = sendRequest("/api/v1/addRelationship", "PUT", confirmReq.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }

    @Test
    @Order(8)
    public void getActorPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("actorID", "nm1001213");

        HttpResponse<String> confirmRes = sendRequest("/api/v1/getActor", "GET", confirmReq.toString());

        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    @Order(9)
    public void getActorFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("actorID", "nm1234567");

        HttpResponse<String> confirmRes = sendRequest("/api/v1/getActor", "GET", confirmReq.toString());

        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }
    @Test
    @Order(10)
    public void getMoviePass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("movieID", "nm7001453");

        HttpResponse<String> confirmRes = sendRequest("/api/v1/getMovie", "GET", confirmReq.toString());

        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    @Order(11)
    public void getMovieFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("movieID", "nm1234567");

        HttpResponse<String> confirmRes = sendRequest("/api/v1/getMovie", "GET", confirmReq.toString());

        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }
    @Test
    @Order(12)
    public void hasRelationshipPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                    .put("actorID", "nm1001213")
                .put("movieID", "nm7001453");
        HttpResponse<String> confirmRes = sendRequest("/api/v1/hasRelationship", "GET", confirmReq.toString());

        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    @Order(13)
    public void computeBaconNumber() throws JSONException, IOException, InterruptedException{
        JSONObject confirmReq = new JSONObject()
                .put("actorID", "nm1001213");

        HttpResponse<String> confirmRes = sendRequest("/api/v1/computeBaconNumber", "GET", confirmReq.toString());

        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }


}
