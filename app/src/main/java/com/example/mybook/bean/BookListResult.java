package com.example.mybook.bean;

import java.util.List;

public class BookListResult {
    private List<BookData> data;

    public BookListResult(){}

    public BookListResult(List<BookData> data) {
        this.data=data;
    }

    public List<BookData> getData() {
        return data;
    }

    public void setData(List<BookData> data) {
        this.data = data;
    }

    public class BookData{
        public String bookname;
        public String bookfile;
        public String bookclass;
        public String bookpic;

        public BookData() {
        }

        public BookData(String bookname, String bookfile, String bookpic) {
            this.bookname = bookname;
            this.bookfile = bookfile;
            this.bookpic = bookpic;
        }

        public BookData(String bookclass) {
            this.bookclass = bookclass;
        }

        public BookData(String bookname, String bookfile) {
            this.bookname = bookname;
            this.bookfile = bookfile;
        }

        public String getBookname() {
            return bookname;
        }

        public void setBookname(String bookname) {
            this.bookname = bookname;
        }

        public String getBookfile() {
            return bookfile;
        }

        public void setBookfile(String bookfile) {
            this.bookfile = bookfile;
        }

        public String getBookclass() {
            return bookclass;
        }

        public void setBookclass(String bookclass) {
            this.bookclass = bookclass;
        }

        public String getBookpic() {
            return bookpic;
        }

        public void setBookpic(String bookpic) {
            this.bookpic = bookpic;
        }
    }


}

