package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DefaultSubscriber<T> implements Subscriber<T> {
    private Subscription subscription;
    private int count = 0;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public void onNext(T t) {
        System.out.println("收到数据:" + t);
        if (++count > 2) { //当到达数据阈值时，取消Publisher给当前Subscriber发送数据
            subscription.cancel();
            return;
        }
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("异常发生: " + throwable);
    }

    @Override
    public void onComplete() {
        System.out.println("收到数据完成");
    }
}
