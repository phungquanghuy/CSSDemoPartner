package com.tiger.css.object;

public class Partner {
    private String url, username, name, phone, email, info, status;

    public Partner() {
        this.username = "supportPartner1";
    }

    public Partner(String url, String username, String name, String phone, String email, String info, String status) {
        this.url = url;
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.info = info;
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
