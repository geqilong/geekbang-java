package org.geektimes.reactive.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

class SubscriptionAdapter implements Subscription {

    private final DecoratingSubscriber<?> subscriber;

    public SubscriptionAdapter(Subscriber<?> subscriber) {
        this.subscriber = new DecoratingSubscriber(subscriber);
    }

    @Override
    public void request(long l) {
        if (l < 1) {
            throw new IllegalArgumentException("Wrong argument of l=[" + l + "]");
        }
        this.subscriber.setMaxRequest(l);
    }

    @Override
    public void cancel() {
        this.subscriber.cancel();
        this.subscriber.onComplete();
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public Subscriber getSourceSubscriber() {
        return subscriber.getSource();
    }
}
