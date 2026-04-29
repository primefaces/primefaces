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

import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
import org.primefaces.cdk.api.PrimeFacetKeys;
import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.behavior.PrimeClientBehavior;
import org.primefaces.cdk.api.component.PrimeClientBehaviorHolder;
import org.primefaces.cdk.api.component.PrimeComponent;
import org.primefaces.cdk.api.converter.PrimeConverter;
import org.primefaces.cdk.api.validator.PrimeValidator;
import org.primefaces.cdk.spi.taglib.Tag;
import org.primefaces.cdk.spi.taglib.TagType;
import org.primefaces.cdk.spi.taglib.Taglib;
import org.primefaces.cdk.spi.taglib.TaglibParser;
import org.primefaces.util.LangUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import lombok.Getter;

@Named("cdkController")
@ApplicationScoped
@Getter
public class CDKController {

    private Taglib taglib;
    private Map<String, TagInfo> tags;

    @PostConstruct
    public void init() {
        TaglibParser taglibParser = new TaglibParser();
        try {
            taglib = taglibParser.parse(LangUtils.getContextClassLoader().getResourceAsStream(
                    "/META-INF/primefaces.taglib.xml"));
            tags = taglib.getTags().stream().collect(
                    Collectors.toMap(t -> t.getTagName(), t -> new TagInfo(t)));
        }
        catch (Exception e) {
            throw new FacesException("Could not parse taglib!", e);
        }
    }

    public String resolveDefaultValue(PrimePropertyKeys property) {
        if (LangUtils.isNotBlank(property.getImplicitDefaultValue())) {
            return property.getImplicitDefaultValue();
        }
        return property.getDefaultValue();
    }

    @Getter
    public static class TagInfo {
        private Tag tag;
        private List<PrimePropertyKeys> properties;
        private List<PrimeFacetKeys> facets;
        private List<PrimeClientBehaviorEventKeys> clientBehaviorsEvents;

        public TagInfo(Tag tag) {
            try {
                this.tag = tag;

                FacesContext context = FacesContext.getCurrentInstance();

                if (tag.getType() == TagType.COMPONENT) {
                    PrimeComponent instance = (PrimeComponent) context.getApplication().createComponent(tag.getComponentType());
                    properties = Arrays.asList(instance.getPropertyKeys());
                    facets = Arrays.asList(instance.getFacetKeys());
                    if (instance instanceof PrimeClientBehaviorHolder) {
                        clientBehaviorsEvents = Arrays.asList(((PrimeClientBehaviorHolder) instance).getClientBehaviorEventKeys());
                    }
                }
                else if (tag.getType() == TagType.BEHAVIOR) {
                    PrimeClientBehavior instance = (PrimeClientBehavior) context.getApplication().createBehavior(tag.getBehaviorId());
                    properties = Arrays.asList(instance.getPropertyKeys());
                }
                else if (tag.getType() == TagType.CONVERTER) {
                    PrimeConverter<?> instance = (PrimeConverter<?>) context.getApplication().createConverter(tag.getConverterId());
                    properties = Arrays.asList(instance.getPropertyKeys());
                }
                else if (tag.getType() == TagType.VALIDATOR) {
                    PrimeValidator<?> instance = (PrimeValidator<?>) context.getApplication().createValidator(tag.getValidatorId());
                    properties = Arrays.asList(instance.getPropertyKeys());
                }
                else if (tag.getType() == TagType.TAG_HANDLER) {
                    // TODO
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
