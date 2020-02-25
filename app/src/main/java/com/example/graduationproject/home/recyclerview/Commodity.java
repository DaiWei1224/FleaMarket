package com.example.graduationproject.home.recyclerview;

public class Commodity {
    private int mImage;
    private String mContent;
    private String mPrice;

    public Commodity(int image, String content, String price){
        this.mImage = image;
        this.mContent = content;
        this.mPrice = price;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int image) {
        mImage = image;
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