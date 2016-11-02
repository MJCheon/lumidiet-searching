package com.cse.entity;

import org.apache.spark.mllib.linalg.Vector;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bullet on 16. 10. 25.
 */
public class DocVector implements Serializable{
    private int pageId;
    private ArrayList<TfIdfWord> wordIdxList;
    private Vector docVector;
    public static final int dimension = 200;

    public DocVector(int pageId){
        this.pageId = pageId;
        this.wordIdxList = new ArrayList<>();
    }

    public void setPageId(int pageId){
        this.pageId = pageId;
    }

    public int getPageId(){
        return this.pageId;
    }

    public void setWordIdxList(ArrayList<TfIdfWord> wordIdxList){
        this.wordIdxList = wordIdxList;
    }

    public ArrayList<TfIdfWord> getWordIdxList(){
        return this.wordIdxList;
    }

    public void setDocVector(Vector docVector){
        this.docVector = docVector;
    }

    public Vector getDocVector(){
        return this.docVector;
    }
}
