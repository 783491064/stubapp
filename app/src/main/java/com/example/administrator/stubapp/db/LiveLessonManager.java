package com.example.administrator.stubapp.db;

import android.text.TextUtils;

import com.example.administrator.stubapp.bean.LiveLesson;
import com.example.administrator.stubapp.db.dao.LiveLessonDao;
import com.example.administrator.stubapp.utils.FileManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 文件描述：视频管理器
 * 作者：Created by BiJingCun on 2018/9/18.
 */

public class LiveLessonManager {
    private static LiveLessonManager instance;
    private Map<Long, LiveLesson> songLibrary;
    private final LiveLessonDao dao;

    public static LiveLessonManager getInstance() {
        if (instance == null) {
            instance = new LiveLessonManager();
        }
        return instance;
    }

    public LiveLessonManager() {
        dao = new LiveLessonDao();
        songLibrary = new LinkedHashMap<>();
        updateLiveLessonLibrary();
    }

    /**
     * 异步更新视频信息
     */
    private void updateLiveLessonLibrary() {
        Observable.create(
                new Observable.OnSubscribe<List<LiveLesson>>() {
                    @Override
                    public void call(Subscriber<? super List<LiveLesson>> subscriber) {
                        subscriber.onNext(dao.queryAll());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(new Func1<List<LiveLesson>, Observable<LiveLesson>>() {
                    @Override
                    public Observable<LiveLesson> call(List<LiveLesson> songs) {
                        return Observable.from(songs);
                    }
                })
                .map(new Func1<LiveLesson, LiveLesson>() {
                    @Override
                    public LiveLesson call(LiveLesson mLiveLesson) {
                        if (mLiveLesson.getDownload() == mLiveLesson.DOWNLOAD_COMPLETE && !TextUtils.isEmpty(mLiveLesson.getPath())) {
                            if (!FileManager.existFile(mLiveLesson.getPath())) {
                                mLiveLesson.setDownload(mLiveLesson.DOWNLOAD_NONE);
                                insertOrUpdateLiveLesson(mLiveLesson);
                            }
                        }
                        return mLiveLesson;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LiveLesson>() {
                    @Override
                    public void call(LiveLesson mLiveLesson) {
                        songLibrary.put(mLiveLesson.getId(), mLiveLesson);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    private void insertOrUpdateLiveLesson(final LiveLesson mLiveLesson) {
        Observable.create(
                new Observable.OnSubscribe<LiveLesson>() {
                    @Override
                    public void call(Subscriber<? super LiveLesson> subscriber) {
                        updateSongFromLibrary(mLiveLesson);
                        dao.insertOrUpdateLiveLesson(mLiveLesson);
                        subscriber.onNext(mLiveLesson);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LiveLesson>() {
                    @Override
                    public void call(LiveLesson mLiveLesson) {
                        songLibrary.put(mLiveLesson.getId(), mLiveLesson);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    public void updateSongFromLibrary(LiveLesson mLiveLesson) {
        if (songLibrary.containsKey(mLiveLesson.getId())) {
            LiveLesson mLive = songLibrary.get(mLiveLesson.getId());
            mLiveLesson.setDownload(mLive.getDownload());
            mLiveLesson.setPath(mLive.getPath());
        }
    }

    public LiveLesson querySong(long sid) {
        if (songLibrary.containsKey(sid)) {
            return songLibrary.get(sid);
        }
        return null;
    }

    /**
     * 删除数据库的视频信息，包括下载中的和已经下载完成的
     */
    public void deleLiveLesson(final LiveLesson mLiveLesson) {
        Observable.create(new Observable.OnSubscribe<LiveLesson>() {
            @Override
            public void call(Subscriber<? super LiveLesson> mSubscriber) {
                if (songLibrary.containsKey(mLiveLesson.getId())) {
                    dao.deleteSong(mLiveLesson);
                }
                mSubscriber.onNext(mLiveLesson);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LiveLesson>() {
                    @Override
                    public void call(LiveLesson mLiveLesson) {
                        songLibrary.remove(mLiveLesson.getId());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable mThrowable) {

                    }
                });
    }
}
