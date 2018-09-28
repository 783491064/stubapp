package com.example.administrator.stubapp.download.downloadListener;

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

    public DownloadResponseBody(ResponseBody mBody, DownloadProgressListener mListener) {
        this.responseBody = mBody;
        this.progressListener = mListener;
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
            long totalBytesRead = 0L;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                if(null!=progressListener){
                    progressListener.update(totalBytesRead,responseBody.contentLength(),bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}
