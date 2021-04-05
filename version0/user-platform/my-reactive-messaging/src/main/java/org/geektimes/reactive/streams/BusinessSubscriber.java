package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 业务数据订阅者
 *
 * @param <T>
 */
public class BusinessSubscriber<T> implements Subscriber<T> {
    private Logger logger = Logger.getLogger(getClass().getName());

    private Subscription subscription;
    private int count = 0;
    private final long maxRequest;

    public BusinessSubscriber(long maxRequest) {
        this.maxRequest = maxRequest;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(maxRequest);
    }

    @Override
    public void onNext(T t) {
        logger.log(Level.INFO, "收到数据: " + t);
        if (count++ > maxRequest) { //当到达数据阈值时，取消Publisher给当前Subscriber发送数据
            subscription.cancel();
            return;
        }
    }

    @Override
    public void onError(Throwable throwable) {
        logger.log(Level.SEVERE, "遇到异常:" + throwable);
    }

    @Override
    public void onComplete() {
        logger.info("收到数据完成");
    }
}
