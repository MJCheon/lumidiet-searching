package com.cse.network.data;

/**
 * Created by bullet on 16. 10. 25.
 */
public class SimKeywordMethod extends BaseMethod {

    private String searchKeyword;

    public SimKeywordMethod(){
        super();
    }

    public void setSearchKeyword(String keyword){
        this.searchKeyword = keyword;
    }

    public String getSearchKeyword(){
        return searchKeyword;
    }
}
