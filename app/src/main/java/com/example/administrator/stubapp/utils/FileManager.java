package com.example.administrator.stubapp.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.example.administrator.stubapp.bean.LiveLesson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;

public class FileManager {

    public static final String ROOT_NAME = "com.tiangui.civil";
    public static final String CACHE_NAME = "Cache";
    public static final String IMAGE_NAME = "Image";
    /**
     * 分隔符.
     */
    public final static String FILE_EXTENSION_SEPARATOR = ".";

    public static final String ROOT_PATH = File.separator + ROOT_NAME
            + File.separator;
    public static final String CACHE_PATH_NAME = File.separator + CACHE_NAME
            + File.separator;
    public static final String IMAGE_PATH_NAME = File.separator + IMAGE_NAME
            + File.separator;

    public static String getRootPath(Context appContext) {

        String rootPath = null;
        if (checkMounted()) {
            rootPath = getRootPathOnSdcard();
        } else {
            rootPath = getRootPathOnPhone(appContext);
        }
        return rootPath;
    }

    public static String getRootPathOnSdcard() {
        File sdcard = Environment.getExternalStorageDirectory();
        String rootPath = sdcard.getAbsolutePath() + ROOT_PATH;
        return rootPath;
    }

    public static String getRootPathOnPhone(Context appContext) {
        File phoneFiles = appContext.getFilesDir();
        String rootPath = phoneFiles.getAbsolutePath() + ROOT_PATH;
        return rootPath;
    }

    public static boolean checkMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }


    // 缓存整体路径
    public static String getCacheDirPath(Context appContext) {

        String imagePath = getRootPath(appContext) + CACHE_PATH_NAME;
        return imagePath;
    }

    // 图片缓存路径
    public static String getImageCacheDirPath(Context appContext) {

        String imagePath = getCacheDirPath(appContext) + IMAGE_PATH_NAME;
        return imagePath;
    }

    // 创建一个图片文件
    public static File getImgFile(Context context) {
        File file = new File(getImageCacheDirPath(context));
        if (!file.exists()) {
            file.mkdirs();
        }
        String imgName = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File imgFile = new File(file.getAbsolutePath() + File.separator
                + "IMG_" + imgName + ".jpg");
        return imgFile;
    }

    public static boolean hasSdcard() {
        String status = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(status);
    }

    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        return deleteFile(new File(path));
    }

    public static boolean deleteFile(File file) {
        if (file == null)
            throw new NullPointerException("file is null");
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }

        File[] files = file.listFiles();
        if (files == null)
            return true;
        for (File f : files) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    public static void delete(String dir, FilenameFilter filter) {
        if (TextUtils.isEmpty(dir))
            return;
        File file = new File(dir);
        if (!file.exists())
            return;
        if (file.isFile())
            file.delete();
        if (!file.isDirectory())
            return;

        File[] lists = null;
        if (filter != null)
            lists = file.listFiles(filter);
        else
            lists = file.listFiles();

        if (lists == null)
            return;
        for (File f : lists) {
            if (f.isFile()) {
                f.delete();
            }
        }
    }

    /**
     * 获得不带扩展名的文件名称
     *
     * @param filePath 文件路径
     * @return
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }
        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0,
                    extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1,
                extenPosi) : filePath.substring(filePosi + 1));
    }

    /**
     * 判断是否存在文件
     *
     * @param path
     * @return
     */
    public static boolean existFile(String path) {
        File path1 = new File(path);
        return path1.exists();
    }

    /**
     * 写入文件
     *
     * @param mResponseBody
     * @param mFile
     * @param mInfo
     */
    public static void writeCache(ResponseBody mResponseBody, File mFile, LiveLesson mInfo) throws IOException {
        //获取文件所在的目录 不存在创建
        if (!mFile.getParentFile().exists()) {
            mFile.getParentFile().mkdirs();
        }
        //设置文件总长度
        long allLenght;
        if (mInfo.getSize() == 0) {
            allLenght = mResponseBody.contentLength();
        } else {
            allLenght = mInfo.getSize();
        }
        /**
         * FileChannel 优势：
         多线程并发读写，并发性；
         IO读写性能提高（OS负责），也可引做共享内存，减少IO操作，提升并发性；
         应用crash，保证这部分内容还能写的进去文件。在我们调用channel.write(bytebuffer)之后，
         具体何时写入磁盘、bytebuffer中内容暂存于哪里（os cache）等相关一系列问题，就交由OS本身负责了
         */
        try {
            FileChannel channelOut = null;
            RandomAccessFile randomAccessFile = null;
            //mFiel 文件本身  rwd  以读取、写入方式打开指定文件
            randomAccessFile = new RandomAccessFile(mFile, "rwd");
            channelOut = randomAccessFile.getChannel();
            MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE,
                    mInfo.getDownlength(), allLenght - mInfo.getDownlength());
            byte[] buffer = new byte[1024 * 8];
            int len;
            int record = 0;
            while ((len = mResponseBody.byteStream().read(buffer)) != -1) {
                mappedBuffer.put(buffer, 0, len);
                record += len;
            }
            mResponseBody.byteStream().close();
            if (channelOut != null) {
                channelOut.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } catch (FileNotFoundException mE) {
            mE.printStackTrace();
        }
    }
}
