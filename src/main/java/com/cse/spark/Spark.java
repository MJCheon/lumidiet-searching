package com.cse.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 25.
 */
public class Spark implements Serializable {
    private static JavaSparkContext javaSparkContext;
    public static SparkConf sparkConf;
    public static final String APP_NAME = "lumidiet-searching";
    public static final String LOCAL_MASTER = "local[4]";
    public static final String MASTER = "";
    public static final int NUM_CORE = 4;
    public static boolean isLocal = true;

    static{
        if(!isLocal) {
            sparkConf = new SparkConf().setAppName(APP_NAME).setMaster(MASTER);
            sparkConf.set("spark.default.parallelism", "16");
            sparkConf.set("spark.cores.max", "16");
            sparkConf.set("spark.driver.memory", "2g");
            sparkConf.set("spark.executor.memory", "16g");
            sparkConf.set("spark.executor.cores", "8");
        }
        else{
            sparkConf = new SparkConf().setAppName(APP_NAME).setMaster(LOCAL_MASTER);
            sparkConf.set("spark.rpc.askTimeout", "120");
            sparkConf.set("spark.default.parallelism", "4");
            sparkConf.set("spark.driver.memory", "8g");
            sparkConf.set("spark.executor.memory", "8g");
            sparkConf.set("spark.executor.cores", "4");
        }
        javaSparkContext = new JavaSparkContext(sparkConf);
    }

    public static JavaSparkContext getJavaSparkContext(){
        return javaSparkContext;
    }
}
