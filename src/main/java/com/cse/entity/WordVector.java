package com.cse.entity;

import org.apache.spark.mllib.linalg.Vector;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 25.
 */
public class WordVector implements Serializable{
    private String word;
    private long wordIdx;
    private Vector wordVector;
    private int pageId;

    public WordVector(String word, Vector wordVector){
        this.word = word;
        this.pageId = 0;
        this.wordVector = wordVector;
    }

    public void setWord(String word){
        this.word = word;
    }

    public String getWord(){
        return this.word;
    }

    public void setWordIdx(long wordIdx){
        this.wordIdx = wordIdx;
    }

    public long getWordIdx(){
        return this.wordIdx;
    }

    public void setWordVector(Vector wordVector){
        this.wordVector = wordVector;
    }

    public Vector getWordVector(){
        return this.wordVector;
    }

    public void setPageId(int pageId){
        this.pageId = pageId;
    }

    public int getPageId(){
        return this.pageId;
    }
}
