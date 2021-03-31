package org.geektimes.reactive.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.LinkedList;
import java.util.List;

public class DefaultPublisher<T> implements Publisher<T> {
    private List<SubscriberWrapper> subscribers = new LinkedList<>();

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        DefaultSubscription subscription = new DefaultSubscription(subscriber);
        //当订阅者订阅时
        subscriber.onSubscribe(subscription);
        subscribers.add(new SubscriberWrapper(subscriber, subscription));
    }

    public void publish(T data) {
        //广播
        subscribers.forEach(subscriberWrapper -> {
            DefaultSubscription subscription = subscriberWrapper.getSubscription();
            if (subscription.isCanceled()) {
                System.err.println("本次数据发布已忽略，数据为：" + data);
                return;
            }
            subscriberWrapper.onNext(data);
        });
    }

    public static void main(String[] args) {
        DefaultPublisher publisher = new DefaultPublisher();
        publisher.subscribe(new DefaultSubscriber());
        for (int i = 0; i < 5; i++) {
            publisher.publish(i);
        }
    }

}
