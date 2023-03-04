package com.signatureapp.app.az;

public class Users {


    String email,id,name,rank;
    int status;
    // 0 -> unemployed
    // 1 -> employed
    // 2 -> owner
    // 3 -> admin

    public Users() {
    }

    public Users(String email, String id, String name, String rank) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.rank = rank;
    }

    public String getEmail() { return email; }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getRank() { return rank; }
    public int getStatus() { return status; }

    public void setEmail(String email) { this.email = email; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRank(String rank) { this.rank = rank; }
    public void setStatus(int status) { this.status = status; }
}
