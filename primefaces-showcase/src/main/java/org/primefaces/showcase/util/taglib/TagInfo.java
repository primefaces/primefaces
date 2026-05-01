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

import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
import org.primefaces.cdk.api.PrimeFacetKeys;
import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.behavior.PrimeClientBehavior;
import org.primefaces.cdk.api.component.PrimeClientBehaviorHolder;
import org.primefaces.cdk.api.component.PrimeComponent;
import org.primefaces.cdk.api.converter.PrimeConverter;
import org.primefaces.cdk.api.utils.ReflectionUtils;
import org.primefaces.cdk.api.validator.PrimeValidator;
import org.primefaces.cdk.spi.taglib.Tag;
import org.primefaces.cdk.spi.taglib.TagType;
import org.primefaces.util.LangUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.RenderKit;
import jakarta.faces.render.Renderer;

import lombok.Getter;

@Getter
public class TagInfo {
    private Tag tag;

    private List<PrimePropertyKeys> properties;

    private String componentClass;
    private String componentFamily;
    private String rendererClass;
    private List<PrimeFacetKeys> facets;
    private List<PrimeClientBehaviorEventKeys> clientBehaviorsEvents;

    private String behaviorClass;

    private String converterClass;

    private String validatorClass;

    private List<ResourceDependency> resourceDependencies;

    public TagInfo(Tag tag) {
        try {
            this.tag = tag;

            FacesContext context = FacesContext.getCurrentInstance();

            if (tag.getType() == TagType.COMPONENT) {
                PrimeComponent instance = (PrimeComponent) context.getApplication().createComponent(tag.getComponentType());
                properties = Arrays.asList(instance.getPropertyKeys());

                componentClass = instance.getClass().getName();
                componentFamily = ((UIComponent) instance).getFamily();

                if (LangUtils.isNotBlank(tag.getRendererType())) {
                    RenderKit renderKit = context.getRenderKit();
                    Renderer<?> renderer = renderKit.getRenderer(componentFamily, tag.getRendererType());
                    rendererClass = renderer.getClass().getName();
                }

                facets = Arrays.asList(instance.getFacetKeys());
                if (instance instanceof PrimeClientBehaviorHolder) {
                    clientBehaviorsEvents = Arrays.asList(((PrimeClientBehaviorHolder) instance).getClientBehaviorEventKeys());
                }

                resourceDependencies = Arrays.stream(
                                instance.getClass().getAnnotationsByType(jakarta.faces.application.ResourceDependency.class))
                        .map(r -> new ResourceDependency(r.name(), r.library(), r.target()))
                        .collect(Collectors.toList());
            }
            else if (tag.getType() == TagType.BEHAVIOR) {
                PrimeClientBehavior instance = (PrimeClientBehavior) context.getApplication().createBehavior(tag.getBehaviorId());
                properties = Arrays.asList(instance.getPropertyKeys());
                behaviorClass = instance.getClass().getName();

                resourceDependencies = Arrays.stream(
                                instance.getClass().getAnnotationsByType(jakarta.faces.application.ResourceDependency.class))
                        .map(r -> new ResourceDependency(r.name(), r.library(), r.target()))
                        .collect(Collectors.toList());
            }
            else if (tag.getType() == TagType.CONVERTER) {
                PrimeConverter<?> instance = (PrimeConverter<?>) context.getApplication().createConverter(tag.getConverterId());
                properties = Arrays.asList(instance.getPropertyKeys());
                converterClass = instance.getClass().getName();
            }
            else if (tag.getType() == TagType.VALIDATOR) {
                PrimeValidator<?> instance = (PrimeValidator<?>) context.getApplication().createValidator(tag.getValidatorId());
                properties = Arrays.asList(instance.getPropertyKeys());
                validatorClass = instance.getClass().getName();
            }
            else if (tag.getType() == TagType.TAG_HANDLER) {
                Class<?> tagHandler = ReflectionUtils.tryToLoadClassForName(tag.getHandlerClass());
                properties = Arrays.stream(tagHandler.getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(Property.class))
                        .map(f -> PrimePropertyKeys.Literal.of(f.getName(), f.getAnnotation(Property.class)))
                        .collect(Collectors.toList());
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isComponent() {
        return tag.getType() == TagType.COMPONENT;
    }

    public boolean isBehavior() {
        return tag.getType() == TagType.BEHAVIOR;
    }

    public boolean isConverter() {
        return tag.getType() == TagType.CONVERTER;
    }

    public boolean isValidator() {
        return tag.getType() == TagType.VALIDATOR;
    }
}