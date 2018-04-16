package library.learn.com.basetools.noml;

import com.google.common.collect.Maps;


import org.apache.commons.codec.binary.StringUtils;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static io.reactivex.internal.functions.ObjectHelper.requireNonNull;

/**
 * RxBus类，用于模块间通讯和解耦
 * Created by jay on 2016/12/5.
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RxBus {
    @AllArgsConstructor
    @Getter
    private class Event {
        private final String action;
        private final Object data;

        public final <T> T getData() {
            return (T) data;
        }

        public final boolean filter(String action) {
            return StringUtils.equals(this.action, action);
        }
    }

    @Getter
    private static final RxBus instance = new RxBus();
    private final Subject<Event> mSubject = PublishSubject.<Event>create().toSerialized();
    private final Map<String, Object> mStickyEventMap = Maps.newConcurrentMap();

    public void post(String action, Object data) {
        requireNonNull(action, "action is null");
        if (mSubject.hasObservers()) {
            mSubject.onNext(new Event(action, data));
        }
    }

    public void postSticky(String action, Object data) {
        requireNonNull(action, "action is null");
        mStickyEventMap.put(action, data);
        if (mSubject.hasObservers()) {
            mSubject.onNext(new Event(action, data));
        }
    }

    public <T> Observable<T> asObservable(String action) {
        return mSubject.filter(e -> e.filter(action)).map(Event::getData);
    }

    public <T> Observable<T> asObservableSticky(String action) {
        return asObservable(action, true);
    }

    public <T> Observable<T> asObservable(String action, boolean sticky) {
        Observable<T> observable = asObservable(action);
        if (sticky) {
            T stickyEvent = (T) mStickyEventMap.get(action);
            if (stickyEvent != null) {
                observable = observable.startWith(stickyEvent);
            }
        }
        return observable;
    }

    public boolean hasObservers() {
        return mSubject.hasObservers();
    }

    public void removeStickyEvent(String action) {
        mStickyEventMap.remove(action);
    }

    public void removeAllStickyEvents() {
        mStickyEventMap.clear();
    }
}
