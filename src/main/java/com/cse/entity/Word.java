package com.cse.entity;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 25.
 * 단어에 대한 클래스
 */
public class Word implements Serializable{
    private long id;
    private int pageId;
    private String word;
    private double tf;
    private double tfidf;
    private double cnt;
    private Vector vector;

    public Word(int pageId, String word){
        this.id = 0;
        this.pageId = pageId;
        this.word = word;
        this.tf = 0;
        this.tfidf = 0;
        this.cnt = 0;
    }

    public Word(int pageId, String word, double tfidf){
        this.pageId = pageId;
        this.word = word;
        this.tfidf = tfidf;
    }

    @Override
    public int hashCode(){
        String identity = pageId+"/"+word;
        return identity.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(this.hashCode() == obj.hashCode())
            return true;
        else
            return false;
    }
    public long getId(){
        return this.id;
    }

    public void setId(long id){
        this.id = id;
    }

    public int getPageId(){
        return this.pageId;
    }

    public void setPageId(int pageId){
        this.pageId = pageId;
    }

    public String getWord(){
        return this.word;
    }

    public void setWord(String word){
        this.word = word;
    }

    public double getTf(){
        return this.tf;
    }

    public void setTf(double tf){
        this.tf = tf;
    }

    public double getTfidf(){
        return this.tfidf;
    }

    public void setTfidf(double tfidf){
        this.tfidf = tfidf;
    }

    public void setCnt(double cnt){
        this.cnt = cnt;
    }

    public double getCnt(){
        return this.cnt;
    }

    public void setVector(Vector vector){
        this.vector = vector;
    }

    public Vector getVector(){
        return this.vector;
    }
}
