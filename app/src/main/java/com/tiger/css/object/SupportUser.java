package com.tiger.css.object;

public class SupportUser {
    private String url, username, name, phone, email, info, status;

    public SupportUser() {
        this.username = "supportPartner1";
        this.name = "Trần Văn Hưởng";
        this.email = "huongtv.uet@gmail.com";
        this.phone = "0357020398";
        this.status = "offline";
        this.info = "Sinh viên trường Đại học Công nghệ";
        this.url = "https://scontent.fhan5-5.fna.fbcdn.net/v/t1.0-9/60344970_2346263012270612_2518838473306144768_n.jpg?_nc_cat=108&_nc_oc=AQmo_UFjckgzlaCVPxW90BJpXs-AegviK1LP17u51oG3NzB0zltuS6uM8N_K2pr-89k&_nc_ht=scontent.fhan5-5.fna&oh=89d04b209479a42320c9d12cceda6c32&oe=5D65E2C2";
    }

    public SupportUser(String url, String username, String name, String phone, String email, String info, String status) {
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
