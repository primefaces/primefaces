/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.effect;

import org.primefaces.model.JSObjectBuilder;

public class EffectBuilder implements JSObjectBuilder {

    private StringBuffer buffer;

    private boolean hasOption = false;

    public EffectBuilder(String type, String id, boolean queue) {
        buffer = new StringBuffer();
        buffer.append("$(PrimeFaces.escapeClientId('");
        buffer.append(id);
        buffer.append("'))");
        if (!queue) {
            buffer.append(".stop(true,true)");
        }
        buffer.append(".effect('");
        buffer.append(type);
        buffer.append("',{");
    }

    public EffectBuilder withOption(String name, String value) {
        if (hasOption) {
            buffer.append(",");
        }
        else {
            hasOption = true;
        }

        buffer.append(name);
        buffer.append(":");
        buffer.append(value);

        return this;
    }

    public EffectBuilder atSpeed(int speed) {
        buffer.append("},");
        buffer.append(speed);

        return this;
    }

    @Override
    public String build() {
        buffer.append(");");

        return buffer.toString();
    }
}
