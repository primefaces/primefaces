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

/**
 * Factory for retrieving {@link PushContext}
 */
public class PushContextFactory {

    private static final PushContextFactory p = new PushContextFactory();
    private final PushContext pushContext;

    private PushContextFactory() {
        pushContext = new PushContextImpl();
    }

    /**
     * Return the default factory
     * @return the default factory
     */
    public final static PushContextFactory getDefault() {
        return p;
    }

    /**
     * Retrieve a {@link PushContext}
     * @return
     */
    public PushContext getPushContext(){
        return pushContext;
    }

}
