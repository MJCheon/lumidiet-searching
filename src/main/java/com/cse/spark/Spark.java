package com.cse.spark;

import com.cse.common.LogInstance;
import com.cse.network.SearchServer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * Created by bullet on 16. 10. 25.
 */
public class Spark implements Serializable {
    private static JavaSparkContext javaSparkContext;
    public static SparkConf sparkConf;
    public static final String APP_NAME = "lumidiet-searching";
    public static int NUM_CORE = 0;
    public static int EXECUTOR_MEMORY = 0;
    public static int DRIVER_MEMORY = 0;
    public static String MASTER = null;

    private static void init(){
        if(NUM_CORE != 0 && EXECUTOR_MEMORY != 0 && DRIVER_MEMORY != 0 && MASTER != null) {
            sparkConf = new SparkConf().setAppName(APP_NAME).setMaster(MASTER);
            sparkConf.set("spark.rpc.askTimeout", "120");
            sparkConf.set("spark.default.parallelism", Integer.toString(NUM_CORE));
            sparkConf.set("spark.driver.memory", Integer.toString(DRIVER_MEMORY) + "g");
            sparkConf.set("spark.executor.memory", Integer.toString(EXECUTOR_MEMORY) + "g");
            sparkConf.set("spark.executor.cores", Integer.toString(NUM_CORE));

            javaSparkContext = new JavaSparkContext(sparkConf);
        }
    }

    public static JavaSparkContext getJavaSparkContext(){
        return javaSparkContext;
    }

    public static void initSettings(String fileName){
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(fileName));
            DRIVER_MEMORY = Integer.parseInt(properties.getProperty("SPARK_DRIVER_MEMORY"));
            EXECUTOR_MEMORY = Integer.parseInt(properties.getProperty("SPARK_EXECUTOR_MEMORY"));
            NUM_CORE = Integer.parseInt(properties.getProperty("SPARK_NUM_CORE"));
            MASTER = "local["+Integer.toString(NUM_CORE)+"]";
            SparkJDBC.DB_HOST = properties.getProperty("DB_HOST");
            SparkJDBC.DB_PORT = properties.getProperty("DB_PORT");
            SparkJDBC.DB_NAME = properties.getProperty("DB_NAME");
            SparkJDBC.DB_USER = properties.getProperty("DB_USER");
            SparkJDBC.DB_PW = properties.getProperty("DB_PW");
            SearchServer.serverIP = properties.getProperty("SERVER_IP");
            SearchServer.serverPort = properties.getProperty("SERVER_PORT");
        }
        catch (Exception e){
            LogInstance.getLogger().debug(e.getMessage());
        }
        finally{
            init();
        }
    }
    public static void shutdown(){
        if(javaSparkContext!=null) {
            javaSparkContext.stop();
            javaSparkContext = null;
        }
    }
}
