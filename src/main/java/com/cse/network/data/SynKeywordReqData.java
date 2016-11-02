package com.cse.network.data;

import com.cse.network.interfaces.IFindKeyword;

/**
 * Created by bullet on 16. 10. 25.
 */
public class SynKeywordReqData {
    private String keyword;
    private IFindKeyword listener;

    public SynKeywordReqData(String keyword,IFindKeyword listener){
        this.keyword = keyword;
        this.listener = listener;
    }

    public String getKeyword() {
        return keyword;
    }

    public IFindKeyword getListener() {
        return listener;
    }
}
