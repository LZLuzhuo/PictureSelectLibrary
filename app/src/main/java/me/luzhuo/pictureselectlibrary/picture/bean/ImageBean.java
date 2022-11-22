package me.luzhuo.storedemo.picture.bean;

import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_picture_upload.bean.ImageUploadBean;

public class ImageBean extends ImageUploadBean {
    public ImageBean(ImageFileBean imageFileBean) {
        super(imageFileBean);
    }

    @Override
    public boolean upload() {
        netState.set(UploadStateCompressing);
        netUrl = "https://66666666666666";
        netState.set(UploadStateEnded);
        return true;
    }
}
