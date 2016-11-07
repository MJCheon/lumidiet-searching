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
    private static final String DB_HOST = "52.78.215.248";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "lumi";
    public static final String DB_USER = "root";
    public static final String DB_PW = "cmj92";
    public static Properties SQL_PROPERTIES;
    public static final String TABLE_PAGE = "page";
    public static final String TABLE_DOCWORD = "docword";
    private static String DB_URL;

    static{
        initSqlProperties();
    }

    private static void initSqlProperties(){
        SQL_PROPERTIES = new Properties();

        DB_URL = JDBC_PREFIX+DB_HOST+":"+DB_PORT+"/"+DB_NAME;
        SQL_PROPERTIES.setProperty("url", DB_URL);
        SQL_PROPERTIES.setProperty("user", DB_USER);
        SQL_PROPERTIES.setProperty("password", DB_PW);
        SQL_PROPERTIES.setProperty("driver", "com.mysql.cj.jdbc.Driver");
        SQL_PROPERTIES.setProperty("validationQuery", "select 1");
    }

    public static SQLContext getSQLContext(){
        return new SQLContext(Spark.getJavaSparkContext());
    }

    public static DataFrame getSqlReader(String table){
        return getSQLContext().read().jdbc(DB_URL, table, SQL_PROPERTIES);
    }

    public static Connection getMysqlConnection() throws Exception{
        if(SQL_PROPERTIES == null)
            initSqlProperties();
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
    }

    public static void saveDataFrame(DataFrame dataFrame,String table){
        if (SQL_PROPERTIES == null)
            initSqlProperties();
        dataFrame.write().mode(SaveMode.Append).jdbc(DB_URL, table, SQL_PROPERTIES);
    }
}
