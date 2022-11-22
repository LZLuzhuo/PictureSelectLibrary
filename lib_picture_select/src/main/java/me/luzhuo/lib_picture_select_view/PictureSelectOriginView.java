/* Copyright 2022 Luzhuo. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.luzhuo.lib_picture_select_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import me.luzhuo.lib_core.ui.toast.ToastManager;
import me.luzhuo.lib_file.FileManager;
import me.luzhuo.lib_file.bean.AudioFileBean;
import me.luzhuo.lib_file.bean.CheckableFileBean;
import me.luzhuo.lib_file.bean.FileBean;
import me.luzhuo.lib_file.bean.ImageFileBean;
import me.luzhuo.lib_file.bean.VideoFileBean;
import me.luzhuo.lib_file.store.FileStore;
import me.luzhuo.lib_picture_select.PictureSelectActivity;
import me.luzhuo.lib_picture_select.PictureSelectListener;
import me.luzhuo.lib_picture_select.PictureSelectUtils;
import me.luzhuo.lib_picture_select.R;
import me.luzhuo.lib_picture_select_view.adapter.OnPictureAdapterSelectListener;
import me.luzhuo.lib_picture_select_view.adapter.PictureViewSelectAdapter;
import me.luzhuo.lib_picture_select_view.adapter.PictureViewShowAdapter;
import me.luzhuo.lib_picture_select_view.bean.AudioNetBean;
import me.luzhuo.lib_picture_select_view.bean.ImageNetBean;
import me.luzhuo.lib_picture_select_view.bean.VideoNetBean;
import me.luzhuo.lib_picture_select_view.callback.PictureViewSelectCallback;
import me.luzhuo.lib_picture_select_view.callback.PictureViewShowCallback;
import me.luzhuo.lib_video.picture_select_view.PictureSelectVideoPlayerActivity;

/**
 * 相册选择 (没有压缩功能)
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class PictureSelectOriginView extends RecyclerView implements OnPictureAdapterSelectListener {

    protected int spanCount = 3;
    protected int maxCount = 9;
    protected int flags = FileStore.TypeImage;
    protected boolean onlyShow = false;
    protected boolean isOriginal = false;
    protected boolean showCamera = false;
    protected int layout_select_add = R.layout.picture_select_item_add;
    protected int layout_select_normal = R.layout.picture_select_item_normal;
    protected int layout_show = R.layout.picture_select_item_show;
    protected boolean action_select = false;
    protected boolean action_delete = false;
    protected boolean action_show = false;
    protected boolean move = false;

    @Nullable
    protected PictureViewShowCallback callback;

    protected RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public PictureSelectOriginView(@NonNull Context context) {
        this(context, null);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public PictureSelectOriginView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.recyclerViewStyle);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public PictureSelectOriginView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PictureSelectView, defStyleAttr, 0);
        try {
            spanCount = typedArray.getInt(R.styleable.PictureSelectView_picture_span_count, 3);
            maxCount = typedArray.getInt(R.styleable.PictureSelectView_picture_max_count, 9);
            flags = typedArray.getInt(R.styleable.PictureSelectView_picture_flags, FileStore.TypeImage);
            onlyShow = typedArray.getBoolean(R.styleable.PictureSelectView_picture_only_show, false);
            isOriginal = typedArray.getBoolean(R.styleable.PictureSelectView_picture_is_original, false);
            showCamera = typedArray.getBoolean(R.styleable.PictureSelectView_picture_show_camera, false);
            layout_select_add = typedArray.getResourceId(R.styleable.PictureSelectView_picture_layout_select_add, R.layout.picture_select_item_add);
            layout_select_normal = typedArray.getResourceId(R.styleable.PictureSelectView_picture_layout_select_normal, R.layout.picture_select_item_normal);
            layout_show = typedArray.getResourceId(R.styleable.PictureSelectView_picture_layout_show, R.layout.picture_select_item_show);
            action_select = typedArray.getBoolean(R.styleable.PictureSelectView_picture_action_select, true);
            action_delete = typedArray.getBoolean(R.styleable.PictureSelectView_picture_action_delete, true);
            action_show = typedArray.getBoolean(R.styleable.PictureSelectView_picture_action_show, true);
            move = typedArray.getBoolean(R.styleable.PictureSelectView_picture_move, false);
        } finally {
            typedArray.recycle();
        }

        initView();
    }

    protected void initView() {
        this.setOverScrollMode(OVER_SCROLL_NEVER);
        adapter = getSelectAdapter();
        this.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        this.setAdapter(adapter);

        if (move) itemTouchHelper.attachToRecyclerView(this);
    }

    private final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, 0) {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder) {
            if (viewHolder instanceof PictureViewSelectAdapter.RecyclerAddHolder) return makeMovementFlags(0, 0);
            else return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, @NonNull ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            List<CheckableFileBean> datas = getOriginalDatas();

            if (fromPosition >= datas.size() || toPosition >= datas.size()) return false;

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(datas, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(datas, i, i - 1);
                }
            }

            adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }
        @Override
        public void onSwiped(@NonNull ViewHolder viewHolder, int direction) { }

        @Override
        public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull ViewHolder current, @NonNull ViewHolder target) {
            // return true为可替换的目标, return false为不可替换的目标
            if (current instanceof PictureViewSelectAdapter.RecyclerAddHolder || target instanceof PictureViewSelectAdapter.RecyclerAddHolder) return false;
            else return true;
        }
    });

    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getSelectAdapter() {
        if (onlyShow) {
            final PictureViewShowAdapter adapter = new PictureViewShowAdapter(layout_show);
            adapter.setOnPictureAdapterListener(this);
            return adapter;
        } else {
            final PictureViewSelectAdapter adapter = new PictureViewSelectAdapter(layout_select_add, layout_select_normal, flags, maxCount);
            adapter.setOnPictureAdapterListener(this);
            return adapter;
        }
    }

    @Override
    public void onPictureAdapterSelect() {
        PictureSelectActivity.start(getContext(), pictureSelectListener, flags, maxCount - adapter.getItemCount() + 1, showCamera, isOriginal);
    }

    protected PictureSelectListener pictureSelectListener = new PictureSelectListener() {
        @Override
        public void onPictureSelect(List<FileBean> selectFiles) {
            if (action_select) {
                addDatas(selectFiles);
            }

            if (callback != null) {
                if (callback instanceof PictureViewSelectCallback) {
                    ((PictureViewSelectCallback) callback).onSelect(selectFiles);
                }
            }
        }
    };

    @Override
    public void onPictureAdapterDelete(int position) {
        CheckableFileBean bean = ((PictureViewSelectAdapter) adapter).getDatas().get(position);

        if (action_delete) {
            if (adapter instanceof PictureViewSelectAdapter) {
                ((PictureViewSelectAdapter) adapter).removeData(bean);
            }
        }

        if (callback != null) {
            if (callback instanceof PictureViewSelectCallback) {
                ((PictureViewSelectCallback) callback).onDelete(bean);
            }
        }
    }

    @Override
    public void onPictureAdapterShow(int position) {
        CheckableFileBean bean;
        if (adapter instanceof PictureViewSelectAdapter) bean = ((PictureViewSelectAdapter) adapter).getDatas().get(position);
        else if (adapter instanceof PictureViewShowAdapter) bean = ((PictureViewShowAdapter) adapter).getDatas().get(position);
        else throw new IllegalArgumentException("Picture展示时, 数据类型不匹配");

        int type;
        if (bean instanceof ImageFileBean || bean instanceof ImageNetBean) type = FileStore.TypeImage;
        else if (bean instanceof VideoFileBean || bean instanceof VideoNetBean) type = FileStore.TypeVideo;
        else if (bean instanceof AudioFileBean || bean instanceof AudioNetBean) type = FileStore.TypeAudio;
        else throw new IllegalArgumentException("Picture展示时, 数据类型不匹配");

        List<CheckableFileBean> newFilterShowList = new ArrayList<>();
        for (CheckableFileBean data : ((PictureViewSelectAdapter) adapter).getDatas()) {
            if (type == FileStore.TypeImage) {
                if (data instanceof ImageFileBean || data instanceof ImageNetBean) newFilterShowList.add(data);
            } else if (type == FileStore.TypeVideo) {
                if (data instanceof VideoFileBean || data instanceof VideoNetBean) newFilterShowList.add(data);
            } else if (type == FileStore.TypeAudio) {
                if (data instanceof AudioFileBean || data instanceof AudioNetBean) newFilterShowList.add(data);
            } else {
                throw new IllegalArgumentException("Picture展示时, 数据类型不匹配");
            }
        }

        int index;
        try {
            index = newFilterShowList.indexOf(bean);
        } catch (Exception e) {
            throw new IllegalArgumentException("Picture展示时, 数据类型不匹配");
        }

        if (action_show) {
            if (type == FileStore.TypeImage) {
                PictureSelectUtils.imagePreview(getContext(), index, PictureSelectUtils.getOriginStringList(newFilterShowList));
            } else if (type == FileStore.TypeVideo) {
                if (bean instanceof VideoNetBean) {
                    PictureSelectVideoPlayerActivity.start(getContext(), ((VideoNetBean)bean).coverUrl, ((VideoNetBean)bean).netUrl);
                } else {
                    if (new FileManager().needUri()) PictureSelectVideoPlayerActivity.start(getContext(), ((VideoFileBean)bean).uriPath);
                    else PictureSelectVideoPlayerActivity.start(getContext(), new File(((VideoFileBean)bean).urlPath));
                }
            } else if (type == FileStore.TypeAudio) {
                // TODO 不支持
                ToastManager.show2(getContext(), "暂不支持音频播放");
            } else throw new IllegalArgumentException("Picture展示时, 数据类型不匹配");
        }

        if (callback != null && !action_show) {
            if (type == FileStore.TypeImage) callback.onImageShowCallback(bean, index, newFilterShowList);
            else if (type == FileStore.TypeVideo) callback.onVideoShowCallback(bean, index, newFilterShowList);
            else if (type == FileStore.TypeAudio) callback.onAudioShowCallback(bean, index, newFilterShowList);
            else throw new IllegalArgumentException("Picture展示时, 数据类型不匹配");
        }
    }

    public void setPictureListener(PictureViewSelectCallback callback) {
        this.callback = callback;
    }

    public void setPictureListener(PictureViewShowCallback callback) {
        this.callback = callback;
    }

    public void setDatas(List<? extends CheckableFileBean> files) {
        if (adapter instanceof PictureViewSelectAdapter) {
            ((PictureViewSelectAdapter) adapter).setDatas(files);
        } else if (adapter instanceof PictureViewShowAdapter) {
            ((PictureViewShowAdapter) adapter).setData(files);
        }
    }

    public void addDatas(List<? extends CheckableFileBean> files) {
        if (adapter instanceof PictureViewSelectAdapter) {
            ((PictureViewSelectAdapter) adapter).addDatas(files);
        } else if (adapter instanceof PictureViewShowAdapter) {
            ((PictureViewShowAdapter) adapter).addDatas(files);
        }
    }

    public void deleteData(CheckableFileBean file) {
        if (adapter instanceof PictureViewSelectAdapter) {
            ((PictureViewSelectAdapter) adapter).removeData(file);
        } else if (adapter instanceof PictureViewShowAdapter) {
            ((PictureViewShowAdapter) adapter).removeData(file);
        }
    }

    /**
     * 获取原始的数据集合, 该数据未做任何的处理
     * 包括原始文件, 网络文件
     */
    public List<CheckableFileBean> getOriginalDatas() {
        if (adapter instanceof PictureViewSelectAdapter) {
            return ((PictureViewSelectAdapter) adapter).getDatas();
        } else if (adapter instanceof PictureViewShowAdapter) {
            return ((PictureViewShowAdapter) adapter).getDatas();
        } else throw new IllegalArgumentException("Picture获取数据时, 数据类型不匹配");
    }
}
