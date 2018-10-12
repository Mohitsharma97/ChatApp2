package com.example.mohitsharma.chatapp;

public class Messages {
 private String message,type;
 private long time;
 private String from;
 private boolean seend;
 private String to;


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Messages(String from,String to) {

        this.from = from;
        this.to=to;
    }


    public Messages(String message, boolean seend, String type, long time){
        this.message = message;
        this.seend=seend;
        this.time=time;
        this.type=type;


    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeend() {
        return seend;
    }

    public void setSeend(boolean seend) {
        this.seend = seend;
    }

    public Messages() {
    }

}
