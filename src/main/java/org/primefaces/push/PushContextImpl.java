package org.primefaces.push;

import org.atmosphere.cpr.MetaBroadcaster;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PushContextImpl implements PushContext {

    @Override
    public <T> Future<T> push(String channel, final T t) {

        final Future<?> f = MetaBroadcaster.getDefault().broadcastTo(channel, t);

        return new Future<T>() {

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
        };
    }
}
