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
package org.primefaces.component.export;

import org.primefaces.context.PrimeApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;

public final class DataExporters {

    private static final Logger LOGGER = Logger.getLogger(DataExporters.class.getName());

    private DataExporters() {
        // NOOP
    }

    public static <T extends UIComponent> Exporter<T> get(Class<T> targetClass, String type) {
        PrimeApplicationContext primeAppContext = PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance());

        // since users might defined their own impl of components (e.g. MyDataTable, MyTreeTable etc.)
        // retrieve the most eligible built-in component class first (e.g. DataTable, TreeTable) to get corresponding exporters
        Class<? extends UIComponent> resolvedTargetClass = primeAppContext.getExporters().keySet().stream()
                .filter(k -> k.isAssignableFrom(targetClass))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(
                        "Component " + targetClass.getName() + " not supported. Use DataExporters#register()"));

        Map<String, Class<? extends Exporter<?>>> supportedExporters = primeAppContext.getExporters().get(resolvedTargetClass);

        String newType = type.toLowerCase();
        Class<? extends Exporter<?>> exportClass = Optional.ofNullable(supportedExporters.get(newType))
                .orElseThrow(() -> new UnsupportedOperationException(
                        "Exporter for " + targetClass.getName() + " of type '" + newType + "' is not supported. Use DataExporters#register()"));

        try {
            return (Exporter<T>) exportClass.getConstructor().newInstance();
        }
        catch (ReflectiveOperationException e) {
            throw new FacesException(e);
        }
    }

    /**
     * Register a new exporter. In case an exporter is already provided for the corresponding component
     * and file type: this new exporter will replace the old one
     * @param targetClass component type on which exporter should be available
     * @param exporter custom exporter implementing {@link Exporter}
     * @param type file type
     */
    public static <T extends UIComponent, E extends Exporter<T>> void register(Class<T> targetClass, Class<E> exporter, String type) {
        PrimeApplicationContext primeAppContext = PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance());
        Map<String, Class<? extends Exporter<?>>> supportedExporters = primeAppContext.getExporters().computeIfAbsent(targetClass, o -> new HashMap<>());

        String newType = type.toLowerCase();
        Class<? extends Exporter<?>> old = supportedExporters.put(newType, exporter);
        if (old != null) {
            LOGGER.log(Level.INFO, "Exporter {0} of type {1} has been replaced by {2}", new Object[]{old.getName(), newType, exporter.getName()});
        }
        else {
            LOGGER.log(Level.INFO, "New exporter {0} of type {1} has been registered", new Object[]{exporter.getName(), newType});
        }
    }
}
