package com.cse.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by leeyh on 2016. 11. 8.
 * 사용자의 검색 결과에 대한 클래스
 */
public class SearchResult {
    /**
     * 유사 단어 List
     */
    @SerializedName("words")
    private List<ResultWord> relativeWords;
    /**
     * 유사 페이지 List
     */
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
