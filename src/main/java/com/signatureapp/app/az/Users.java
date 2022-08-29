package com.signatureapp.app.az;

public class Users {


    String email,id,name,rank;


    public Users() {
    }

    public Users(String email, String id, String name, String rank) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.rank = rank;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
