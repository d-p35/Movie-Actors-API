package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
        String originalURI = "/api/v1";
        String uri = exchange.getRequestURI().toString();
        String body = Utils.convert(exchange.getRequestBody());


        if(uri.equals(originalURI + "/getActor")){
            this.getActor(exchange, body);
        }
        else if(uri.equals(originalURI + "/getMovie")){
            this.getMovie(exchange, body);
        }
        else if(uri.equals(originalURI + "/hasRelationship")){
            this.hasRelationship(exchange, body);
        }
        else if(uri.equals(originalURI + "/computeBaconPath")){
            this.getBaconPath(exchange, body);
        }
        else if (uri.equals(originalURI+"/computeBaconNumber")){
            this.computeBaconNumber(exchange,body);
        }
        

    }

    public void handlePost(HttpExchange exchange) throws IOException, JSONException {

    }
    public void handlePut(HttpExchange exchange) throws IOException, JSONException, UserException {
        String originalURI = "/api/v1";
        String uri = exchange.getRequestURI().toString();
        String body = Utils.convert(exchange.getRequestBody());

        if(uri.equals(originalURI + "/addActor")) {
            addActor(exchange,body);
        } else if (uri.equals(originalURI + "/addMovie")) {
            addMovie(exchange,body);
        }
        else if (uri.equals(originalURI+"/addRelationship")){
            addRelationship(exchange,body);
        }
    }
    public void addActor (HttpExchange exchange,String body) throws IOException {
        try {
            JSONObject deserialized = new JSONObject(body);

            String name, actorId;

            if (deserialized.has("name") && deserialized.has("actorId")
            ) {
                name = deserialized.getString("name");
                actorId = deserialized.getString("actorId");

            } else {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
                this.dao.insertActor(name, actorId);
            }
            catch (UserException u){
                exchange.sendResponseHeaders(400, -1);
                return;
            }
            catch (Exception e) {
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

    public void addMovie(HttpExchange exchange, String body) throws IOException {
        try {
            JSONObject deserialized = new JSONObject(body);

            String name, movieId;

            if (deserialized.has("name") && deserialized.has("movieId")
            ) {
                name = deserialized.getString("name");
                movieId = deserialized.getString("movieId");

            } else {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
                this.dao.insertMovie(name, movieId);
            }
            catch (UserException u){
                exchange.sendResponseHeaders(400, -1);
                return;
            }
            catch (Exception e) {
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

    public void addRelationship(HttpExchange exchange, String body) throws IOException {
        try {
            JSONObject deserialized = new JSONObject(body);

            String actorID, movieId;

            if (deserialized.has("actorId") && deserialized.has("movieId")
            ) {
                actorID = deserialized.getString("actorId");
                movieId = deserialized.getString("movieId");

            } else {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
                this.dao.insertRelationship(actorID, movieId);
            }
            catch (UserException u) {
                if (u.message.equals("MovieID or actorID does not exists")){
                    exchange.sendResponseHeaders(404, -1);
                }
                else if (u.message.equals("Relationship already exists")) {
                    exchange.sendResponseHeaders(400, -1);
                }
                return;
            }
            catch (Exception e) {
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

        public void getActor(HttpExchange exchange, String body) throws IOException{
        try {
            JSONObject deserialized = new JSONObject(body);


            String actorID;

            if (deserialized.has("actorId")) {

                actorID = deserialized.getString("actorId");

            } else {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
               JSONObject r = this.dao.getActor(actorID);
               String response = r.toString();
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            catch (UserException u){
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                e.printStackTrace();
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    public void getMovie(HttpExchange exchange, String body) throws IOException{
        try {
            JSONObject deserialized = new JSONObject(body);


            String movieID;

            if (deserialized.has("movieId")) {

                movieID = deserialized.getString("movieId");

            } else {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
                JSONObject r = this.dao.getMovie(movieID);
                String response = r.toString();
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            catch (UserException u){
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                e.printStackTrace();
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    public void hasRelationship(HttpExchange exchange, String body) throws IOException{
        try {
            JSONObject deserialized = new JSONObject(body);

            String actorID, movieId;

            if (deserialized.has("actorId") && deserialized.has("movieId")
            ) {
                actorID = deserialized.getString("actorId");
                movieId = deserialized.getString("movieId");

            } else {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
              JSONObject r =  this.dao.getRelationship(actorID, movieId);
              String response = r.toString();
              exchange.sendResponseHeaders(200, response.length());
              OutputStream os = exchange.getResponseBody();
              os.write(response.getBytes());
              os.close();
            }
            catch (UserException u) {

                    exchange.sendResponseHeaders(404, -1);

                return;
            }
            catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                e.printStackTrace();
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    public void computeBaconNumber(HttpExchange exchange, String body) throws IOException {
        try {
            JSONObject deserialized = new JSONObject(body);

            String actorID;

            if (deserialized.has("actorId")) {
                actorID = deserialized.getString("actorId");
            } else {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
                JSONObject r =  this.dao.computeBaconNumber(actorID);
                String response = r.toString();
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            catch (UserException u) {

                    exchange.sendResponseHeaders(404, -1);

                return;
            }
            catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                e.printStackTrace();
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }


    public void getBaconPath(HttpExchange exchange, String body) throws IOException{
        try {
            JSONObject deserialized = new JSONObject(body);


            String actorID;

            if (deserialized.has("actorId")) {

                actorID = deserialized.getString("actorId");

            } else {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            try {
                JSONObject r = this.dao.getBaconPath(actorID);
                String response = r.toString();
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            catch (UserException u){
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                e.printStackTrace();
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }
    }

