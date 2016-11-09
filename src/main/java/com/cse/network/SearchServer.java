package com.cse.network;


import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;


import java.io.IOException;
import java.net.URI;

/**
 * Created by leeyh on 2016. 11. 7..
 */
public class SearchServer {
    private URI mainUri;
    private ResourceConfig resourceConfig;
    private HttpServer httpServer;

    public SearchServer(String host){
        mainUri = URI.create(host);
        resourceConfig = new PackagesResourceConfig("com.cse.network");
    }

    public void startServer() throws IOException{
        httpServer = GrizzlyServerFactory.createHttpServer(mainUri,resourceConfig);
    }

    public void shutdown(){
        httpServer.stop();
    }
}
