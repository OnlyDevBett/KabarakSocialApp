package com.kabarakuniversityforumApp.General_Issues.Commenting;

import java.util.Date;



public class CommentList extends com.kabarakuniversityforumApp.General_Issues.PostId {

    private String imageComment,comment,user_id,document, status;
    private Date timeStamp;

    public CommentList() {
    }

    public CommentList(String imageComment, String comment, String user_id, String document, String status, Date timeStamp) {
        this.imageComment = imageComment;
        this.comment = comment;
        this.user_id = user_id;
        this.document = document;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    public String getImageComment() {
        return imageComment;
    }

    public void setImageComment(String imageComment) {
        this.imageComment = imageComment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
