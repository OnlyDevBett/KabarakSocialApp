package com.kabarakuniversityforumApp.General_Issues.NotificationsIn;

import java.util.Date;



public class NotificationList extends com.kabarakuniversityforumApp.General_Issues.PostId {
    private String message,status,user_id;
    private Date timeStamp;

    public NotificationList() {
    }

    public NotificationList(String message, String status, String user_id, Date timeStamp) {
        this.message = message;
        this.status = status;
        this.user_id = user_id;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
