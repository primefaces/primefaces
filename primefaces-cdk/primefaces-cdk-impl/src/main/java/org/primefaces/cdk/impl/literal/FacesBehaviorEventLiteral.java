/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.cdk.impl.literal;

import org.primefaces.cdk.api.FacesBehaviorEvent;

import java.lang.annotation.Annotation;

import jakarta.faces.event.BehaviorEvent;

public class FacesBehaviorEventLiteral implements FacesBehaviorEvent {

    private String name;
    private Class<? extends jakarta.faces.event.BehaviorEvent> event;
    private String description;
    private boolean implicit;
    private boolean defaultEvent;

    public FacesBehaviorEventLiteral(String name, Class<? extends BehaviorEvent> event, String description,
                                     boolean implicit, boolean defaultEvent) {
        this.name = name;
        this.event = event;
        this.description = description;
        this.implicit = implicit;
        this.defaultEvent = defaultEvent;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<? extends BehaviorEvent> event() {
        return event;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public boolean implicit() {
        return implicit;
    }

    @Override
    public boolean defaultEvent() {
        return defaultEvent;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return FacesBehaviorEvent.class;
    }
}
