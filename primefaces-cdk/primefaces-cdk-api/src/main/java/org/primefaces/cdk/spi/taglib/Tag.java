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
package org.primefaces.cdk.spi.taglib;

import java.util.List;

public class Tag {

    private final TagType type;
    private final String tagName;
    private final String description;
    private final String componentType;
    private final String handlerClass;
    private final String rendererType;
    private final String behaviorId;
    private final String validatorId;
    private final String converterId;
    private final List<TagAttribute> attributes;

    public Tag(TagType type, String tagName, String description,
               String handlerClass,
               String componentType, String rendererType,
               String behaviorId, String validatorId, String converterId,
               List<TagAttribute> attributes) {
        this.type = type;
        this.tagName = tagName;
        this.description = description;
        this.componentType = componentType;
        this.handlerClass = handlerClass;
        this.rendererType = rendererType;
        this.behaviorId = behaviorId;
        this.validatorId = validatorId;
        this.converterId = converterId;
        this.attributes = attributes;
    }

    public TagType getType() {
        return type;
    }

    public String getTagName() {
        return tagName;
    }

    public String getDescription() {
        return description;
    }

    public String getComponentType() {
        return componentType;
    }

    public String getHandlerClass() {
        return handlerClass;
    }

    public String getRendererType() {
        return rendererType;
    }

    public String getBehaviorId() {
        return behaviorId;
    }

    public String getValidatorId() {
        return validatorId;
    }

    public String getConverterId() {
        return converterId;
    }

    public List<TagAttribute> getAttributes() {
        return attributes;
    }
}
