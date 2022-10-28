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


    public void insertRelationship(String actorID, String movieID) throws UserException {
        String query;
        Transaction tx = session.beginTransaction();
        Result node_bool = tx.run("MATCH (n:movie {movieID: $x }) RETURN n as bool", parameters("x", movieID));
        Result node_bool2 = tx.run("MATCH (n:actor {actorID: $x }) RETURN n as bool", parameters("x", actorID));
        if((!node_bool.hasNext()) || (!node_bool2.hasNext())) {
            tx.close();
            throw new UserException("MovieID or actorID does not exists");
        }

        Result result = tx.run("MATCH (a:actor {actorID: $x })-[r:ACTED_IN]-(b:movie {movieID: $y}) RETURN collect(b.movieID) as movieIDs", parameters("x", actorID, "y",movieID ));
        List<Record> recs = result.list();
        List <Object> movieIds = recs.get(0).get(0).asList();
        System.out.println(movieIds.size());
        if (movieIds.size()==1){
            tx.close();
            throw new UserException("Relationship already exists");
        }

        query = "MATCH (a:actor) Match (b:movie) WHERE a.actorID = \"%s\" AND b.movieID = \"%s\" CREATE (a)-[r:ACTED_IN]->(b) RETURN type(r)";
        query = String.format(query, actorID, movieID);
        tx.close();
        this.session.run(query);
        return;
    }

        public JSONObject getActor(String actorID) throws UserException, JSONException {
        Transaction tx = session.beginTransaction();
        Result node_bool = tx.run("MATCH (n:actor {actorID: $x }) RETURN n.name as name", parameters("x", actorID));
            List<org.neo4j.driver.Record> re = node_bool.list();
//            System.out.println(re);
            if(re.isEmpty()){
            tx.close();
            throw new UserException("Actor ID doesn't exist");
        }
            String name = re.get(0).get("name").asString();
            System.out.println(name);

        Result result = tx.run("MATCH (a:actor {actorID: $x })-[r:ACTED_IN]-(b:movie) RETURN collect(b.movieID) as movieIDs", parameters("x", actorID));
        List<org.neo4j.driver.Record> recs = result.list();
        System.out.println(recs.get(0).get("movieIDs"));
        List<Object> movieIds = recs.get(0).get("movieIDs").asList();
//        System.out.println(recs.get(0).get("name"));
//        String name = recs.get(0).get("name").asString();
//        List<Object> movieIds;
//        movieIds.clear();
//        if(!recs.isEmpty()) {
//            movieIds = recs.get(0).get("movieIDs").asList();
//        }


        JSONObject response = new JSONObject();

        response.put("actorID", actorID);
        response.put("name", name);
        response.put("movies", movieIds);
//
////        System.out.println(response);
        tx.close();
//
        return response;
//            return new JSONObject();
    }

    public JSONObject getMovie(String movieID) throws UserException, JSONException {
        Transaction tx = session.beginTransaction();
        Result node_bool = tx.run("MATCH (n:movie {movieID: $x }) RETURN n.name as name", parameters("x", movieID));
        List<org.neo4j.driver.Record> re = node_bool.list();
//            System.out.println(re);
        if(re.isEmpty()){
            tx.close();
            throw new UserException("Movie ID doesn't exist");
        }

        String name = re.get(0).get("name").asString();
        System.out.println(name);

        Result result = tx.run("MATCH (a:actor )-[r:ACTED_IN]-(b:movie {movieID: $x } ) RETURN collect(a.actorID) as actorIDs", parameters("x", movieID));
        List<org.neo4j.driver.Record> recs = result.list();
        System.out.println(recs.get(0).get("actorIDs"));
        List<Object> actorIDs = recs.get(0).get("actorIDs").asList();

        JSONObject response = new JSONObject();

        response.put("movieID", movieID);
        response.put("name", name);
        response.put("actors", actorIDs);

        tx.close();
        return response;

    }


    public JSONObject getRelationship(String actorID,String movieID) throws UserException, JSONException{
        String query;
        Transaction tx = session.beginTransaction();
        Result node_bool = tx.run("MATCH (n:movie {movieID: $x }) RETURN n as bool", parameters("x", movieID));
        Result node_bool2 = tx.run("MATCH (n:actor {actorID: $x }) RETURN n as bool", parameters("x", actorID));
        if((!node_bool.hasNext()) || (!node_bool2.hasNext())) {
            tx.close();
            throw new UserException("MovieID or actorID does not exists");
        }

        Result result = tx.run("MATCH (a:actor {actorID: $x })-[r:ACTED_IN]-(b:movie {movieID: $y}) RETURN collect(b.movieID) as movieIDs", parameters("x", actorID, "y",movieID ));
        List<Record> recs = result.list();
        List <Object> movieIds = recs.get(0).get(0).asList();
        System.out.println(movieIds.size());

        if (movieIds.size()==1){
            tx.close();
            JSONObject response = new JSONObject();

            response.put("actorID", actorID);
            response.put("movieID", movieID);
            response.put("hasRelationship", "true");
            return response;
        }
        else {
            tx.close();

            JSONObject response = new JSONObject();

            response.put("actorID", actorID);
            response.put("movieID", movieID);
            response.put("hasRelationship", "false");
            return response;
        }


    }

    public JSONObject computeBaconNumber(String actorID ) throws JSONException, UserException {

        if (actorID == "nm0000102"){
            JSONObject response = new JSONObject();

            response.put("baconNumber", 0);

            return response;
        }


        String query;
        Transaction tx = session.beginTransaction();
        Result node_bool2 = tx.run("MATCH (n:actor {actorID: $x }) RETURN n as bool", parameters("x", actorID));

        if((!node_bool2.hasNext())) {
            tx.close();
            throw new UserException("actorID does not exists");
        }
        Result re = tx.run(  "MATCH (a:actor { actorID:\"nm0000102\" }),(b:actor { actorID: $x }), p = shortestPath((a)-[*]-(b)) RETURN relationships(p) ", parameters("x", actorID ));
        List<org.neo4j.driver.Record> recs1 = re.list();
        List <Object> relationship = recs1.get(0).get(0).asList();
            int baconNumber = relationship.size()/2;
        JSONObject response = new JSONObject();

        response.put("baconNumber", baconNumber);
        tx.close();
        return response;



    }


}
