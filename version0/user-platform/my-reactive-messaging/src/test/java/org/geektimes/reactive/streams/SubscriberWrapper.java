package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SubscriberWrapper<T> implements Subscriber<T> {
    private final Subscriber<T> subscriber;
    private final DefaultSubscription subscription;

    public SubscriberWrapper(Subscriber<T> subscriber, DefaultSubscription subscription) {
        this.subscriber = subscriber;
        this.subscription = subscription;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T t) {
        subscriber.onNext(t);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

    public DefaultSubscription getSubscription() {
        return subscription;
    }
}
