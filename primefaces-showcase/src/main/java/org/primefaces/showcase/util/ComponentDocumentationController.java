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
package org.primefaces.showcase.util;

import org.primefaces.cdk.api.FacesBehaviorInfo;
import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
import org.primefaces.cdk.api.PrimeFacetKeys;
import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.component.PrimeClientBehaviorHolder;
import org.primefaces.cdk.api.component.PrimeComponent;
import org.primefaces.util.LangUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import lombok.Getter;

@Named
@ApplicationScoped
@Getter
public class ComponentDocumentationController {

    private final Component badge = new Component(org.primefaces.component.badge.Badge.class);
    private final Component inputTextArea = new Component(org.primefaces.component.inputtextarea.InputTextarea.class);
    private final Component panel = new Component(org.primefaces.component.panel.Panel.class);

    public String resolveDefaultValue(PrimePropertyKeys property) {
        if (LangUtils.isNotBlank(property.getImplicitDefaultValue())) {
            return property.getImplicitDefaultValue();
        }
        return property.getDefaultValue();
    }

    public static class Component {
        private final Class<? extends PrimeComponent> clazz;
        private final PrimeComponent instance;
        private final FacesBehaviorInfo info;

        public Component(Class<? extends PrimeComponent> clazz) {
            this.clazz = clazz;
            try {
                this.instance = clazz.getConstructor().newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.info = clazz.getAnnotation(FacesBehaviorInfo.class);
        }

        public String getName() {
            return info == null ? null : info.name();
        }

        public String getDescription() {
            return info == null ? null : info.description();
        }

        public PrimePropertyKeys[] getProperties() {
            return instance.getPropertyKeys();
        }

        public PrimeFacetKeys[] getFacets() {
            return instance.getFacetKeys();
        }

        public boolean isClientBehaviorHolder()  {
            return instance instanceof PrimeClientBehaviorHolder;
        }

        public PrimeClientBehaviorEventKeys[] getClientBehaviorEvents() {
            if (instance instanceof PrimeClientBehaviorHolder) {
                return ((PrimeClientBehaviorHolder) instance).getClientBehaviorEventKeys();
            }
            return new PrimeClientBehaviorEventKeys[0];
        }
    }
}
