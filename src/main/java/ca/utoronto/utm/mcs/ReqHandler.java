package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

public class ReqHandler implements HttpHandler {
    public Neo4jDAO dao;

    @Inject
    public ReqHandler(Neo4jDAO neo4jDAO) {
        this.dao = neo4jDAO;
    }
    // TODO Complete This Class

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    this.handleGet(exchange);
                    break;
                case "POST":
                    this.handlePost(exchange);
                    break;
                case "PUT":
                    this.handlePut(exchange);
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleGet(HttpExchange exchange) throws IOException, JSONException {

    }

    public void handlePost(HttpExchange exchange) throws IOException, JSONException {

    }
    public void handlePut(HttpExchange exchange) throws IOException, JSONException {
        String originalURI = "/api/v1";
        String uri = exchange.getRequestURI().toString();
        String body = Utils.convert(exchange.getRequestBody());

        if(uri.equals(originalURI + "/addActor")) {
            try {
                JSONObject deserialized = new JSONObject(body);

                String name, actorId;

                if (deserialized.has("name") && deserialized.has("actorID")
                ) {
                    name = deserialized.getString("name");
                    actorId = deserialized.getString("actorID");

                } else {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                try {
                    this.dao.insertActor(name, actorId);
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                    e.printStackTrace();
                    return;
                }
                exchange.sendResponseHeaders(200, -1);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }


    }

