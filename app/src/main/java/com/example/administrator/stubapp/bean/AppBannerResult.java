package com.example.administrator.stubapp.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 文件描述：
 * 作者：Created by BiJingCun on 2018/9/11.
 */

public class AppBannerResult {
    private int PageIndex;
    private int PageSize;
    private int PageCount;
    private int TotalCount;
    private String ErrMsg;
    private String MsgCode;
    private List<InfoBean> Info;

    public int getPageIndex() {
        return PageIndex;
    }

    public String getErrMsg() {
        return ErrMsg;
    }

    public void setErrMsg(String mErrMsg) {
        ErrMsg = mErrMsg;
    }

    public String getMsgCode() {
        return MsgCode;
    }

    public void setMsgCode(String mMsgCode) {
        MsgCode = mMsgCode;
    }

    public void setPageIndex(int PageIndex) {
        this.PageIndex = PageIndex;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int PageSize) {
        this.PageSize = PageSize;
    }

    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int PageCount) {
        this.PageCount = PageCount;
    }

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int TotalCount) {
        this.TotalCount = TotalCount;
    }

    public List<InfoBean> getInfo() {
        return Info;
    }

    public void setInfo(List<InfoBean> Info) {
        this.Info = Info;
    }

    public static class InfoBean {
        /**
         * AppBannerId : 1
         * Title : 测试
         * Remark : 测试是控件的哈萨克
         * ImgUrl : /upload/qingjiatiao/20180514061016.jpg
         * TeacherUrls :
         */

        @SerializedName("AppBannerId")
        private int id;
        private String Title;
        private String Remark;
        private String ImgUrl;
        private String TeacherUrls;
        private List<String> Tags;			//标签 多个标签用逗号隔开
        private String TeacherUrl;		//老师头像地址
        private int IsShowUpdate;		//是否显示new
        private int LearnCount;			//学习人数

        public List<String> getTags() {
            return Tags;
        }

        public void setTags(List<String> tags) {
            Tags = tags;
        }

        public String getTeacherUrl() {
            return TeacherUrl;
        }

        public void setTeacherUrl(String teacherUrl) {
            TeacherUrl = teacherUrl;
        }

        public int getIsShowUpdate() {
            return IsShowUpdate;
        }

        public void setIsShowUpdate(int isShowUpdate) {
            IsShowUpdate = isShowUpdate;
        }

        public int getLearnCount() {
            return LearnCount;
        }

        public void setLearnCount(int learnCount) {
            LearnCount = learnCount;
        }

        public int getAppBannerId() {
            return id;
        }

        public void setAppBannerId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String Remark) {
            this.Remark = Remark;
        }

        public String getImgUrl() {
            return ImgUrl;
        }

        public void setImgUrl(String ImgUrl) {
            this.ImgUrl = ImgUrl;
        }

        public String getTeacherUrls() {
            return TeacherUrls;
        }

        public void setTeacherUrls(String TeacherUrls) {
            this.TeacherUrls = TeacherUrls;
        }
    }
}
