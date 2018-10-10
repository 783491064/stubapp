package com.example.administrator.stubapp.download.downloadListener;

import android.text.TextUtils;
import android.widget.TextView;

import com.example.administrator.stubapp.utils.StubPreferences;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 文件描述：自定义进度的body
 * 作者：Created by BiJingCun on 2018/9/21.
 */

public class DownloadResponseBody  extends ResponseBody{
    private ResponseBody responseBody;
    private DownloadProgressListener progressListener;
    private BufferedSource bufferedSource;
    private int size=0;
    private String id;
    int hasDown = 0;

    public DownloadResponseBody(ResponseBody mBody, DownloadProgressListener mListener,int size,String id) {
        this.responseBody = mBody;
        this.progressListener = mListener;
        this.size=size;
        this.id=id;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if(bufferedSource==null){
            bufferedSource= Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source){
        return new ForwardingSource(source) {
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                String stringValue = StubPreferences.getStringValue(id);//还没有下载的片段数
                if(!TextUtils.isEmpty(stringValue)){
                    String[] split = stringValue.split(",");
                    if(null!=progressListener){
                        if(bytesRead==-1){//下载完一个片段
                            hasDown=size-split.length+1;//下载完一个片段 hasDown增加一个
                            if(hasDown==size){
                                progressListener.update(hasDown,size,true);
                            }else{
                                progressListener.update(hasDown,size,false);
                            }
                        }
                    }

                }
                return bytesRead;
            }
        };
    }
}
