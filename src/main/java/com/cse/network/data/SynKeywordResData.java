package com.cse.network.data;

/**
 * Created by bullet on 16. 10. 25.
 */
public class SynKeywordResData {
    //유사 키워드 리스트
    private OutboundData synData;

    /**
     * 유사 키워드 검색 결과 생성자
     * @param synData 검색된 유사 키워드 리스트
     */
    public SynKeywordResData(OutboundData synData){
        this.synData = synData;
    }

    /**
     * 유사 키워드 리스트를 리턴한다.
     * @return 유사 키워드 리스트
     */
    public OutboundData getSynData(){
        return this.synData;
    }
}
