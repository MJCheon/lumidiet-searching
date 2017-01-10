package com.cse.module;

import com.cse.common.LogInstance;
import com.cse.entity.*;
import com.cse.processor.WordExtractor;
import com.cse.spark.Spark;
import com.cse.spark.SparkJDBC;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.ml.feature.Word2VecModel;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.sql.Row;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by bullet on 16. 10. 25.
 * 검색한 단어에 대한 유사 단어 및 문서를 찾아줌
 */
public class DocSearchModule implements Serializable{
    private static DocSearchModule instance;

    private JavaPairRDD<Integer, DocVector> docVectorPairRDD;
    private JavaPairRDD<String, Word> tfidfRDD;
    private JavaPairRDD<String, WordVector> wordVectorRDD;
    private JavaPairRDD<Integer, Page> pageRDD;
    private Word2VecModel model;

    public static DocSearchModule getInstance(){
        if(instance==null)
            instance = new DocSearchModule();
        return instance;
    }

    private DocSearchModule(){
        init();
    }

    private void init(){
        initWordVector();
        initDocVectorRDD();
    }

    /**
     * 단어 벡터 생성
     * 단어에 대한 TF-IDF 값과 학습된 모델에서 Vector값을 읽어들어옴
     */
    private void initWordVector(){
        //TF-IDF 단어 벡터를 생성한다.
        tfidfRDD = SparkJDBC.getSqlReader(SparkJDBC.TABLE_DOCWORD).select("pageid","word","tfidf").toJavaRDD().repartition(Spark.NUM_CORE).map(r->{
            return new Word(r.getInt(0),r.getString(1),r.getDouble(2));
        }).mapToPair(r->{
            return new Tuple2<>(r.getWord(),r);
        });

        tfidfRDD.cache();
        long allWordCnt = tfidfRDD.count();
        LogInstance.getLogger().info("총 {}개의 단어가 로드되었습니다.",allWordCnt);

        model = Word2VecModel.load("./w2model");
        wordVectorRDD = model.getVectors().select("word", "vector").toJavaRDD().repartition(Spark.NUM_CORE).mapToPair(r->{
            return new Tuple2<>(r.getString(0),new WordVector(r.getString(0),(Vector)r.get(1)));
        });

        wordVectorRDD.cache();
        wordVectorRDD.count();
    }

    /**
     * 문서 벡터 생성
     * 단어 벡터를 이용하여 페이지를 구성하는 단어의 벡터 값의 평균값을 문서 벡터로 사용
     */
    private void initDocVectorRDD() {
        pageRDD = SparkJDBC.getSqlReader(SparkJDBC.TABLE_PAGE).select("id", "url", "title", "body", "date").toJavaRDD().repartition(Spark.NUM_CORE).mapToPair(r->{
            int length = r.getString(3).length();
            String body = null;
            if(length>150)
                body = r.getString(3).substring(0, 150);
            else
                body = r.getString(3);

            return new Tuple2<>(r.getInt(0), new Page(r.getInt(0), r.getString(1), r.getString(2), body+"...", r.getLong(4)));
        });

        pageRDD.cache();
        pageRDD.count();

        JavaPairRDD<Integer, Iterable<Word>> pairDocWordRDD = tfidfRDD.join(wordVectorRDD).mapToPair(r->{
                Word word = new Word(r._2()._1().getPageId(), r._1(), r._2()._1().getTfidf());
                word.setVector(r._2()._2().getWordVector());
                return new Tuple2<>(word.getPageId(), word);
        }).groupByKey();

        docVectorPairRDD = pairDocWordRDD.mapToPair(tuple->{
                double[] docVec = new double[DocVector.dimension];
                int wordCnt = 0;
                ArrayList<Word> wordList = new ArrayList<>();

                for(Word word : tuple._2()){
                    wordCnt++;
                    double[] wordVec = word.getVector().toArray();
                    wordList.add(word);

                    for(int i = 0; i< DocVector.dimension; i++)
                        docVec[i] += (wordVec[i]);
                }

                for(int i = 0; i< DocVector.dimension; i++)
                    docVec[i] /= (double) wordCnt;


                DocVector docVector = new DocVector(tuple._1());
                docVector.setDocVector(new DenseVector(docVec));
                docVector.setWordList(wordList);

                return new Tuple2<>(tuple._1(), docVector);
        });

        docVectorPairRDD = docVectorPairRDD.join(pageRDD).mapToPair(tuple->{
                DocVector docVector = new DocVector(tuple._1());
                docVector.setDocVector(tuple._2()._1().getDocVector());
                docVector.setWordList(tuple._2()._1().getWordList());
                docVector.setPageId(tuple._2()._1().getPageId());
                docVector.setPage(tuple._2()._2());
                return new Tuple2<>(tuple._1(), docVector);
        });

        docVectorPairRDD.cache();
        docVectorPairRDD.count();
    }

    /**
     * 사용자가 검색한 단어에 대한 유사 단어 검색
     * @param searchWord 사용자가 검색한 단어
     * @return
     */
    public ArrayList<ResultWord> getRelativeWord(SearchWord searchWord){
        ArrayList<ResultWord> simWords = new ArrayList<>();
        final double[] wordVec = searchWord.getVector().toArray();
        final HashSet<String> searchWordList = searchWord.getWordIdxList();

        List<Tuple2<Double, WordVector>> similarWordList = wordVectorRDD.filter(tuple->{
                if(searchWordList.contains(tuple._1()))
                    return false;
                else
                    return true;
        }).mapToPair(tuple->{
                double similarity = 0;
                double[] secondVal = tuple._2().getWordVector().toArray();
                for(int i = 0; i< DocVector.dimension; i++)
                    similarity+=Math.pow(secondVal[i]-wordVec[i],2);
                similarity = Math.sqrt(similarity);
                return new Tuple2<Double, WordVector>(similarity,tuple._2());
        }).sortByKey(true).take(10);

        for(int i=0; i<similarWordList.size(); i++)
            simWords.add(i, new ResultWord(similarWordList.get(i)._2().getWord(),similarWordList.get(i)._1()));

        return simWords;
    }

    /**
     * 사용자가 검색한 쿼리에 대한 단어 추출 및 벡터 값 계산
     * @param query 사용자가 검색한 쿼리
     * @return
     */
    private SearchWord getQueryVector(String query){
        final HashSet<String> querySet = splitQueryString(query);
        HashSet<String> searchWordList = new HashSet<>();
        List<WordVector> wordVectorList = wordVectorRDD.filter(tuple->{
                if(querySet.contains(tuple._1()))
                    return true;
                else
                    return false;
        }).values().collect();

        double[] searchWordVector = new double[DocVector.dimension];

        for(WordVector wordVector : wordVectorList){
            searchWordList.add(wordVector.getWord());
            double[] vec = wordVector.getWordVector().toArray();
            for(int i = 0; i< DocVector.dimension; i++)
                searchWordVector[i] += vec[i];
        }
        for(int i = 0; i< DocVector.dimension; i++)
            searchWordVector[i] /= (double)wordVectorList.size();
        return new SearchWord(new DenseVector(searchWordVector), searchWordList);
    }

    public HashSet<String> splitQueryString(String query){
        HashSet<String> hashSet = new HashSet<>();

        List<Word> words = WordExtractor.extractWordFromParagraph(0, query);

        for(Word word : words){
            String strWord = word.getWord();
            hashSet.add(strWord);
        }

        return hashSet;
    }

    /**
     * 검색 메소드
     * @param query 사용자가 검색한 쿼리
     * @return
     */
    public SearchResult search(String query) {
        SearchWord searchWord = getQueryVector(query);

        return new SearchResult(getRelativeWord(searchWord),getRelativeDoc(searchWord));
    }

    /**
     * 사용자가 검색한 단어와 유사한 문서를 찾음
     * 사용자가 검색한 단어를 포함하고 있고, 페이지를 구성하는 단어들의 TF-IDF 값을 이용해
     * 유클리디안 유사도를 사용하여 가장 가까운 문서 검색
     * @param searchWord 사용자가 검색한 단어
     * @return
     */
    private ArrayList<Page> getRelativeDoc(SearchWord searchWord){
        ArrayList<Page> simDoc = new ArrayList<>();
        final int wordCnt = searchWord.getWordIdxList().size();
        final HashSet<String> wordSet = searchWord.getWordIdxList();

        List<Tuple2<Double, DocVector>> docList = docVectorPairRDD.filter(tuple->{
            DocVector docVector = tuple._2();
            int currentCnt = 0;
            if(docVector== null || docVector.getWordList()==null || docVector.getWordList().size()==0)
                return false;

            for(Word word : docVector.getWordList()){
                if(wordSet.contains(word.getWord()))
                    currentCnt++;
            }
            if(currentCnt==wordCnt)
                return true;
            else
                return false;
        }).mapToPair(tuple->{
            double tfidfSimilarity = 0;
            ArrayList<Word> wordList = tuple._2().getWordList();

            for(Word word : wordList){
                if(wordSet.contains(word.getWord()))
                    tfidfSimilarity += word.getTfidf();
            }
            return new Tuple2<>(tfidfSimilarity, tuple._2());
        }).sortByKey().take(50);

        for(int i=0; i<docList.size(); i++){
            simDoc.add(i, docList.get(i)._2().getPage());
        }
        return simDoc;
    }
}
