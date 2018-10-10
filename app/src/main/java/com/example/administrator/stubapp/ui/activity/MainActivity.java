package com.example.administrator.stubapp.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.stubapp.R;
import com.example.administrator.stubapp.app.AppManager;
import com.example.administrator.stubapp.bean.LiveLesson;
import com.example.administrator.stubapp.customView.DownloadButton;
import com.example.administrator.stubapp.customView.LoadingPage;
import com.example.administrator.stubapp.db.LiveLessonManager;
import com.example.administrator.stubapp.download.DownState;
import com.example.administrator.stubapp.download.HttpDownManager;
import com.example.administrator.stubapp.download.downloadListener.HttpDownOnNextListener;
import com.example.administrator.stubapp.presenter.MainPresenter;
import com.example.administrator.stubapp.base.BaseMVPActivity;
import com.example.administrator.stubapp.view.MainView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;

import static com.example.administrator.stubapp.download.DownState.*;

public class MainActivity extends BaseMVPActivity<MainView, MainPresenter> implements MainView, HttpDownManager.DownloadObserver {

    private MainPresenter mPresenter;
    @BindView(R.id.bt_getData)
    Button bt_getData;
    @BindView(R.id.btn_download)
    DownloadButton btn_download;
    @BindView(R.id.fl_content)
    RelativeLayout fl_content;
    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.re)
    Button re;
    private LoadingPage mLoadingPage;
    private long firstExitTime = 0;
    private static final int EXIT_TIME = 2000;
    private File outFile;//下载文件存储的位置
    private HttpDownManager mHttpDownManager;//下载请求的类
    private LiveLessonManager mDownUtil;
    private String mUrl;

    @Override
    protected void setStatusBarColor() {

    }

    @Override
    protected void preInit() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initView() {
        mLoadingPage = new LoadingPage(mContext) {
            @Override
            public void refresh() {

            }

            @Override
            public void toLogin() {

            }
        };
        fl_content.addView(mLoadingPage);
        //测试下载视频
        // 地址
//        mUrl = "http://appdlc.hicloud.com/dl/appdl/application/apk/54/54da7ff6a97f44b3950f8debc8b8b882/cn.eclicks.drivingtest.1705151637.apk?sign=c9d81011e610010520007000@7ED2A239A80FA54D27BEFAACCC1AB7F3&cno=4010001&source=HiAd&listId=40&position=2&hcrId=0D855BF76D854F5ABD8EF2C81BCF46AA&extendStr=a28f8b52f26ff78840b24bedd884271d%3BcdrInfo%3A20170518203514aps09188440%5E%7BopType%7D%5E7798%5EC188770%5E40%5E2%5E1c578c5865f511e6bc3800163e0b0f53%5E17453%5Ec1737895f9ab9f0f2ab194c30e1e76f6d97d4d7a814b70bbe407de74ebc30d70%5E%5EU0NFOn5TUkM6NDA%5E2017-05-18+20%3A35%3A14%5E5%5E%E8%8D%A3%E8%80%807%5E0.000120%5E1%5E2.17%5E2.17%5E0.8%5E900086000000033869%5E20358%5E%5E%5E%5E7.2.3%5E1495110901735%5E0%3BisAdTag%3A0%3B%3BserviceType%3A0%3Bisshake%3A0%3Bs%3A1495110914770%3Btrace%3A7a33dd5251e64e80ba4bb2f38ada8f8b%3BlayoutId%3A806929&encryptType=1";
        mUrl = "http://cdnaliyunv.tianguiedu.com/201803/e342b754-6ae3-4d55-bef6-dbfef14d94c0/low.m3u8";
        mHttpDownManager = HttpDownManager.getInstance();
        mHttpDownManager.registerObserver(this);
        /**
         * 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
         */
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.bt_getData,R.id.re})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_getData:
                if (mLoadingPage.show()) {
                    mPresenter.getData();
                }
                break;
            case R.id.re:
                //http://cdnaliyunv.tianguiedu.com/201803/e342b754-6ae3-4d55-bef6-dbfef14d94c0/low.m3u8
                // id  153
//                setDownload();
                break;
        }
    }

    /**
     * 测试开始下载 断点续传
     */
    private LiveLesson setDownload(LiveLesson mLiveLesson) {
        if (mLiveLesson == null) {
            int sid=153;
            outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "video" + sid);
            String path = outFile.getAbsolutePath();
            LiveLesson lesson=new LiveLesson(mUrl);
            lesson.setPath(path);
            lesson.setId((long) 153);
            lesson.setState(DownState.START);
            lesson.setListener(downLoadListener);//数据进度监听
            //从未下载
            Toast.makeText(mContext, "还没有下载过", Toast.LENGTH_SHORT).show();
            btn_download.setText("下载");
            return lesson;
        } else {
            if (mLiveLesson.getState() == DownState.DOWN) {
                btn_download.setText("下载中");
                btn_download.setState(DownloadButton.STATUS_PROGRESS_DOWNLOADING);
                mLiveLesson.setListener(downLoadListener);//目的是调用updateProgress中的setProgress(progress);更新进度
                mHttpDownManager.startDown(mLiveLesson,true);
            } else if (mLiveLesson.getState() == DownState.PAUSE) {
                btn_download.setText("下载暂停");
                btn_download.setState(DownloadButton.STATUS_PROGRESS_PAUSE);
                mLiveLesson.setListener(downLoadListener);//目的是调用updateProgress中的setProgress(progress);更新进度
                mHttpDownManager.pause(mLiveLesson);
            } else if (mLiveLesson.getState() == DownState.FINISH) {
                btn_download.setText("下载完成");
                btn_download.setState(DownloadButton.STATUS_PROGRESS_FINISH);
            }

        }
        return mLiveLesson;
    }

    @Override
    protected void initData() {
        if (mLoadingPage!=null&&mLoadingPage.show()) {
            mPresenter.getData();
        }
    }

    @Override
    protected MainPresenter initPresenter() {
        mPresenter = new MainPresenter();
        return mPresenter;
    }

    @Override
    public void showData() {
        Toast.makeText(mContext, "获取数据成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        startProgressDialog("");
    }

    @Override
    public void cancleProgress() {
        //模拟假设获取到了下载视频的地址了和ID了
        stopProgressDialog();
        int sid=153;
        mDownUtil = LiveLessonManager.getInstance();
        LiveLesson mLiveLesson = mDownUtil.queryVideo(sid);
//        if (mLiveLesson == null) {
//            ///storage/emulated/0/Download 所有文件的存储路径
//            outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "video" + sid);
//            //重来没有下载过，初始化下载条目的数据；
//            mLiveLesson = new LiveLesson(mUrl);
//            mLiveLesson.setListener(downLoadListener);//数据进度监听
//            mLiveLesson.setId((long) 153);
//            mLiveLesson.setPath(outFile.getAbsolutePath());
//            mLiveLesson.setState(DownState.START);
//            mLiveLesson.setDownlength((long) 0);
//            mLiveLesson.setSize((long) 0);
//            //数据库保存下载数据
//            mDownUtil.insertLiveLesson(mLiveLesson);
//        }
        final LiveLesson liveLesson = setDownload(mLiveLesson);
        btn_download.setStateChangeListener(new DownloadButton.StateChangeListener() {
            @Override
            public void onPauseTask() {
                mHttpDownManager.pause(liveLesson);
            }

            @Override
            public void onFinishTask() {
                Toast.makeText(mContext, "去安装程序", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadingTask() {
                pb.setMax(100);
                //1开始下载 先查询数据库下过么
                LiveLesson lesson = mDownUtil.queryVideo(liveLesson.getId());
                if(lesson==null){
                    //2没下过的 先插入数据库 然后开始下载
                    mDownUtil.insertLiveLesson(liveLesson);
                    mHttpDownManager.startDown(liveLesson,false);
                }else{
                    //3下过的就继续下载
                    if (lesson.getState() != DownState.FINISH) {
                        mHttpDownManager.startDown(liveLesson,true);
                    }
                }

            }
        });
    }

    @Override
    public void onError(String e) {

    }

    /**
     * 覆盖返回键（可选）.
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstExitTime < EXIT_TIME) {// 两次按返回键的时间小于2秒就退出应用
            AppManager.getInstance().appExit();
        } else {
            firstExitTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次回到桌面",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private HttpDownOnNextListener downLoadListener = new HttpDownOnNextListener() {
        @Override
        public void onNext(Object o) {
            Toast.makeText(MainActivity.this,"完成一个",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStart() {
            Toast.makeText(MainActivity.this, "开始下载了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPuse() {
            Toast.makeText(MainActivity.this, "下载暂停了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete() {
            Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void updateProgress(long readLength, long countLength) {
            Log.d("下载","下载进度:"+readLength+"总长度："+countLength);
            int progress = (int) ((readLength * 100) / countLength);
            pb.setProgress(progress);
        }
    };


    @Override
    public void onDownloadStateChanged(final LiveLesson info) {
        if (info.getState() == DownState.FINISH) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDownUtil != null && info != null)
                                mDownUtil.updateLiveLesson(info);
                        }
                    });
                }
            }).start();
        }

    }

    @Override
    public void onDownloadProgressed(LiveLesson info) {
        if (info != null) {
            pb.setProgress((int) (100 * info.getDownlength() / info.getSize()));
        }
    }
}
