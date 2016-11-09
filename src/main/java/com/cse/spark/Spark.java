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
    public static final String APP_NAME = "lumidiet-learning";
    public static final int NUM_CORE = 16;
    public static final int MEMORY = 32;
    public static final String MASTER = "local["+Integer.toString(NUM_CORE)+"]";

    static{
        sparkConf = new SparkConf().setAppName(APP_NAME).setMaster(MASTER);
        sparkConf.set("spark.rpc.askTimeout", "120");
        sparkConf.set("spark.default.parallelism", Integer.toString(NUM_CORE));
        sparkConf.set("spark.driver.memory", Integer.toString(MEMORY)+"g");
        sparkConf.set("spark.executor.memory", Integer.toString(MEMORY)+"g");
        sparkConf.set("spark.executor.cores", Integer.toString(NUM_CORE));

        javaSparkContext = new JavaSparkContext(sparkConf);
    }

    public static JavaSparkContext getJavaSparkContext(){
        return javaSparkContext;
    }

    public static void shutdown(){
        if(javaSparkContext!=null) {
            javaSparkContext.stop();
            javaSparkContext = null;
        }
    }
}
