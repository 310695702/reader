package com.example.mybook.bean;

import java.util.Date;
import java.util.List;

public class ShuPingBean {
    private Mydata data;

    @Override
    public String toString() {
        return "ShuPingBean{" +
                "data=" + data +
                '}';
    }

    public ShuPingBean() {
    }

    public ShuPingBean(Mydata data) {
        this.data = data;
    }



    public Mydata getData() {
        return data;
    }

    public void setData(Mydata data) {
        this.data = data;
    }

    public class Mydata {
        private List<listdata> listdata;

        @Override
        public String toString() {
            return "Mydata{" +
                    "listdata=" + listdata +
                    '}';
        }

        public Mydata() {
        }

        public Mydata(List<Mydata.listdata> listdata) {
            this.listdata = listdata;
        }

        public List<Mydata.listdata> getListdata() {
            return listdata;
        }

        public void setListdata(List<Mydata.listdata> listdata) {
            this.listdata = listdata;
        }

        public class listdata {
            private String userName;
            private Integer id;
            private String content;
            private String creatDate;

            @Override
            public String toString() {
                return "listdata{" +
                        "userName='" + userName + '\'' +
                        ", id=" + id +
                        ", content='" + content + '\'' +
                        ", creatDate=" + creatDate +
                        '}';
            }

            public listdata() {
            }

            public listdata(String userName, Integer id, String content, String creatDate) {
                this.userName = userName;
                this.id = id;
                this.content = content;
                this.creatDate = creatDate;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
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
        }
    }

}
