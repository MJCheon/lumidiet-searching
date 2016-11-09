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

        wordVectorRDD = wordVectorRDD.join(tfidfRDD).groupByKey().mapToPair(tuple->{
            double[] wordVector = null;

            for(Tuple2<WordVector, Word> wordVectorWord : tuple._2()){
                wordVector = wordVectorWord._1().getWordVector().toArray();
            }
            return new Tuple2<>(tuple._1(), new WordVector(tuple._1(),new DenseVector(wordVector)));
        });

        wordVectorRDD.cache();
        wordVectorRDD.count();
    }

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

    private HashSet<String> splitQueryString(String query){
        HashSet<String> hashSet = new HashSet<>();

        WordExtractor wordExtractor = new WordExtractor();
        List<Word> words = wordExtractor.extractWordFromParagraph(0, query);

        for(Word word : words){
            String strWord = word.getWord();
            hashSet.add(strWord);
        }

        return hashSet;
    }

    public SearchResult search(String query) {
        SearchWord searchWord = getQueryVector(query);

        return new SearchResult(getRelativeWord(searchWord),getRelativeDoc(searchWord));
    }

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
