package com.cse.entity;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 25.
 */
public class Paragraph implements Serializable{
    private int pageId;
    private String line;

    public Paragraph(int pageId, String line){
        this.pageId = pageId;
        this.line = line;
    }

    public int getPageId(){
        return this.pageId;
    }

    public String getLine(){
        return this.line;
    }
}
