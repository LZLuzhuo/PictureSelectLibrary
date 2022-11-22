package me.luzhuo.pictureselectlibrary.picture;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import me.luzhuo.lib_core.ui.toast.ToastManager;
import me.luzhuo.lib_file.bean.CheckableFileBean;
import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_picture_compress.PictureSelectCompressView;
import me.luzhuo.lib_picture_select_view.bean.ImageNetBean;
import me.luzhuo.lib_picture_select_view.bean.VideoNetBean;
import me.luzhuo.lib_picture_select_view.callback.PictureViewSelectCallback;
import me.luzhuo.lib_picture_upload.PictureSelectUploadView;
import me.luzhuo.pictureselectlibrary.R;

public class PictureActivity extends AppCompatActivity {
    private static final String TAG = PictureActivity.class.getSimpleName();
    private PictureSelectUploadView picture_select;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        picture_select = findViewById(R.id.picture_select);




        /*picture_select.setPictureListener(new PictureViewSelectCallback() {
            @Override
            public void onSelect(List<? extends CheckableFileBean> fileBeans) {
                boolean notSupport = notSupport(fileBeans);
                if (notSupport) ToastManager.show2(PictureActivity.this, "存在不支持的文件格式");
                picture_select.addDatas(fileBeans);
            }

            @Override
            public void onDelete(CheckableFileBean fileBean) {
                picture_select.deleteData(fileBean);
            }

            @Override
            public void onImageShowCallback(CheckableFileBean imageFileBean, int index, List<CheckableFileBean> imageFileBeans) {
                Log.e(TAG, "" + imageFileBean);
                Log.e(TAG, "" + index);
                Log.e(TAG, "" + imageFileBeans);
            }

            @Override
            public void onVideoShowCallback(CheckableFileBean videoFileBean, int index, List<CheckableFileBean> videoFileBeans) {
                Log.e(TAG, "" + videoFileBean);
                Log.e(TAG, "" + index);
                Log.e(TAG, "" + videoFileBeans);
            }

            @Override
            public void onAudioShowCallback(CheckableFileBean audioFileBean, int index, List<CheckableFileBean> audioFileBeans) {
                Log.e(TAG, "" + audioFileBean);
                Log.e(TAG, "" + index);
                Log.e(TAG, "" + audioFileBeans);
            }
        });
        List<CheckableFileBean> images = new ArrayList<>();
        images.add(new ImageNetBean("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201311%2F01%2F215828tpmddz2d2bfcz5pk.jpg&refer=http%3A%2F%2Fattach.bbs.miui.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1619197782&t=8ec66be3937ab86f0e7edab59df271ca"));
        images.add(new ImageNetBean("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201410%2F25%2F220832wlwzqq6ble9ql6rd.jpg&refer=http%3A%2F%2Fattach.bbs.miui.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1619197782&t=0631fd64899882e134049c4631e2eef8"));
        images.add(new VideoNetBean(null, "https://vd3.bdstatic.com/mda-khtuhgzs96xrn7sa/mda-khtuhgzs96xrn7sa.mp4?v_from_s=sz_videoui_4135&auth_key=1616607729-0-0-c8772210059111f2e9738b9eaa2b2138&bcevod_channel=searchbox_feed&pd=1&pt=3&abtest=3000156_3"));
        picture_select.addDatas(images);*/
    }

    private boolean notSupport(List<? extends CheckableFileBean> fileBeans) {
        boolean notSupport = false;
        ListIterator<? extends CheckableFileBean> iterator = fileBeans.listIterator();
        while (iterator.hasNext()) {
            CheckableFileBean fileBean = iterator.next();
            if (fileBean instanceof ImageFileBean) {
                if (((ImageFileBean) fileBean).mimeType.equalsIgnoreCase("image/webp")) {
                    iterator.remove();
                    notSupport = true;
                }
            }
        }
        return notSupport;
    }

    public void ok(View view) {
        List<CheckableFileBean> datas = picture_select.getDatas();
        Log.e(TAG, "size: " + datas.size());
        for (CheckableFileBean data : datas) {
            Log.e(TAG, "data: " + data);
        }
    }
}
