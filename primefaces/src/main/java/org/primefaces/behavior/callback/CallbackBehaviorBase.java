/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.behavior.callback;

import org.primefaces.cdk.api.FacesBehaviorBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.behavior.PrimeClientBehavior;

@FacesBehaviorBase
public abstract class CallbackBehaviorBase extends PrimeClientBehavior {

    @Override
    @Property(description = "Event of the component that executes the JavaScript, the default event of the component if not set.")
    public abstract String getEvent();

    @Property(description = "JavaScript to execute, the body of a function with the arguments source (client id of the component),"
            + " event (name of the event), params (parameters of the event) and ext. Returning false stops the following behaviors of the event.")
    public abstract String getExecute();

    @Property(description = "Disables the callback.", defaultValue = "false")
    public abstract boolean isDisabled();
}
