package com.toxicrain.rainengine.core.eventbus;

import com.toxicrain.rainengine.core.RainLogger;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * RainBus is the EventBus for RainEngine. You can post and listen to events on it.
 */
public class RainBus {
    private final PublishSubject<Object> bus = PublishSubject.create();

    public void post(Object event) {
        bus.onNext(event);
    }

    public <T> Observable<T> listen(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}

