package library.learn.com.basetools.view;

/**
 * Created by jay on 2017/11/29.
 */

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.ViewGroup;


public final class DataBindingRecyclerViewAdapter<E, B extends ViewDataBinding> extends RecyclerViewAdapter<E, DataBindingViewHolder<B>> {
    private final int layout;
    private final DataBindingRecyclerViewAdapter.Binder binder;

    public DataBindingRecyclerViewAdapter(Context ctx, int layout, DataBindingRecyclerViewAdapter.Binder<E, B> binder) {
        super(ctx);
        this.layout = layout;
        this.binder = binder;
    }

    @Override
    public final DataBindingViewHolder<B> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataBindingViewHolder(DataBindingUtil.inflate(this.layoutInflater, this.layout, parent, false));
    }

    @Override
    public final void onBindViewHolder(DataBindingViewHolder<B> holder, int position) {
        this.binder.onBindViewHolder(holder, this, position);
    }

    public interface Binder<E, B extends ViewDataBinding> {
        void onBindViewHolder(DataBindingViewHolder<B> var1, DataBindingRecyclerViewAdapter<E, B> var2, int var3);
    }
}
