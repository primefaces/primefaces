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
package org.primefaces.showcase.util.taglib;

import org.primefaces.cdk.api.utils.ReflectionUtils;
import org.primefaces.cdk.spi.taglib.Function;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class FunctionInfo {

    private Function function;
    private String signature;

    public FunctionInfo(Function function) {
        this.function = function;

        try {
            Class<?> clazz = ReflectionUtils.loadClassForName(function.getFunctionClass());

            Method method = Arrays.stream(clazz.getMethods()).filter(m -> {
                org.primefaces.cdk.api.Function func = m.getAnnotation(org.primefaces.cdk.api.Function.class);
                if (func == null) return false;
                String annotationName = func.name().isEmpty() ? m.getName() : func.name();
                return annotationName.equals(function.getName());
            })
                    .findFirst()
                    .orElse(null);

            if (method == null) {
                Class<?>[] paramTypes = extractParamTypes(function.getFunctionSignature());
                method = clazz.getMethod(function.getName(), paramTypes);
            }

            signature = formatMethodSignature(function.getName(), method);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?>[] extractParamTypes(String signature) throws ClassNotFoundException {
        int start = signature.indexOf('(');
        int end = signature.indexOf(')');

        String inside = signature.substring(start + 1, end).trim();

        if (inside.isEmpty()) return new Class<?>[0];

        String[] typeNames = inside.split("\\s*,\\s*");
        Class<?>[] types = new Class<?>[typeNames.length];

        for (int i = 0; i < typeNames.length; i++) {
            types[i] = resolveType(typeNames[i].trim());
        }
        return types;
    }

    private Class<?> resolveType(String typeName) throws ClassNotFoundException {
        switch (typeName) {
            case "int":     return int.class;
            case "long":    return long.class;
            case "double":  return double.class;
            case "float":   return float.class;
            case "boolean": return boolean.class;
            case "byte":    return byte.class;
            case "short":   return short.class;
            case "char":    return char.class;
            case "void":    return void.class;
            default:
                try {
                    return ReflectionUtils.loadClassForName(typeName);
                }
                catch (ClassNotFoundException e) {
                    return ReflectionUtils.loadClassForName("java.lang." + typeName);
                }
        }
    }

    public String formatMethodSignature(String name, Method method) {
        String returnType = method.getReturnType().getSimpleName();

        String params = Arrays.stream(method.getParameters())
                .map(p -> p.getType().getSimpleName() + " " + p.getName())
                .collect(Collectors.joining(", "));

        return returnType + " " + name + "(" + params + ")";
    }
}
