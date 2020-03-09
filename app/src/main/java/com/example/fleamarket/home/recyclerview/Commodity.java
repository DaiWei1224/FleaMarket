package com.example.fleamarket.home.recyclerview;

public class Commodity {
    private int mImage;
    private String mName;
    private String mContent;
    private String mPrice;

    public Commodity(int image, String name, String content, String price){
        this.mImage = image;
        this.mName = name;
        this.mContent = content;
        this.mPrice = price;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int image) {
        mImage = image;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        this.mPrice = price;
    }

}