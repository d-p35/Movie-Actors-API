package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;
import dagger.Module;
import dagger.Provides;

import java.io.IOException;
import java.net.InetSocketAddress;

@Module
public class ServerModule {
    // TODO Complete This Module
    @Provides
    public HttpServer provideHttpServer() {

        try {
            return HttpServer.create( new InetSocketAddress("0.0.0.0",8080) ,0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
