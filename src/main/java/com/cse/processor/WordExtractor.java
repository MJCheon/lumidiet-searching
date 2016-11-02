package com.cse.processor;

import com.clearspring.analytics.util.Lists;
import com.cse.common.Common;
import com.cse.entity.InnerWord;
import com.cse.entity.Word;
import com.twitter.penguin.korean.KoreanPosJava;
import com.twitter.penguin.korean.KoreanTokenJava;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.phrase_extractor.KoreanPhraseExtractor;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;
import scala.collection.Seq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by bullet on 16. 10. 25.
 */
public class WordExtractor implements Serializable{
    public List<Word> extractWordFromParagraph(int pageId, String body){
        Hashtable<String, InnerWord> innerWordHashTable = new Hashtable<>();
        double allCnt = 0;
        List<KoreanTokenJava> stemmedLinelist = stemmingLine(body);
        ArrayList<String> nameList = initNameList(extractPhrases(body));

        if(stemmedLinelist.size() == 0)
            return null;

        for(int i=0; i<stemmedLinelist.size(); i++) {
            KoreanTokenJava koreanTokenJava = stemmedLinelist.get(i);
            String word = "";
            if ((koreanTokenJava.getPos().equals(KoreanPosJava.Noun) || koreanTokenJava.getPos().equals(KoreanPosJava.ProperNoun))) {
                word = koreanTokenJava.getText();
                if (koreanTokenJava.getLength() > 1) {  // 2글자 이상의 명사 출력 ("12경기" 같은 것도 포함)
                    if (i != 0 && stemmedLinelist.get(i - 1).getPos().equals(KoreanPosJava.Number)) {
                        if (!isTime(stemmedLinelist.get(i - 1)))
                            word = stemmedLinelist.get(i - 1).getText() + word;
                    }
                }
                else if (koreanTokenJava.getLength() == 1) {    // "1세" 같은 것 출력
                    if (i != 0 && stemmedLinelist.get(i - 1).getPos().equals(KoreanPosJava.Number)) {
                        if (!isTime(stemmedLinelist.get(i - 1)))
                            word = stemmedLinelist.get(i - 1).getText() + word;
                    }
                    else
                        word = "";
                }
            } else if (koreanTokenJava.getPos().equals(KoreanPosJava.Number) && koreanTokenJava.getLength() > 1) {      // 년월일 같은 것들 출력
                if (isTime(koreanTokenJava))
                    word = koreanTokenJava.getText();
            }
            else
                word = "";

            if(word.equals(""))
                continue;

            word = foundName(nameList, word);
            word = removeJosa(word);

            allCnt++;

            if(innerWordHashTable.containsKey(word))
                innerWordHashTable.get(word).increaseCnt();
            else
                innerWordHashTable.put(word, new InnerWord(word));
        }

        List<Word> wordList = Lists.newArrayList();

        for (InnerWord innerWord : innerWordHashTable.values()) {
            Word w = new Word(pageId, innerWord.getWord());
            w.setTf((innerWord.getCnt() / allCnt));
            w.setCnt(innerWord.getCnt());
            wordList.add(w);
        }

        if(wordList.size()==0)
            wordList.add(new Word(Common.ERROR, " "));
        return wordList;
    }

    public static List<KoreanTokenJava> stemmingLine(String line){
        try {
            CharSequence normalized = TwitterKoreanProcessorJava.normalize(line);
            Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
            Seq<KoreanTokenizer.KoreanToken> stemmed = TwitterKoreanProcessorJava.stem(tokens);
            return TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed);
        }
        catch (Exception e){
            return com.google.common.collect.Lists.newArrayList();
        }
    }

    public static List<KoreanPhraseExtractor.KoreanPhrase> extractPhrases(String line){
        CharSequence normalized = TwitterKoreanProcessorJava.normalize(line);
        Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
        return TwitterKoreanProcessorJava.extractPhrases(tokens, false, false);
    }

    private static boolean isTime(KoreanTokenJava koreanTokenJava){
        String txt = koreanTokenJava.getText();
        if(txt.contains("년") || txt.contains("월") || txt.contains("일"))
            return true;
        else if(txt.contains("시") || txt.contains("분") || txt.contains("초"))
            return true;
        else
            return false;
    }

    private static String removeJosa(String word){
        String allJosa = "었,께서,에서,에게,보다,라고,이여,이시여,마따나,이며,부터,로부터,으로부터,야말로,이라며,라며,라서,로서,로써,에다,까지,마저,조차,따라,토록,커녕,든지,이든지,나마,이나마,았";
        String josaList[] = allJosa.split("\\,");
        for(String josa : josaList){
            if(word.contains(josa))
                word = "";
        }

        return word;
    }

    private static ArrayList<String> initNameList(List<KoreanPhraseExtractor.KoreanPhrase> phraseList){
        ArrayList<String> nameList = new ArrayList<String>();
        for(KoreanPhraseExtractor.KoreanPhrase phrase : phraseList){
            String word = phrase.text();
            if(word.length() > 2 && word.length() < 4 && !word.contains(" ") && !word.contains(",")){
                nameList.add(word);
            }
        }

        return nameList;
    }

    private String foundName(ArrayList<String> nameList, String word){
        for(String name : nameList){
            if(name.contains(word))
                return name;
        }
        return word;
    }
}
