package com.cse.network.data;

import com.cse.entity.Page;

import java.util.ArrayList;

/**
 * Created by bullet on 16. 10. 25.
 */
public class OutboundData {
    ArrayList<String> simKeywordList;
    ArrayList<Page> simPageList;

    public OutboundData(ArrayList<String> simKeywordList, ArrayList<Page> simPageList){
        this.simKeywordList = simKeywordList;
        this.simPageList = simPageList;
    }

    public void setSimKeywordList(ArrayList<String> simKeywordList){
        this.simKeywordList = simKeywordList;
    }

    public ArrayList<String> getSimKeywordList(){
        return this.simKeywordList;
    }

    public void setSimPageList(ArrayList<Page> simPageList){
        this.simPageList = simPageList;
    }

    public ArrayList<Page> getSimPageList(){
        return this.simPageList;
    }
}
