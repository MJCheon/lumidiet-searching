package com.cse.entity;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 25.
 * 사용자의 검색에 대한 유사 단어 클래스
 */
public class ResultWord implements Serializable {
    private String text;
    private double weight;

    public ResultWord(String text, double weight) {
        this.text = text;
        this.weight = 1.0f/weight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
