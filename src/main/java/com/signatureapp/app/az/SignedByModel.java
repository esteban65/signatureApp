package com.signatureapp.app.az;

public class SignedByModel {

    String userId;

    public SignedByModel() {
    }

    public SignedByModel(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
