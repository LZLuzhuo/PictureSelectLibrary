package me.luzhuo.storedemo.picture.bean;

import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_picture_upload.bean.VideoUploadBean;

public class VideoBean extends VideoUploadBean {
    public VideoBean(VideoFileBean videoFileBean) {
        super(videoFileBean);
    }

    @Override
    public boolean upload() {
        netUrl = "https://66666666666666";
        return false;
    }
}
