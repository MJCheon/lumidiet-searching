package com.cse.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by leeyh on 2016. 11. 8..
 */
public class SearchResult {
    @SerializedName("words")
    private List<ResultWord> relativeWords;
    @SerializedName("pages")
    private List<Page> pageList;

    public SearchResult(List<ResultWord> relativeWords, List<Page> pageList) {
        this.relativeWords = relativeWords;
        this.pageList = pageList;
    }

    public List<ResultWord> getRelativeWords() {
        return relativeWords;
    }

    public void setRelativeWords(List<ResultWord> relativeWords) {
        this.relativeWords = relativeWords;
    }

    public List<Page> getPageList() {
        return pageList;
    }

    public void setPageList(List<Page> pageList) {
        this.pageList = pageList;
    }
}
