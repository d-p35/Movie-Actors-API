package ca.utoronto.utm.mcs;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.List;

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
//        query = "MERGE (n:actor {actorID: \"%s\"})\n" +
//                "ON CREATE\n" +
//                "SET n.name = \"%s\"\n" +
//                "ON MATCH\n" +
//                "SET n.name = \"%s\"\n";
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

}
