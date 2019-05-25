package com.tiger.css.object;

public class Patron {
    private String url, username, name, phone, email, info, status;

    public Patron() {
        this.url = "https://scontent.fhan5-7.fna.fbcdn.net/v/t1.0-9/58714788_2233408590108222_6277549909507833856_n.jpg?_nc_cat=103&_nc_oc=AQmJKv3IhgGrW0HcZCmJjSmLx3bPihM2yfkDj5h3Ipy5H7fcQbIUTRFdjEauhdsKjws&_nc_ht=scontent.fhan5-7.fna&oh=ea9cf9ae864b9c60267debcd9484281d&oe=5D59990D";
        this.username = "patron1";
        this.name = "Phùng Quang Huy";
        this.email = "qhuy1501@gmail.com";
        this.phone = "0948282897";
        this.status = "available";
        this.info = "Sinh viên trường Đại học Công nghệ";
    }

    public Patron(String url, String username, String name, String phone, String email, String info, String status) {
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
