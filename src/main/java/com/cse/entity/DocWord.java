package com.cse.entity;

import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 29.
 */
public class DocWord implements Serializable{
    private long id;
    private int page_id;
    private long wordIdx;
    private float tf;
    private float tfidf;
    private Vector vector;

    private int cnt;
    private String word;

    public DocWord(int page_id,long wordIdx,float tfidf){
        this.page_id = page_id;
        this.wordIdx = wordIdx;
        this.tfidf = tfidf;
    }

    public DocWord(long id, int page_id, long wordIdx, float tf, float tfidf) {
        this.id = id;
        this.page_id = page_id;
        this.wordIdx = wordIdx;
        this.tf = tf;
        this.tfidf = tfidf;
        this.word = "";
    }

    public DocWord(String word){
        this.word = word;
        this.cnt = 1;
        this.id = 0;
        this.page_id = 0;
        this.wordIdx = 0;
        this.tfidf = 0;
        this.tf = 0;
    }

    public DocWord(long id,long wordIdx,float tf){
        this.id = id;
        this.wordIdx = wordIdx;
        this.tf = tf;
    }

    public String getWord(){
        return word;
    }

    public void setWord(String word){
        this.word = word;

    }

    public int getCnt(){
        return cnt;
    }

    public void setCnt(int cnt){
        this.cnt = cnt;
    }

    public void increaseCnt(){
        this.cnt++;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }

    public long getWordIdx() {
        return wordIdx;
    }

    public void setWordIdx(long wordIdx) {
        this.wordIdx = wordIdx;
    }

    public float getTf() {
        return tf;
    }

    public void setTf(float tf) {
        this.tf = tf;
    }

    public float getTfIdf() {
        return tfidf;
    }

    public void setTfIdf(float tfidf) {
        this.tfidf = tfidf;
    }

    public Vector getVector(){
        return vector;
    }

    public void setVector(Vector vector){
        this.vector = vector;
    }

    public Row toIndexTypeRow(){
        return RowFactory.create(word,cnt);
    }
}
