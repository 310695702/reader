package com.example.mybook.bean;

public class ShupingPostbean {
    private String userName;
    private String content;
    private String creatDate;
    private String bookname;

    @Override
    public String toString() {
        return "ShupingPostbean{" +
                "userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                ", creatDate='" + creatDate + '\'' +
                ", bookname='" + bookname + '\'' +
                '}';
    }

    public ShupingPostbean() {
    }

    public ShupingPostbean(String userName, String content, String creatDate, String bookname) {
        this.userName = userName;
        this.content = content;
        this.creatDate = creatDate;
        this.bookname = bookname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatDate() {
        return creatDate;
    }

    public void setCreatDate(String creatDate) {
        this.creatDate = creatDate;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }
}
