package com.example.mohitsharma.chatapp;

public class FriendsRequest {
    public String request_type;


    public FriendsRequest(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public FriendsRequest() {

    }
}
