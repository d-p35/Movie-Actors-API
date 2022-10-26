package ca.utoronto.utm.mcs;

import dagger.Module;
import dagger.Provides;
import io.github.cdimascio.dotenv.Dotenv;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

@Module
public class ReqHandlerModule {
    @Provides
    public Neo4jDAO provideNeo4jDAO(Driver driver){
        return new Neo4jDAO(driver);
    }
    @Provides
    public Driver provideDriver(){

        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
         final String uriDb = "bolt://"+addr+":7687";
         final String username = "neo4j";
         final String password = "123456";
        return GraphDatabase.driver(uriDb, AuthTokens.basic(username,password));
    }
    // TODO Complete This Module
}
