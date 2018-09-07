package com.example.administrator.stubapp.view;

/**
 * 文件描述：所有View 的基类
 * 作者：Created by BiJingCun on 2018/9/7.
 */

public interface IView {
    //展示加载进度旋转框
    void showDialog();
    //加载旋转框消失
    void dismessDialog();
    //显示错误信息
    void onError(String e);
}
