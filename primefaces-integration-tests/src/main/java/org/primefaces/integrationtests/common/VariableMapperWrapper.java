/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.integrationtests.common;

import java.util.HashMap;
import java.util.Map;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * Utility class for wrapping a VariableMapper. Modifications occur to the internal Map instance.
 * The resolving occurs first against the internal Map instance and then against the wrapped VariableMapper
 * if the Map doesn't contain the requested ValueExpression.
 */
public class VariableMapperWrapper extends VariableMapper {

    private final VariableMapper wrapped;

    private Map<String, ValueExpression> vars;

    public VariableMapperWrapper(VariableMapper orig) {
        super();
        this.wrapped = orig;
    }

    @Override
    public ValueExpression resolveVariable(String variable) {
        ValueExpression ve = null;
        try {
            if (this.vars != null) {
                // try to resolve against the internal map
                ve = this.vars.get(variable);
            }

            if (ve == null) {
                // look in the wrapped variable mapper
                return this.wrapped.resolveVariable(variable);
            }

            return ve;
        }
        catch (Throwable e) {
            throw new ELException("Could not resolve variable: " + variable, e);
        }
    }

    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        if (this.vars == null) {
            this.vars = new HashMap<>();
        }

        return this.vars.put(variable, expression);
    }
}
