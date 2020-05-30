package com.example.test1.item;

public class gridViewStore_item {
    private String text;
    private int imageId;
    public gridViewStore_item(String text, int imageId){
        this.text=text;
        this.imageId=imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public String getText() {
        return text;
    }
}
