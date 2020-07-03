package com.example.test1.item;

public class gridViewChoice_item {
    private String text;
    private int imageId;
    public gridViewChoice_item(String text, int imageId){
        this.text=text;
        this.imageId=imageId;
    }

    public String getText() {
        return text;
    }

    public int getImageId() {
        return imageId;
    }
}
