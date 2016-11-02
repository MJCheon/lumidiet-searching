package com.cse.entity;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 26.
 */
public class TfIdfWord implements Serializable {
    private long wordIdx;
    private double tfidf;

    public TfIdfWord(long wordIdx, double tfidf){
        this.wordIdx = wordIdx;
        this.tfidf = tfidf;
    }

    public void setWordIdx(long wordIdx){
        this.wordIdx = wordIdx;
    }

    public long getWordIdx(){
        return this.wordIdx;
    }

    public void setTfidf(double tfidf){
        this.tfidf = tfidf;
    }

    public double getTfidf(){
        return this.tfidf;
    }
}
