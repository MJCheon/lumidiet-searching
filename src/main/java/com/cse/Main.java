package com.cse;

import com.cse.module.DocSearchModule;
import com.cse.network.SearchPage;
import com.cse.network.SearchServer;
import com.cse.spark.Spark;
import org.apache.spark.ml.feature.Word2VecModel;

/**
 * Created by bullet on 16. 11. 2.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //Initalizing DocSearchModule
        DocSearchModule.getInstance();

        final SearchServer server = new SearchServer("http://192.168.10.114:9999");
        server.startServer();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                server.shutdown();
                Spark.shutdown();
            }
        });
        Thread.currentThread().join();

    }
}
