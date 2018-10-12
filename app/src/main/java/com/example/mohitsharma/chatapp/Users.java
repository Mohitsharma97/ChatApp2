package com.example.mohitsharma.chatapp;

/**
 * Created by mohit sharma on 6/9/2018.
 */

public class Users {

    public String name;
    public String image;
    public String status;
    public String thum_image;





    public Users(String name, String image, String status,String thum_image) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thum_image = thum_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getThum_image() {

        return thum_image;
    }

    public void setThum_image(String thum_image) {
        this.thum_image = thum_image;
    }

    public Users(){}


}
