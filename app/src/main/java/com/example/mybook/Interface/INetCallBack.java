package com.example.mybook.Interface;

public interface INetCallBack {
    void onSuccess(String response);
    void onFailure(Exception e);
}
