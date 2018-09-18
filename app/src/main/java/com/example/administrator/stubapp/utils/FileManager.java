package com.example.administrator.stubapp.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {

    public static final String ROOT_NAME = "com.tiangui.civil";
    public static final String CACHE_NAME = "Cache";
    public static final String IMAGE_NAME = "Image";
    /** 分隔符. */
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
     * @param path
     * @return
     */
    public static boolean existFile(String path) {
        File path1 = new File(path);
        return path1.exists();
    }

}
