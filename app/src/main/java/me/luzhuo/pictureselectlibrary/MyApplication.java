package me.luzhuo.pictureselectlibrary;

import androidx.multidex.MultiDex;
import me.luzhuo.lib_core.app.base.CoreBaseApplication;

public class MyApplication extends CoreBaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
