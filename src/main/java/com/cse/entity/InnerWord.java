package com.cse.entity;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 25.
 */
public class InnerWord implements Serializable{
    private String word;
    private double cnt;

    public InnerWord(String word){
        this.word = word;
        this.cnt = 1;
    }

    @Override
    public int hashCode(){
        return word.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(this.hashCode() == obj.hashCode())
            return true;
        else
            return false;
    }

    public void increaseCnt(){
        this.cnt++;
    }

    public String getWord(){
        return word;
    }

    public double getCnt(){
        return cnt;
    }
}
