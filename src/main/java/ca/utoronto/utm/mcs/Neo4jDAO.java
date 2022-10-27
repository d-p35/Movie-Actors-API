package ca.utoronto.utm.mcs;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

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


    public void insertActor(String name, String actorID) {
        String query;
        query = "CREATE (n:actor {name: \"%s\", actorID: \"%s\"})";
        query = String.format(query, name, actorID);
        this.session.run(query);
        return;
    }

    public void insertMovie(String name, String movieID) {
        String query;
        query = "CREATE (n:movie {name: \"%s\", movieID: \"%s\"})";
        query = String.format(query, name, movieID);
        this.session.run(query);
        return;
    }

}
