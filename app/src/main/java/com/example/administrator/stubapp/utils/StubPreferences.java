package com.example.administrator.stubapp.utils;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * 文件描述：
 * 作者：Created by BiJingCun on 2018/8/16.
 */

public class StubPreferences {
    private static final String TAG = "Preferences";
    private static SharedPreferences mSp;
    private static final String preferences_name = "Preferences";

    public static void initPreferences(Context context) {
        try {
            if (mSp != null) {
                return;
            }
            mSp = context.getSharedPreferences(preferences_name, Activity.MODE_PRIVATE);
        } catch (Exception e) {
        }
    }

    public static void delete(String name) {
        try {
            Editor editor = mSp.edit();
            editor.remove(name);
            editor.commit();
        } catch (Exception e) {
        }
    }

    public static synchronized String getStringValue(String key) {
        try {
            if (null == mSp)
                return null;
            else
                return mSp.getString(key, "");
        } catch (Exception e) {
            return "";
        }
    }

    public static synchronized Integer getIntegerValue(String key) {
        try {
            if (null == mSp)
                return 0;
            else
                return mSp.getInt(key, 0);
        } catch (Exception e) {
            return -1;
        }
    }

    public static synchronized Boolean getBooleangerValue(String key) {
        try {
            if (null == mSp){
                return false;
            } else{
                return mSp.getBoolean(key, false);
            }
        } catch (Exception e) {
            DebugUtil.d("tag",e.toString());
            return false;
        }
    }

    /**
     * 带有默认值的
     * @param key
     * @param b 默认值
     * @return
     */
    public static synchronized Boolean getBooleangerValue(String key , boolean b) {
        try {
            if (null == mSp)
                return b;
            else
                return mSp.getBoolean(key, b);
        } catch (Exception e) {
            return b;
        }
    }

    public static synchronized long getLongValue(String key) {
        try {
            if (null == mSp)
                return 0;
            else
                return mSp.getLong(key, 0);
        } catch (Exception e) {
            return -1;
        }
    }

    public static synchronized Integer getIntegerValueDefault(String key) {
        try {
            if (null == mSp)
                return -1;
            else
                return mSp.getInt(key, -1);
        } catch (Exception e) {
            return -1;
        }
    }

    public static synchronized void setStringValue(String key, String value) {
        try {
            if (null != mSp) {
                Editor editor = mSp.edit();
                editor.putString(key, value);
                editor.commit();
            }
        } catch (Exception e) {
            DebugUtil.e(TAG, "set String key-(String)value from preferences xml and key:" + key
                    + ",value:" + value);
        }
    }

    public static synchronized void setIntegerValue(String key, Integer value) {
        try {
            if (null != mSp) {
                Editor editor = mSp.edit();
                editor.putInt(key, value);
                editor.commit();
            }
        } catch (Exception e) {
        }
    }

    public static synchronized void setBooleanValue(String key, Boolean value) {
        try {
            if (null != mSp) {
                Editor editor = mSp.edit();
                editor.putBoolean(key, value);
                editor.commit();
            }
        } catch (Exception e) {
        }
    }

    public static synchronized void clearAllConfig() {
        try {
            if (null != mSp) {
                Editor editor = mSp.edit();
                editor.clear();
                editor.commit();
            }
        } catch (Exception e) {
        }
    }

    public static void destory() {
        mSp = null;
    }
}
