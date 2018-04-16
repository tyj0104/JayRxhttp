package library.learn.com.basetools.http;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import lombok.AllArgsConstructor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * 自动重试的retrofit2-rxjava2-adapter
 * Created by jay on 2016/12/15.
 */

@AllArgsConstructor
public class RxJava2RetryCallAdapterFactory extends CallAdapter.Factory {
    public static CallAdapter.Factory create(int times, Scheduler scheduler) {
        return new RxJava2RetryCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(scheduler), times);
    }

    public static CallAdapter.Factory create(int times) {
        return new RxJava2RetryCallAdapterFactory(RxJava2CallAdapterFactory.create(), times);
    }

    private final CallAdapter.Factory factory;
    private final int times;

    @Override
    public CallAdapter<?> get(Type type, Annotation[] annotations, Retrofit retrofit) {
        final CallAdapter<?> adapter = factory.get(type, annotations, retrofit);
        return new CallAdapter<Object>() {
            @Override
            public Type responseType() {
                return adapter.responseType();
            }

            @Override
            public <R> Object adapt(Call<R> call) {
                Object result = adapter.adapt(call);
                Class rawType = getRawType(type);
                if (rawType == Completable.class) {
                    result = Completable.class.cast(result).retry(times);
                } else if (rawType == Flowable.class) {
                    result = Flowable.class.cast(result).retry(times);
                } else if (rawType == Single.class) {
                    result = Single.class.cast(result).retry(times);
                } else if (rawType == Maybe.class) {
                    result = Maybe.class.cast(result).retry(times);
                } else if (rawType == Observable.class) {
                    result = Observable.class.cast(result).retry(times);
                }
                return result;
            }
        };
    }
}
