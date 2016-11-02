package com.cse.spark;

import com.cse.common.Common;
import com.cse.module.DocSearchModule;
import com.cse.network.data.OutboundData;
import com.cse.network.data.SynKeywordReqData;
import com.cse.network.interfaces.IFindKeyword;
import org.apache.spark.ml.feature.Word2VecModel;
import org.apache.spark.sql.Row;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by bullet on 16. 10. 25.
 */
public class Word2VecInstance extends Thread {
    private static DocSearchModule docSearchModule;
    private static Word2VecInstance word2VecInstance;
    private static Word2VecModel model;
    private Queue queue;

    /**
     * Singleton 객체인 Word2VecInstance를 리턴한다.
     * @return Word2VecInstance
     */
    public static Word2VecInstance getInstance() {
        //Word2VecInstance가 초기화되지 않았다면 초기화 후 리턴한다.
        if (word2VecInstance == null)
            word2VecInstance = new Word2VecInstance();
        return word2VecInstance;
    }

    /**
     * Word2VecInstance 생성자
     */
    private Word2VecInstance() {
        //model이 null일 경우 load한다.
        if (model == null) {
            model = Word2VecModel.load("./w2model");
            model.getVectors().show();
            docSearchModule = new DocSearchModule();
            queue = new LinkedList();
        }
    }

    /**
     * 유사 키워드를 찾는다.
     * @param keyword 찾고자 하는 Keyword
     * @param listener 콜백 객체(찾은 후 해당 객체의 메서드 호출)
     */
    public void getSymKeyword(String keyword, IFindKeyword listener) {
        queue.offer(new SynKeywordReqData(keyword, listener));
    }

    /**
     * 실제 처리 loop
     */
    public void run() {

        while (true) {
            //처리할 데이터가 존재한다면 처리한다.
            if (!queue.isEmpty()) {
                while (!queue.isEmpty())
                    process(queue.poll());
            }
            Common.sleep();
        }
    }

    /**
     * 유사 키워드 검색 작업을 수행한다.
     *
     * @param obj
     */
    private void process(Object obj) {
        //유사 키워드 검색 요청일 때
        if (obj instanceof SynKeywordReqData) {
            SynKeywordReqData data = (SynKeywordReqData) obj;
            try {
                //유사 키워드를 검색하여 Row배열에 저장한다(Collect)

                Row[] rowList = model.findSynonyms(data.getKeyword(), 10).select("word", "similarity").collect();
                ArrayList<String> symKeywordList = new ArrayList<String>();

                String query = "";
                //검색 결과를 결과 리스트에 삽입
                for (Row row : rowList) {
                    symKeywordList.add(row.getString(0));
                    query += row.getString(0) + " ";
                }
                OutboundData synData = null;

                //콜백 메서드 실행
                data.getListener().findSynData(synData);
            }
            catch(Exception e){
                e.printStackTrace();
                data.getListener().error();
            }
        }
    }

}
