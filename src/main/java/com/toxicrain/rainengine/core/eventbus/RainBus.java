package com.toxicrain.rainengine.core.eventbus;

import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.subjects.PublishSubject;


public class RainBus {
    private final PublishSubject<Object> bus = PublishSubject.create();

    public void post(Object event) {
        bus.onNext(event);
    }

    public <T> Observable<T> listen(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}

