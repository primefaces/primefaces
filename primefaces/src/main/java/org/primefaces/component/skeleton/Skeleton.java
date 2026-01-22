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
package org.primefaces.component.skeleton;

import org.primefaces.cdk.api.FacesComponentDescription;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;

@FacesComponent(value = Skeleton.COMPONENT_TYPE, namespace = Skeleton.COMPONENT_FAMILY)
@FacesComponentDescription("Skeleton is a placeholder to display instead of the actual content.")
@ResourceDependency(library = "primefaces", name = "components.css")
public class Skeleton extends SkeletonBaseImpl {
    public static final String COMPONENT_TYPE = "org.primefaces.component.Skeleton";

    public static final String STYLE_CLASS = "ui-skeleton ui-widget";
    public static final String CIRCLE_CLASS = "ui-skeleton-circle";
    public static final String NONE_ANIMATION_CLASS = "ui-skeleton-animation-none";
}
