package com.example.mybook.bean;

public class User {
    private String Phone;
    private String name;
    private String password;

    public User(String Phone, String name, String password) {
        this.Phone = Phone;
        this.name = name;
        this.password = password;
    }
    public User(String Phone, String password) {
        this.Phone = Phone;
        this.password = password;
    }
    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
