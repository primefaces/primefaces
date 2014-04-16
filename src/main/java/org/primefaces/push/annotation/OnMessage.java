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

import org.primefaces.push.Decoder;
import org.primefaces.push.Encoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to use with {@link PushEndpoint} annotated classes. A {@link PushEndpoint} may annotate one or more methods with {@code OnMessage}.
 * Methods will be invoked when one of many {@link Decoder}, if an {@link java.io.InputStream}, {@link java.io.Reader} or {@link String }match the method signature. For example
 *
 * <blockquote>
 *     @OnMessage
 *     public void onMessage(String s);
 *
 *     @OnMessage
 *     public void onMessage(InputStream stream);
 *
 *     @OnMessage(decoders = { MessageDecoder.class })
 *     public void onMessage(Message message);
 *
 *     @OnMessage(encoders= { PushMessageEncoder.class })
 *     public PushMessage onMessage(String message);
 *
 *     @OnMessage(encoders= { PushMessageEncoder.class }, decoders = { MessageDecoder.class })
 *     public PushMessage onMessage(Message message);
 * </blockquote>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnMessage {

    /**
     * A list of {@link Encoder} used to encode the method's return type into a response's String or bytes.
     */
    Class[] encoders() default {};

    /**
     * A list of {@link Decoder} used to decode the request's body String or bytes into an object.
     */
    Class[] decoders() default {};
}
