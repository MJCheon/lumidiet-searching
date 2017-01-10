package com.cse.entity;

import java.io.Serializable;

/**
 * Created by bullet on 16. 10. 25.
 */
public class Page implements Serializable{
    private int id;
    private String url;
    private long date;
    private String title;
    private String body;

    public Page(int id, String url, String title, String body, long date){
        this.id = id;
        this.url = url;
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getBody(){
        return this.body;
    }

    public void setBody(String body){
        this.body = body;
    }

    public long getDate(){
        return this.date;
    }

    public void setDate(long date){
        this.date = date;
    }
}
