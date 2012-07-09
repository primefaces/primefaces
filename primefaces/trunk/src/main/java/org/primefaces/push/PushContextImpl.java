package org.primefaces.push;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterListener;
import org.atmosphere.cpr.MetaBroadcaster;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

public class PushContextImpl implements PushContext {

    private final ConcurrentLinkedQueue<PushContextListener> listeners = new ConcurrentLinkedQueue<PushContextListener>();
    private final MetaBroadcaster broadcaster = MetaBroadcaster.getDefault();

    public <T> Future<T> push(final String channel, final T t) {
        String data = toJSON(t);
        final Future<?> f = broadcaster.addBroadcasterListener(new BroadcasterListener() {
            public void onComplete(Broadcaster b) {
                for (PushContextListener p: listeners) {
                    p.onComplete(channel, t);
                    broadcaster.removeBroadcasterListener(this);
                }
            }
        }).broadcastTo(channel, data);

        return new WrappedFuture(f, t);
    }

    public <T> Future<T> schedule(final String channel, final T t, int time, TimeUnit unit) {
        String data = toJSON(t);
        final Future<?> f = broadcaster.addBroadcasterListener(new BroadcasterListener() {
            public void onComplete(Broadcaster b) {
                for (PushContextListener p: listeners) {
                    p.onComplete(channel, t);
                    broadcaster.removeBroadcasterListener(this);
                }
            }
        }).scheduleTo(channel, data, time, unit);

        return new WrappedFuture(f, t);
    }

    public PushContext addListener(PushContextListener p) {
        listeners.add(p);
        return this;
    }

    public PushContext removeListener(PushContextListener p) {
        listeners.remove(p);
        return this;
    }

    private String toJSON(Object data) {
        try {
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");

            if(isBean(data)) {
                jsonBuilder.append("\"").append("data").append("\":").append(new JSONObject(data).toString());
            } 
            else {
                String json = new JSONObject().put("data", data).toString();

                jsonBuilder.append(json.substring(1, json.length() - 1));
            }

            jsonBuilder.append("}");

            return jsonBuilder.toString();
        }
        catch(JSONException e) {
            System.out.println(e.getMessage());
            
            throw new RuntimeException(e);
        }
        
    }
    
    private boolean isBean(Object value) {
        if(value == null) {
            return false;
        }

        if(value instanceof Boolean || value instanceof String || value instanceof Number) {
            return false;
        }

        return true;
    }

    private final static class WrappedFuture<T> implements Future {

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
