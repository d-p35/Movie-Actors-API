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
        String body = Utils.convert(exchange.getRequestBody());
        try {
            JSONObject deserialized = new JSONObject(body);

            String name, pid, description, type1, type2;

            if (deserialized.length() == 5 && deserialized.has("name") && deserialized.has("pid") &&
                    deserialized.has("description") && deserialized.has("type1") && deserialized.has("type2")) {
                name = deserialized.getString("name");
                pid = deserialized.getString("pid");
                description = deserialized.getString("description");
                type1 = deserialized.getString("type1");
                type2 = deserialized.getString("type2");
            } else {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
                this.dao.insertPokemon(name, pid, description, type1, type2);
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

