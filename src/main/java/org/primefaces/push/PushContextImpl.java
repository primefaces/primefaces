/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.push;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.atmosphere.cpr.AsyncSupportListenerAdapter;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterListener;
import org.atmosphere.cpr.MetaBroadcaster;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

public class PushContextImpl extends AsyncSupportListenerAdapter implements PushContext {

    private final ConcurrentLinkedQueue<PushContextListener> listeners = new ConcurrentLinkedQueue<PushContextListener>();
    private final MetaBroadcaster broadcaster = MetaBroadcaster.getDefault();

    public <T> Future<T> push(final String channel, final T t) {
        String data = toJSON(t);
        final Future<?> f = broadcaster.addBroadcasterListener(new BroadcasterListener() {
            public void onPostCreate(Broadcaster broadcaster) {
            }

            public void onComplete(Broadcaster b) {
                for (PushContextListener p: listeners) {
                    p.onComplete(channel, t);
                    b.removeBroadcasterListener(this);
                }
            }

            public void onPreDestroy(Broadcaster broadcaster) {
            }
        }).broadcastTo(channel, data);

        return new WrappedFuture(f, t);
    }

    public <T> Future<T> schedule(final String channel, final T t, int time, TimeUnit unit) {
        Object data = t;        
        if(!(t instanceof Callable || t instanceof Runnable)) {
            data = toJSON(t);
        }

        final AtomicBoolean completed = new AtomicBoolean();
        final Future<List<Broadcaster>> f = broadcaster.addBroadcasterListener(new BroadcasterListener() {
            public void onPostCreate(Broadcaster broadcaster) {
            }

            public void onComplete(Broadcaster b) {
                completed.set(true);
                for (PushContextListener p: listeners) {
                    p.onComplete(channel, t);
                    b.removeBroadcasterListener(this);
                }
            }

            public void onPreDestroy(Broadcaster broadcaster) {
            }
        }).scheduleTo(channel, data, time, unit);

        return new WrappedFuture(f, t);
    }

    public <T> Future<T> delay(final String channel, final T t, int time, TimeUnit unit) {
        String data = toJSON(t);
        final Future<?> f = broadcaster.addBroadcasterListener(new BroadcasterListener() {
            public void onPostCreate(Broadcaster broadcaster) {
            }

            public void onComplete(Broadcaster b) {
                for (PushContextListener p: listeners) {
                    p.onComplete(channel, t);
                    broadcaster.removeBroadcasterListener(this);
                }
            }

            public void onPreDestroy(Broadcaster broadcaster) {
            }
        }).delayTo(channel, data, time, unit);

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

    @Override
    public void onTimeout(AtmosphereRequest request, AtmosphereResponse response) {
        for (PushContextListener l : listeners) {
            l.onDisconnect(request);
        }
    }

    @Override
    public void onClose(AtmosphereRequest request, AtmosphereResponse response) {
        for (PushContextListener l : listeners) {
            l.onDisconnect(request);
        }
    }

    @Override
    public void onSuspend(AtmosphereRequest request, AtmosphereResponse response) {

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
