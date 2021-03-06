package com.example.fleamarket.net;

import java.io.Serializable;
import java.util.Date;

public class Commodity implements Serializable {
    private String mCommodityID;
    private String mCommodityName;
    private String mCommodityDetail;
    private String mPrice;
    private String mSellerID;
    private String mSellerName;
    private String mArea;
    private Date mPostTime;
    private String mPostTimeString;
    private boolean havePhoto;
    private NetImage mCommodityPhoto;
    private NetImage mAvatar;

    public String getCommodityID() {
        return mCommodityID;
    }

    public void setCommodityID(String commodityID) {
        mCommodityID = commodityID;
    }

    public String getCommodityName() {
        return mCommodityName;
    }

    public void setCommodityName(String commodityName) {
        mCommodityName = commodityName;
    }

    public String getCommodityDetail() {
        return mCommodityDetail;
    }

    public void setCommodityDetail(String commodityDetail) {
        mCommodityDetail = commodityDetail;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getSellerID() {
        return mSellerID;
    }

    public void setSellerID(String sellerID) {
        mSellerID = sellerID;
    }

    public String getSellerName() {
        return mSellerName;
    }

    public void setSellerName(String sellerName) {
        mSellerName = sellerName;
    }

    public String getArea() {
        return mArea;
    }

    public void setArea(String area) {
        mArea = area;
    }

    public Date getPostTime() {
        return mPostTime;
    }

    public void setPostTime(Date postTime) {
        mPostTime = postTime;
    }

    public boolean isHavePhoto() {
        return havePhoto;
    }

    public void setHavePhoto(boolean havePhoto) {
        this.havePhoto = havePhoto;
    }

    public NetImage getCommodityPhoto() {
        return mCommodityPhoto;
    }

    public void setCommodityPhoto(NetImage commodityPhoto) {
        mCommodityPhoto = commodityPhoto;
    }

    public NetImage getAvatar() {
        return mAvatar;
    }

    public void setAvatar(NetImage avatar) {
        mAvatar = avatar;
    }

    public String getPostTimeString() {
        return mPostTimeString;
    }

    public void setPostTimeString(String postTimeString) {
        mPostTimeString = postTimeString;
    }

}