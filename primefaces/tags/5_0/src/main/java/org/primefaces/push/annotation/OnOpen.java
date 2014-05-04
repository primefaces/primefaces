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
package org.primefaces.push.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * An annotation to use with {@link PushEndpoint} annotated classes. A method annotated with @OnOpen will be invoked
 * when a {@link org.primefaces.push.RemoteEndpoint} is connected and ready to receive push messages. An annotated method may define zero, one or two parameters. For example
 *
 * <blockquote>
 *     @OnOpen
 *     public void onOpen();
 *
 *     @OnOpen
 *     public void onOpen(RemoteEndpoint r);
 *
 *     @OnOpen
 *     public void onOpen(RemoteEndpoint r, EventBus e);
 * </blockquote>
 *
 * Only one method per {@link PushEndpoint} can be annotated with this annotation. The {@link org.primefaces.push.EventBus} can always be
 * passed and used for firing messages between {@link org.primefaces.push.annotation.PushEndpoint}.
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnOpen {
}
