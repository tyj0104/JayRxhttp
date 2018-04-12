package library.learn.com.basetools.view;

/**
 * Created by jay on 2017/11/29.
 */

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView.ViewHolder;

public final class DataBindingViewHolder<T extends ViewDataBinding> extends ViewHolder {
    public final T binding;

    public DataBindingViewHolder(T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
