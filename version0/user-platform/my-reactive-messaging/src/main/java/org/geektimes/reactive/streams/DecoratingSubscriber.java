package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * (Internal) Decorating {@link Subscriber}
 *
 * @param <T>
 */
public class DecoratingSubscriber<T> implements Subscriber<T> {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final Subscriber<T> source;
    private long maxRequest = -1;
    private boolean canceled = false;
    private boolean completed = false;
    private long requestCount = 0;

    public DecoratingSubscriber(Subscriber<T> subscriber) {
        this.source = subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        source.onSubscribe(subscription);
    }

    @Override
    public void onNext(T t) {
        if (maxRequest < 1) {
            throw new IllegalArgumentException("the number of request must be initialized before " +
                    "Subscriber#onNext(Object) method, please set the positive number to " +
                    "Subscription#request(int) method on Publisher#subscribe(Subscriber) phase.");
        }

        if (isCompleted()) {
            logger.log(Level.SEVERE, "The data subscription was completed, this method should not be invoked again");
            return;
        }
        if (isCanceled()) {
            logger.log(Level.WARNING, String.format("The subscriber has canceled the data subscription, " +
                    "current data[%s] will be ignored!", t));
        }
        if (requestCount == maxRequest && maxRequest < Long.MAX_VALUE) {
            onComplete();
            logger.log(Level.WARNING, String.format("The number of requests is up to the max threshold[%d], " +
                    " the data subscription is completed!", maxRequest));
            return;
        }
        next(t);
    }

    private void next(T t) {
        try {
            source.onNext(t);
        } catch (Exception e) {
            onError(e);
        } finally {
            requestCount++;
        }
    }

    @Override
    public void onError(Throwable throwable) {
        source.onError(throwable);
    }

    @Override
    public void onComplete() {
        source.onComplete();
        completed = true;
    }

    public void setMaxRequest(long l) {
        this.maxRequest = l;
    }

    public void cancel() {
        this.canceled = true;
    }

    public Subscriber getSource() {
        return source;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isCompleted() {
        return completed;
    }
}
