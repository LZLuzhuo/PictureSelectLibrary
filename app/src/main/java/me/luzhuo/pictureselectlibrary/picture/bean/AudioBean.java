package me.luzhuo.storedemo.picture.bean;

import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_picture_upload.bean.AudioUploadBean;

public class AudioBean extends AudioUploadBean {
    public AudioBean(AudioFileBean audioFileBean) {
        super(audioFileBean);
    }

    @Override
    public boolean upload() {
        netUrl = "https://66666666666666";
        return false;
    }
}
