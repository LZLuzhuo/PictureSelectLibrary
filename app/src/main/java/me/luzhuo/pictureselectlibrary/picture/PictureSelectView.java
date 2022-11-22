package me.luzhuo.pictureselectlibrary.picture;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_picture_upload.PictureSelectUploadView;
import me.luzhuo.lib_picture_upload.bean.AudioUploadBean;
import me.luzhuo.lib_picture_upload.bean.ImageUploadBean;
import me.luzhuo.lib_picture_upload.bean.VideoUploadBean;
import me.luzhuo.storedemo.picture.bean.AudioBean;
import me.luzhuo.storedemo.picture.bean.ImageBean;
import me.luzhuo.storedemo.picture.bean.VideoBean;

public class PictureSelectView extends PictureSelectUploadView {
    public PictureSelectView(@NonNull Context context) {
        super(context);
    }

    public PictureSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public VideoUploadBean getVideoUploadBean(VideoFileBean data) {
        return new VideoBean(data);
    }

    @Override
    public AudioUploadBean getAudioUploadBean(AudioFileBean data) {
        return new AudioBean(data);
    }

    @Override
    public ImageUploadBean getImageUploadBean(ImageFileBean data) {
        return new ImageBean(data);
    }
}
