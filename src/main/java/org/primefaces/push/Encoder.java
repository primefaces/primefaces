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
package org.primefaces.push;

/**
 * Encode a message returned by a method annotated with {@link org.primefaces.push.annotation.OnMessage}
 * annotated class. The encoded object will be written back to the client. For example
 *
 * <blockquote><pre>

     public final static class StringBufferEncoder implements Encoder&gt;StringBuffer, String&lt;  {

         @Override
         public String encode(StringBuffer s) {
             return s.toString() + "-yo!";
         }
     }
 * </pre></blockquote>
 * will encode a StringBuffer into a String. The StringBuffer will be the object returned when a method annotated with @Message is invoked
 * <blockquote><pre>

     @Message(encoders = {StringBufferEncoder.class})
     public StringBuffer encode(String m) {
         return new StringBuffer(m);
     }
 * </pre></blockquote>
 * You can chain Encoders by defining more than one. They will be invoked in the order they have been added and the last Encoder's value
 * will be used for the write operation.
 * @param <U>
 * @param <T>
 */
public interface Encoder<U, T> extends org.atmosphere.config.managed.Encoder<U, T>{
   // Atmosphere Proxy
    /**
     * Encode the object of type U into an object of type T.
     * @param s an object that has already been encoded or returned from an @Message annotated class.
     * @return an encoded object.
     */
    @Override
    T encode(U s);
}
