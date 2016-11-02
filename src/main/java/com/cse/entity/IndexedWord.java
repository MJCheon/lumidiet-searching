package com.cse.entity;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 25.
 */
public class IndexedWord implements Serializable {
    private long id;
    private String word;
    private long cnt;

    public IndexedWord(long id,String word,long cnt){
        this.id = id;
        this.word = word;
        this.cnt = cnt;
    }

    public IndexedWord(long id, String word){
        this.id = id;
        this.word = word;
    }

    public IndexedWord(long id,long cnt){
        this.id = id;
        this.cnt = cnt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getCnt() {
        return cnt;
    }

    public void setCnt(long cnt) {
        this.cnt = cnt;
    }
}
