package org.geektimes.reactive.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.LinkedList;
import java.util.List;

public class SimplePublisher<T> implements Publisher<T> {
    private List<Subscriber> subscribers = new LinkedList<>();

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        SubscriptionAdapter subscription = new SubscriptionAdapter(subscriber);
        subscriber.onSubscribe(subscription);
        subscribers.add(subscription.getSubscriber());
    }

    /**
     * 给每个subscriber推送消息
     *
     * @param data
     */
    public void publish(T data) {
        subscribers.forEach(subscriber -> {
            subscriber.onNext(data);
        });
    }

    public static void main(String[] args) {
        SimplePublisher publisher = new SimplePublisher();

        publisher.subscribe(new BusinessSubscriber(15));
        for (int i = 0; i < 25; i++) {
            publisher.publish(i);
        }
    }

}
