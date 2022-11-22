package me.luzhuo.pictureselectlibrary;

import androidx.appcompat.app.AppCompatActivity;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_permission.Permission;
import me.luzhuo.lib_permission.PermissionCallback;
import me.luzhuo.lib_picture_select.PictureSelectActivity;
import me.luzhuo.lib_picture_select.PictureSelectListener;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import static me.luzhuo.lib_file.store.FileStore.TypeAudio;
import static me.luzhuo.lib_file.store.FileStore.TypeGif;
import static me.luzhuo.lib_file.store.FileStore.TypeImage;
import static me.luzhuo.lib_file.store.FileStore.TypeVideo;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permission.request(this, new PermissionCallback() {
            @Override
            public void onGranted() {

            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        PictureSelectActivity.start(this, new PictureSelectListener() {
            @Override
            public void onPictureSelect(List<FileBean> selectFiles) {
                Log.e(TAG, "" + selectFiles);
            }
        }, TypeImage | TypeVideo | TypeAudio | TypeGif, 9, true, true);
    }
}