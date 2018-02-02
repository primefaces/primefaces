/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.push.inject;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.inject.InjectIntrospectorAdapter;
import org.atmosphere.inject.annotation.RequestScoped;
import org.atmosphere.util.ThreadLocalInvoker;
import org.primefaces.push.RemoteEndpoint;
import org.primefaces.push.impl.RemoteEndpointImpl;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * {@link RemoteEndpointImpl} injection implementation support.
 *
 * @author Jeanfrancois Arcand
 */
@RequestScoped
public class RemoteEndpointIntrospector extends InjectIntrospectorAdapter<RemoteEndpoint> {

    @Override
    public boolean supportedType(Type t) {
        return (t instanceof Class) && RemoteEndpoint.class.isAssignableFrom((Class) t);
    }

    @Override
    public RemoteEndpoint injectable(final AtmosphereResource resource) {
        ThreadLocalInvoker invoker = new ThreadLocalInvoker();
        invoker.set(resource);
  
        return (RemoteEndpoint) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[] { RemoteEndpoint.class }, 
                invoker);
    }
}
