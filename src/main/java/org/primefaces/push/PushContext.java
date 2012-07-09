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

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public interface PushContext {

    <T> Future<T> push(String channel, T t);

    <T> Future<T> schedule(String channel, T t, int time, TimeUnit unit);

    <T> Future<T> delay(String channel, T t, int time, TimeUnit unit);

    PushContext addListener(PushContextListener p);

    PushContext removeListener(PushContextListener p);

}
