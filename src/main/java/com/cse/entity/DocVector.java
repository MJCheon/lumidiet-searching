package com.cse.entity;

import org.apache.spark.mllib.linalg.Vector;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bullet on 16. 10. 25.
 * 문서 벡터에 대한 클래스
 */
public class DocVector implements Serializable{
    private int pageId;
    private ArrayList<Word> wordList;
    private Vector docVector;
    private Page page;
    public static final int dimension = 200;

    public DocVector(int pageId){
        this.pageId = pageId;
    }

    public void setPageId(int pageId){
        this.pageId = pageId;
    }

    public int getPageId(){
        return this.pageId;
    }

    public ArrayList<Word> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<Word> wordList) {
        this.wordList = wordList;
    }

    public void setDocVector(Vector docVector){
        this.docVector = docVector;
    }

    public Vector getDocVector(){
        return this.docVector;
    }

    public void setPage(Page page){
        this.page = page;
    }

    public Page getPage(){
        return this.page;
    }
}
