package com.signatureapp.app.az;

public class FIleModel {

    String file_name,file_url,date,generatedBy,status;


    public FIleModel() {
    }

    public FIleModel(String file_name, String file_url, String date, String generatedBy, String status) {
        this.file_name = file_name;
        this.file_url = file_url;
        this.date = date;
        this.generatedBy = generatedBy;
        this.status = status;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
