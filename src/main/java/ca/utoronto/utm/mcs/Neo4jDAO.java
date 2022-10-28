package ca.utoronto.utm.mcs;

import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.List;
import java.util.Objects;

import static org.neo4j.driver.Values.parameters;

// All your database transactions or queries should
// go in this class
public class Neo4jDAO {
    // TODO Complete This Class
    public final Session session;
    public final Driver driver;




    public Neo4jDAO(Driver driver) {
        this.driver = driver;
        this.session = this.driver.session();
    }


    public void insertActor(String name, String actorID) throws UserException {
        String query;
        Transaction tx = session.beginTransaction();

        Result node_bool = tx.run("MATCH (n:actor {actorID: $x }) RETURN n as bool", parameters("x", actorID));
        System.out.println(node_bool.hasNext());
        if(node_bool.hasNext()) {
            tx.close();
            throw new UserException("Actor ID already exists");
        }
        System.out.println("Hello");
        query = "CREATE (n:actor {name: \"%s\", actorID: \"%s\"})";
        query = String.format(query, name, actorID);
        tx.close();
        this.session.run(query);

        return;
    }

    public void insertMovie(String name, String movieID) throws UserException {
        String query;
        Transaction tx = session.beginTransaction();
        Result node_bool = tx.run("MATCH (n:movie {movieID: $x }) RETURN n as bool", parameters("x", movieID));
        if(node_bool.hasNext()) {
            tx.close();
            throw new UserException("Movie ID already exists");
        }
        query = "CREATE (n:movie {name: \"%s\", movieID: \"%s\"})";
        query = String.format(query, name, movieID);
        tx.close();
        this.session.run(query);
        return;
    }


    public void insertRelationship(String actorID, String movieID) {
        String query;
        query = "MATCH (a:actor) Match (b:movie) WHERE a.actorID = \"%s\" AND b.movieID = \"%s\" CREATE (a)-[r:ACTED_IN]->(b) RETURN type(r)";
        query = String.format(query, actorID, movieID);
        this.session.run(query);
        return;
    }

        public JSONObject getMovie(String actorID) throws UserException, JSONException {
        String query;
        Transaction tx = session.beginTransaction();
        Result node_bool = tx.run("MATCH (n:actor {actorID: $x }) RETURN n as bool", parameters("x", actorID));
        if(!node_bool.hasNext()) {
            tx.close();
            throw new UserException("Actor ID doesn't exist");
        }
        Result result = tx.run("MATCH (a:actor {actorID: $x })-[r:ACTED_IN]-(b:movie) RETURN collect(b.movieID) as movieIDs, a.name as name", parameters("x", actorID));
        List<org.neo4j.driver.Record> recs = result.list();
//        System.out.println(recs.get(0).get("movieIDs"));
//        System.out.println(recs.get(0).get("name"));

        String name = recs.get(0).get("name").asString();
        List <Object> movieIds = recs.get(0).get("movieIDs").asList();

        JSONObject response = new JSONObject();

        response.put("actorID", actorID);
        response.put("name", name);
        response.put("movies", movieIds);

//        System.out.println(response);
        tx.close();

        return response;
    }
}
