package com.cse.module;

import com.cse.common.LogInstance;
import com.cse.entity.DocVector;
import com.cse.entity.SearchWord;
import com.cse.entity.Word;
import com.cse.entity.WordVector;
import com.cse.spark.Spark;
import com.cse.spark.SparkJDBC;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.ml.feature.Word2VecModel;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.sql.Row;
import scala.Tuple2;
import scala.collection.Seq;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;

/**
 * Created by bullet on 16. 10. 25.
 */
public class DocSearchModule implements Serializable{
    private JavaPairRDD<Integer, DocVector> docVectorPairRDD;
    private JavaPairRDD<String, Word> indexedWordPairRDD;
    private JavaPairRDD<String, WordVector> wordVectorPairRDD;
    private JavaPairRDD<String, WordVector> wordPairRDD;
    private double TFIDF_MAX_VALUE;
    private Word2VecModel model;

    public DocSearchModule(){
        init();
    }

    private void init(){

        initWordVector();
        initDocVectorRDD();
    }

    private void initMaxTfidf(){
        Connection connection = null;
        try{
            connection = SparkJDBC.getMysqlConnection();
            String sql = "select max(tfidf) from docword";
            ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

            while(resultSet.next()){
                TFIDF_MAX_VALUE = resultSet.getDouble(1);
            }

            resultSet.close();
            connection.close();
        }
        catch (Exception e){
            LogInstance.getLogger().debug(e.getMessage());
        }
    }

    private void initWordVector(){
        indexedWordPairRDD = SparkJDBC.getSqlReader(SparkJDBC.TABLE_DOCWORD).select("pageid", "word", "tfidf").toJavaRDD().repartition(Spark.NUM_CORE).map(new Function<Row, Word>() {
            @Override
            public Word call(Row v1) throws Exception {
                int pageId = v1.getInt(0);
                String word = v1.getString(1);
                double tfidfValue = v1.getDouble(2);
                return new Word(pageId, word, tfidfValue);
            }
        }).mapToPair(new PairFunction<Word, String, Word>() {
            @Override
            public Tuple2<String, Word> call(Word word) throws Exception {
                return new Tuple2<String, Word>(word.getWord(), word);
            }
        });

        indexedWordPairRDD.cache();
        indexedWordPairRDD.count();

        initMaxTfidf();

        model = Word2VecModel.load("./w2model");
        wordPairRDD = model.getVectors().select("word", "vector").toJavaRDD().repartition(Spark.NUM_CORE).mapToPair(new PairFunction<Row, String, WordVector>() {
            @Override
            public Tuple2<String, WordVector> call(Row row) throws Exception {
                String word = row.getString(0);
                Vector vector = (Vector) row.get(1);
                return new Tuple2<String, WordVector>(word, new WordVector(word, vector));
            }
        });

        wordVectorPairRDD = wordPairRDD.repartition(Spark.NUM_CORE).join(indexedWordPairRDD).groupByKey().mapToPair(new PairFunction<Tuple2<String, Iterable<Tuple2<WordVector, Word>>>, String, WordVector>() {
            @Override
            public Tuple2<String, WordVector> call(Tuple2<String, Iterable<Tuple2<WordVector, Word>>> stringIterableTuple2) throws Exception {
                double tfidf = 0;
                int tfidfCnt = 0;
                double[] wordVector = null;

                for(Tuple2<WordVector, Word> wordVectorWord : stringIterableTuple2._2()) {
                    tfidf += wordVectorWord._2().getTfidf();
                    wordVector = wordVectorWord._1().getWordVector().toArray();
                    tfidfCnt++;
                }

                tfidf /= (double) tfidfCnt;
                tfidf = TFIDF_MAX_VALUE - tfidf;

                for(int i = 0; i< DocVector.dimension; i++)
                    wordVector[i] *= tfidf;

                return new Tuple2<String, WordVector>(stringIterableTuple2._1(), new WordVector(stringIterableTuple2._1(), new DenseVector(wordVector)));
            }
        });

        wordVectorPairRDD.cache();
        wordVectorPairRDD.count();
    }

    private void initDocVectorRDD() {
        JavaPairRDD<Integer, Iterable<Word>> pairDocWordRDD = indexedWordPairRDD.join(wordVectorPairRDD).repartition(Spark.NUM_CORE).mapToPair(new PairFunction<Tuple2<String, Tuple2<Word, WordVector>>, Integer, Word>() {
            @Override
            public Tuple2<Integer, Word> call(Tuple2<String, Tuple2<Word, WordVector>> stringTuple2Tuple2) throws Exception {
                Word word = new Word(stringTuple2Tuple2._2()._1().getPageId(), stringTuple2Tuple2._1(), stringTuple2Tuple2._2()._1().getTfidf());
                word.setVector(stringTuple2Tuple2._2()._2().getWordVector());
                return new Tuple2<Integer, Word>(word.getPageId(), word);
            }
        }).groupByKey();

        docVectorPairRDD = pairDocWordRDD.mapToPair(new PairFunction<Tuple2<Integer, Iterable<Word>>, Integer, DocVector>() {
            @Override
            public Tuple2<Integer, DocVector> call(Tuple2<Integer, Iterable<Word>> integerIterableTuple2) throws Exception {
                double[] docVec = new double[DocVector.dimension];
                int wordCnt = 0;

                for(Word word : integerIterableTuple2._2()){
                    wordCnt++;
                    double[] wordVec = word.getVector().toArray();
                    double tfidf = TFIDF_MAX_VALUE - word.getTfidf();

                    for(int i = 0; i< DocVector.dimension; i++)
                        docVec[i] += (wordVec[i] * tfidf);
                }

                for(int i = 0; i< DocVector.dimension; i++)
                    docVec[i] /= (double) wordCnt;

                DocVector docVector = new DocVector(integerIterableTuple2._1());
                docVector.setDocVector(new DenseVector(docVec));

                return new Tuple2<Integer, DocVector>(integerIterableTuple2._1(), docVector);
            }
        });

        docVectorPairRDD.cache();
        docVectorPairRDD.count();
    }

    public void getRelativeWord(SearchWord searchWord){
        final double[] wordVec = searchWord.getVector().toArray();
        final HashSet<String> searchWordList = searchWord.getWordIdxList();

        List<Tuple2<Double, WordVector>> similarWordList = wordVectorPairRDD.filter(new Function<Tuple2<String, WordVector>, Boolean>() {
            @Override
            public Boolean call(Tuple2<String, WordVector> v1) throws Exception {
                if(searchWordList.contains(v1._1()))
                    return false;
                else
                    return true;
            }
        }).mapToPair(new PairFunction<Tuple2<String,WordVector>, Double, WordVector>() {
            @Override
            public Tuple2<Double, WordVector> call(Tuple2<String, WordVector> stringWordVectorTuple2){
                double similarity = 0;
                double[] secondVal = stringWordVectorTuple2._2().getWordVector().toArray();
                for(int i = 0; i< DocVector.dimension; i++)
                    similarity+=Math.pow(secondVal[i]-wordVec[i],2);
                similarity = Math.sqrt(similarity);
                return new Tuple2<Double, WordVector>(similarity,stringWordVectorTuple2._2());
            }
        }).sortByKey(true).take(10);
        for (Tuple2<Double, WordVector> doubleWordVectorTuple2 : similarWordList) {
            System.out.println(doubleWordVectorTuple2._2().getWord());
        }
    }

    private SearchWord getQueryVector(String query){
        final HashSet<String> querySet = splitQueryString(query);
        HashSet<String> searchWordList = new HashSet<>();
        List<WordVector> wordVectorList = wordVectorPairRDD.filter(new Function<Tuple2<String, WordVector>, Boolean>() {
            @Override
            public Boolean call(Tuple2<String, WordVector> v1) throws Exception {
                if(querySet.contains(v1._1()))
                    return true;
                else
                    return false;
            }
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

        CharSequence normalized = TwitterKoreanProcessorJava.normalize(query);

        Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);

        for (String str : TwitterKoreanProcessorJava.tokensToJavaStringList(tokens)) {
            if(!hashSet.contains(str)&&str.length()>1)
                hashSet.add(str);
        }

        return hashSet;
    }

    public void search(String query) {
        SearchWord searchWord = getQueryVector(query);

        getRelativeWord(searchWord);
//        getRelativeDoc(docSearch);
    }

    private void getRelativeDoc(SearchWord searchWord){
        final double[] swVec = searchWord.getVector().toArray();
        List<Tuple2<Double, DocVector>> docList = docVectorPairRDD.mapToPair(new PairFunction<Tuple2<Integer, DocVector>, Double, DocVector>() {
            @Override
            public Tuple2<Double, DocVector> call(Tuple2<Integer, DocVector> integerDocVectorTuple2) throws Exception {
                double[] docVec = integerDocVectorTuple2._2().getDocVector().toArray();
                double similarity = 0;
                for(int i = 0; i< DocVector.dimension; i++)
                    similarity = Math.pow(docVec[i]-swVec[i], 2);
                similarity = Math.sqrt(similarity);
                return new Tuple2<Double, DocVector>(similarity, integerDocVectorTuple2._2());
            }
        }).sortByKey(true).take(10);
        for(Tuple2<Double, DocVector> tuple : docList){
            System.out.println(tuple._2().getPageId());
        }
    }
}
