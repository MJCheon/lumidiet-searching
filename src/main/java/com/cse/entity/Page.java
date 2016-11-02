package com.cse.entity;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 25.
 */
public class Page implements Serializable{
    private int id;
    private String url;
    private long date;
    private String title;
    private String body;

    public Page(){
    }

    public Page(int id, String url, String body){
        this.id = id;
        this.url = url;
        this.body = body;
        this.title = null;
        this.date = 0;
    }

    public Page(int id, String url, String title, String body, long date){
        this.id = id;
        this.url = url;
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getBody(){
        return this.body;
    }

    public void setBody(String body){
        this.body = body;
    }

    public long getDate(){
        return this.date;
    }

    public void setDate(long date){
        this.date = date;
    }

    public static StructType getStructType(){
        return new StructType(new StructField[]{
                new StructField("id", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("url", DataTypes.StringType, false, Metadata.empty()),
                new StructField("title", DataTypes.StringType, false, Metadata.empty()),
                new StructField("body", DataTypes.StringType, false, Metadata.empty()),
                new StructField("date", DataTypes.LongType, false, Metadata.empty())
        });
    }

    public Row pageToRow(){
        return RowFactory.create(id, url, title, body, date);
    }

}
