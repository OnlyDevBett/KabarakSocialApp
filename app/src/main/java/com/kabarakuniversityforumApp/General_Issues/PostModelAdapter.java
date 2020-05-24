package com.kabarakuniversityforumApp.General_Issues;

import java.util.Date;


public class PostModelAdapter extends PostId {
    private String user_id,topic,title,post,imageUrl,document,file,actulafilestring,collection;
    private Date timeStamp;

    public PostModelAdapter() {
    }

    public PostModelAdapter(String user_id, String topic, String title, String post, String imageUrl, String document, String file, String actulafilestring, String collection, Date timeStamp) {
        this.user_id = user_id;
        this.topic = topic;
        this.title = title;
        this.post = post;
        this.imageUrl = imageUrl;
        this.document = document;
        this.file = file;
        this.actulafilestring = actulafilestring;
        this.collection = collection;
        this.timeStamp = timeStamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getActulafilestring() {
        return actulafilestring;
    }

    public void setActulafilestring(String actulafilestring) {
        this.actulafilestring = actulafilestring;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
