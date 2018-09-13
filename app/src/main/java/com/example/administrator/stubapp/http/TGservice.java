package com.example.administrator.stubapp.http;

import com.example.administrator.stubapp.bean.AppBannerResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 文件描述：访问接口
 * 作者：Created by BiJingCun on 2018/9/7.
 */

public interface TGservice {
    @GET("api/AppBanner")
    Observable<String> getUserLearn(@Query("s") String s,@Query("m") String m);
}
