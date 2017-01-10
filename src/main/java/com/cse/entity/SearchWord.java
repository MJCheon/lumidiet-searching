package com.cse.entity;

import org.apache.spark.mllib.linalg.Vector;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by bullet on 16. 10. 25.
 * 사용자의 검색어에 대한 클래스
 */
public class SearchWord implements Serializable{
    /**
     * 사용자의 검색 쿼리에 대한 벡터
     */
    private Vector vector;
    /**
     * 사용자의 검색 쿼리에 있는 단어 List
     */
    private HashSet<String> wordList;

    public SearchWord(Vector vector, HashSet<String> wordList){
        this.vector = vector;
        this.wordList = wordList;
    }

    public void setVector(Vector vector){
        this.vector = vector;
    }

    public Vector getVector(){
        return this.vector;
    }

    public void setWordIdxList(HashSet<String> wordIdxList){
        this.wordList = wordList;
    }

    public HashSet<String> getWordIdxList(){
        return wordList;
    }
}
