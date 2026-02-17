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
package org.primefaces.cdk.impl.taglib;

import org.primefaces.cdk.api.FacesBehaviorHandler;
import org.primefaces.cdk.api.FacesBehaviorInfo;
import org.primefaces.cdk.api.FacesComponentHandler;
import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.cdk.api.FacesTagHandler;
import org.primefaces.cdk.api.FacesValidatorInfo;
import org.primefaces.cdk.api.Function;
import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.validator.PrimeValidatorHandler;
import org.primefaces.cdk.impl.CdkUtils;
import org.primefaces.cdk.impl.container.BehaviorInfo;
import org.primefaces.cdk.impl.container.ComponentInfo;
import org.primefaces.cdk.impl.container.FunctionInfo;
import org.primefaces.cdk.impl.container.TagHandlerInfo;
import org.primefaces.cdk.impl.container.ValidatorInfo;
import org.primefaces.cdk.impl.literal.PropertyLiteral;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.behavior.FacesBehavior;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.TagHandler;

public final class TaglibUtils {

    private static final Logger LOG = Logger.getLogger(TaglibUtils.class.getName());

    private static final String DEFAULT_RENDERER_NAME = "DEFAULT_RENDERER";

    private TaglibUtils() {

    }

    public enum TagType {
        BEHAVIOR,
        VALIDATOR,
        COMPONENT,
        TAG_HANDLER
    }

    public static String getRendererType(Class<?> componentClass) throws IllegalAccessException {
        String rendererType = null;
        try {
            Field field = componentClass.getDeclaredField(DEFAULT_RENDERER_NAME);
            rendererType = (String) field.get(null);
        }
        catch (NoSuchFieldException e) {
            LOG.fine("No " + DEFAULT_RENDERER_NAME + " field found for: " + componentClass.getName());
        }

        return rendererType;
    }

    private static String getTagName(Class<?> clazz) {
        FacesBehaviorInfo behaviorInfo = clazz.getAnnotation(FacesBehaviorInfo.class);
        if (behaviorInfo != null && !behaviorInfo.name().isEmpty()) {
            return behaviorInfo.name();
        }

        FacesComponentInfo componentInfo = clazz.getAnnotation(FacesComponentInfo.class);
        if (componentInfo != null && !componentInfo.name().isEmpty()) {
            return componentInfo.name();
        }

        FacesValidatorInfo validatorInfo = clazz.getAnnotation(FacesValidatorInfo.class);
        if (validatorInfo != null && !validatorInfo.name().isEmpty()) {
            return validatorInfo.name();
        }

        String name = String.valueOf(clazz.getSimpleName().charAt(0)).toLowerCase() +
                clazz.getSimpleName().substring(1);
        if (name.endsWith("Behavior")) {
            name = name.substring(0, name.length() - "Behavior".length());
        }
        if (name.endsWith("TagHandler")) {
            name = name.substring(0, name.length() - "TagHandler".length());
        }
        return name;
    }

    private static String getComponentDescription(Class<?> componentClass) {
        FacesComponentInfo annotation = componentClass.getAnnotation(FacesComponentInfo.class);
        return annotation == null ? null : annotation.description();
    }

    private static String getBehaviorDescription(Class<?> behaviorClass) {
        FacesBehaviorInfo annotation = behaviorClass.getAnnotation(FacesBehaviorInfo.class);
        return annotation == null ? null : annotation.description();
    }

    private static String getValidatorDescription(Class<?> behaviorClass) {
        FacesValidatorInfo annotation = behaviorClass.getAnnotation(FacesValidatorInfo.class);
        return annotation == null ? null : annotation.description();
    }

    private static String getTagHandlerDescription(Class<?> tagHandlerClass) {
        FacesTagHandler annotation = tagHandlerClass.getAnnotation(FacesTagHandler.class);
        return annotation == null ? null : annotation.value();
    }

    private static String getComponentType(Class<?> componentClass) {
        FacesComponent annotation = componentClass.getAnnotation(FacesComponent.class);
        return annotation.value();
    }

    public static List<FunctionInfo> getFunctionInfos(Class<?> clazz) {
        List<FunctionInfo> functions = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            Function annotation = method.getAnnotation(Function.class);
            if (annotation == null) {
                continue;
            }

            // Determine function name
            String functionName = annotation.name();
            if (functionName == null || functionName.isEmpty()) {
                functionName = method.getName();
            }

            // Get function signature
            StringBuilder signature = new StringBuilder();
            signature.append(method.getReturnType().getName());
            signature.append(" ");
            signature.append(method.getName());
            signature.append("(");

            Class<?>[] paramTypes = method.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                signature.append(paramTypes[i].getName());
                if (i < paramTypes.length - 1) {
                    signature.append(",");
                }
            }
            signature.append(")");

            FunctionInfo info = new FunctionInfo(
                    functionName,
                    clazz.getName(),
                    signature.toString(),
                    annotation.description()
            );

            functions.add(info);
        }

        return functions;
    }

    public static ComponentInfo getComponentInfo(Class<?> componentClass) throws IllegalAccessException, ClassNotFoundException {
        ComponentInfo info = new ComponentInfo(componentClass,
                getComponentDescription(componentClass),
                getComponentType(componentClass),
                getRendererType(componentClass),
                getTagName(componentClass),
                getComponentHandlerClass(componentClass));

        info.setProperties(findAllProperties(componentClass, TagType.COMPONENT));

        return info;
    }

    public static BehaviorInfo getBehaviorInfo(Class<?> behaviorClass) throws IllegalAccessException, ClassNotFoundException {
        BehaviorInfo info = new BehaviorInfo(behaviorClass,
                getBehaviorDescription(behaviorClass),
                getBehaviorId(behaviorClass),
                getRendererType(behaviorClass),
                getTagName(behaviorClass),
                getBehaviorHandlerClass(behaviorClass));

        info.setProperties(findAllProperties(behaviorClass, TagType.BEHAVIOR));

        return info;
    }

    public static ValidatorInfo getValidatorInfo(Class<?> validatorClass) throws IllegalAccessException, ClassNotFoundException {
        ValidatorInfo info = new ValidatorInfo(validatorClass,
                getValidatorDescription(validatorClass),
                getValidatorId(validatorClass),
                getTagName(validatorClass),
                PrimeValidatorHandler.class);

        info.setProperties(findAllProperties(validatorClass, TagType.VALIDATOR));

        return info;
    }

    public static TagHandlerInfo getTagHandlerInfo(Class<?> tagHandlerClass) throws IllegalAccessException, ClassNotFoundException {
        TagHandlerInfo info = new TagHandlerInfo(tagHandlerClass,
                getTagHandlerDescription(tagHandlerClass),
                getTagName(tagHandlerClass));

        info.setProperties(findAllProperties(tagHandlerClass, TagType.TAG_HANDLER));

        return info;
    }

    private static String getBehaviorId(Class<?> behaviorClass) {
        try {
            FacesBehavior annotation = behaviorClass.getAnnotation(FacesBehavior.class);
            String value = annotation.value();
            if (value != null && !value.isEmpty()) {
                return value;
            }

            // Fall back to fully qualified class name
            return behaviorClass.getName();
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to get behavior ID for " + behaviorClass.getName(), e);
            return behaviorClass.getName();
        }
    }

    private static String getValidatorId(Class<?> validatorClass) {
        try {
            FacesValidator annotation = validatorClass.getAnnotation(FacesValidator.class);
            String value = annotation.value();
            if (value != null && !value.isEmpty()) {
                return value;
            }

            // Fall back to fully qualified class name
            return validatorClass.getName();
        }
        catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to get validator ID for " + validatorClass.getName(), e);
            return validatorClass.getName();
        }
    }

    private static Class<? extends TagHandler> getBehaviorHandlerClass(Class<?> behaviorClass) {
        FacesBehaviorHandler annotation = behaviorClass.getAnnotation(FacesBehaviorHandler.class);
        return annotation == null ? null : annotation.value();
    }

    private static Class<? extends ComponentHandler> getComponentHandlerClass(Class<?> componentClass) {
        FacesComponentHandler annotation = componentClass.getAnnotation(FacesComponentHandler.class);
        return annotation == null ? null : annotation.value();
    }

    private static Map<String, Property> findAllProperties(Class<?> clazz, TagType tagType) throws ClassNotFoundException {
        Map<String, Property> properties = new HashMap<>();

        if (tagType == TagType.COMPONENT || tagType == TagType.BEHAVIOR || tagType == TagType.VALIDATOR) {
            // Superclass = BaseImpl
            for (Class<?> inner : clazz.getSuperclass().getDeclaredClasses()) {
                if (inner.getSimpleName().equals("PropertyKeys")) {
                    Object[] enumConstants = inner.getEnumConstants();
                    if (enumConstants == null) {
                        return null;
                    }

                    for (Object enumConstant : enumConstants) {
                        String propertyName = ((Enum<?>) enumConstant).name();
                        if (enumConstant instanceof PrimePropertyKeys) {
                            propertyName = ((PrimePropertyKeys) enumConstant).getName();
                        }

                        if (properties.containsKey(propertyName) || CdkUtils.shouldIgnoreProperty(clazz, propertyName)) {
                            continue;
                        }

                        properties.put(propertyName,
                                PropertyLiteral.fromPropertyKeys((PrimePropertyKeys) enumConstant));
                    }
                }
            }
        }

        if (tagType == TagType.TAG_HANDLER) {
            Class<?> cls = clazz;
            while (cls != null && !cls.equals(Object.class)) {
                for (Field field : cls.getDeclaredFields()) {
                    Property property = field.getAnnotation(Property.class);
                    if (property != null) {
                        properties.put(field.getName(), property);
                    }
                }
                cls = cls.getSuperclass();
            }
        }

        return properties;
    }
}
