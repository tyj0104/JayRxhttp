package library.learn.com.basetools.view;

/**
 * Created by jay on 2017/11/29.
 */


import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;

public abstract class RecyclerViewAdapter<E, VH extends ViewHolder> extends Adapter<VH> {
    public final LayoutInflater layoutInflater;
    private List<E> list = Lists.newArrayList();

    public RecyclerViewAdapter(Context ctx) {
        this.layoutInflater = LayoutInflater.from(ctx);
    }

    public int getItemCount() {
        return this.list.size();
    }

    public void add(int index, E e) {
        this.list.add(index, e);
        this.notifyItemInserted(index);
    }

    public void addAll(int index, Collection<E> list) {
        if(list != null && list.size() != 0) {
            this.list.addAll(list);
            this.notifyItemRangeInserted(index, list.size());
        }
    }

    public void add(E e) {
        int position = this.list.size();
        this.list.add(e);
        this.notifyItemInserted(position);
    }

    public void addAll(Collection<E> list) {
        if(list != null && list.size() != 0) {
            int positionStart = this.list.size();
            int itemCount = list.size();
            this.list.addAll(list);
            this.notifyItemRangeInserted(positionStart, itemCount);
        }
    }

    public void set(int index, E e) {
        this.list.set(index, e);
        this.notifyItemChanged(index);
    }

    public void move(int from, int to) {
        int len = this.list.size();
        if(from >= 0 && from < len && to >= 0 && to < len) {
            if(from != to) {
                Object e = this.list.remove(from);
                this.list.add(to, (E) e);
                this.notifyItemMoved(from, to);
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void remove(int index) {
        this.list.remove(index);
        this.notifyItemRemoved(index);
    }

    public void remove(E e) {
        int index = this.list.indexOf(e);
        if(index > -1) {
            this.remove(index);
        }

    }

    public int indexOf(E e) {
        return this.list.indexOf(e);
    }

    public void clear() {
        int size = this.list.size();
        this.list.clear();
        this.notifyItemRangeRemoved(0, size);
    }

    public E getItem(int index) {
        return this.list.get(index);
    }
}
