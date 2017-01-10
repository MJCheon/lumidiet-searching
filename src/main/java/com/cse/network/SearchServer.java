package com.cse.network;


import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;


import java.io.IOException;
import java.net.URI;

/**
 * Created by leeyh on 2016. 11. 7..
 * Searching Server에 대한 클래스
 */
public class SearchServer {
    private static URI mainUri;
    private static ResourceConfig resourceConfig;
    private HttpServer httpServer;
    public static String serverIP = null;
    public static String serverPort = null;
    private static String host = null;

    public SearchServer(){
        if(serverIP != null && serverPort != null)
            initServer();

    }

    private static void initServer(){
        host = "http://"+serverIP+":"+serverPort;
        mainUri = URI.create(host);
        resourceConfig = new PackagesResourceConfig("com.cse.network");
    }

    public void startServer() throws IOException{
        if(host == null)
            initServer();
        httpServer = GrizzlyServerFactory.createHttpServer(mainUri,resourceConfig);
    }

    public void shutdown(){
        httpServer.stop();
    }
}
