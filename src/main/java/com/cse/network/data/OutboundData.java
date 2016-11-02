package com.cse.network.data;

import java.util.ArrayList;

/**
 * Created by bullet on 16. 10. 25.
 */
public class OutboundData {
    ArrayList<String> simKeywordList;
    ArrayList<Integer> simPageList;

    public OutboundData(ArrayList<String> simKeywordList, ArrayList<Integer> simPageList){
        this.simKeywordList = simKeywordList;
        this.simPageList = simPageList;
    }

    public void setSimKeywordList(ArrayList<String> simKeywordList){
        this.simKeywordList = simKeywordList;
    }

    public ArrayList<String> getSimKeywordList(){
        return this.simKeywordList;
    }

    public void setSimPageList(ArrayList<Integer> simPageList){
        this.simPageList = simPageList;
    }

    public ArrayList<Integer> getSimPageList(){
        return this.simPageList;
    }
}
