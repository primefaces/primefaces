/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.push.impl;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterListenerAdapter;
import org.atmosphere.cpr.MetaBroadcaster;
import org.primefaces.push.EventBus;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EventBusImpl implements EventBus {
    
    private final MetaBroadcaster metaBroadcaster; 
    
    public EventBusImpl(MetaBroadcaster metaBroadcaster) {
        this.metaBroadcaster = metaBroadcaster;
    }

    //@Override
    public EventBus publish(Object o) {
        metaBroadcaster.broadcastTo("/*", o);
        return this;
    }

    //@Override
    public EventBus publish(String path, Object o) {
        if (!path.startsWith("/")) path = "/" + path;

        metaBroadcaster.broadcastTo(path, o);
        return this;
    }

    //@Override
    public EventBus publish(String path, Object o, final Reply r) {
        metaBroadcaster.addBroadcasterListener(new BroadcasterListenerAdapter() {
            public void onComplete(Broadcaster b) {
                try {
                    r.completed(b.getID());
                } finally {
                    metaBroadcaster.removeBroadcasterListener(this);
                }
            }
        });
        metaBroadcaster.broadcastTo(path, o);
        return this;
    }

    public <T> Future<T> schedule(final String path, final T t, int time, TimeUnit unit) {
        final Future<List<Broadcaster>> f = metaBroadcaster.scheduleTo(path, t, time, unit);
        return new WrappedFuture<T>(f, t);
    }

    private final static class WrappedFuture<T> implements Future<T> {

        private final Future<?> f;
        private final T t;

        private WrappedFuture(Future<?> f, T t) {
            this.f = f;
            this.t = t;
        }

        public boolean cancel(boolean b) {
            return f.cancel(b);
        }

        public boolean isCancelled() {
            return f.isCancelled();
        }

        public boolean isDone() {
            return f.isDone();
        }

        public T get() throws InterruptedException, ExecutionException {
            f.get();
            return t;
        }

        public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            f.get(l, timeUnit);
            return t;
        }
    }

}
