package com.cse.spark;

import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Created by bullet on 16. 10. 25.
 */
public class SparkJDBC implements Serializable {
    private static final String JDBC_PREFIX = "jdbc:mysql://";
    public static String DB_HOST = null;
    public static String DB_PORT = null;
    public static String DB_NAME = null;
    public static String DB_USER = null;
    public static String DB_PW = null;
    public static Properties SQL_PROPERTIES;
    public static final String TABLE_PAGE = "page";
    public static final String TABLE_DOCWORD = "docword";
    private static String DB_URL;

    static{
        initSqlProperties();
    }

    private static void initSqlProperties(){
        if(DB_NAME != null && DB_HOST != null && DB_PORT != null && DB_USER != null && DB_PW != null) {
            SQL_PROPERTIES = new Properties();

            DB_URL = JDBC_PREFIX + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
            SQL_PROPERTIES.setProperty("url", DB_URL);
            SQL_PROPERTIES.setProperty("user", DB_USER);
            SQL_PROPERTIES.setProperty("password", DB_PW);
            SQL_PROPERTIES.setProperty("driver", "com.mysql.cj.jdbc.Driver");
            SQL_PROPERTIES.setProperty("validationQuery", "select 1");
            SQL_PROPERTIES.setProperty("useSSL", "false");
        }
    }

    public static SQLContext getSQLContext(){
        return new SQLContext(Spark.getJavaSparkContext());
    }

    public static DataFrame getSqlReader(String table){
        if(SQL_PROPERTIES == null)
            initSqlProperties();
        return getSQLContext().read().jdbc(DB_URL, table, SQL_PROPERTIES);
    }
}
