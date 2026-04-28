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
import org.primefaces.cdk.api.FacesConverterInfo;
import org.primefaces.cdk.api.FacesTagHandler;
import org.primefaces.cdk.api.FacesValidatorInfo;
import org.primefaces.cdk.api.Function;
import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.converter.PrimeConverterHandler;
import org.primefaces.cdk.api.validator.PrimeValidatorHandler;
import org.primefaces.cdk.impl.CdkUtils;
import org.primefaces.cdk.impl.container.BehaviorInfo;
import org.primefaces.cdk.impl.container.ComponentInfo;
import org.primefaces.cdk.impl.container.ConverterInfo;
import org.primefaces.cdk.impl.container.FunctionInfo;
import org.primefaces.cdk.impl.container.TagHandlerInfo;
import org.primefaces.cdk.impl.container.ValidatorInfo;
import org.primefaces.cdk.spi.taglib.TagType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
        FacesBehaviorInfo facesBehaviorInfo = clazz.getAnnotation(FacesBehaviorInfo.class);
        if (facesBehaviorInfo != null && !facesBehaviorInfo.name().isEmpty()) {
            return facesBehaviorInfo.name();
        }

        FacesComponentInfo facesComponentInfo = clazz.getAnnotation(FacesComponentInfo.class);
        if (facesComponentInfo != null && !facesComponentInfo.name().isEmpty()) {
            return facesComponentInfo.name();
        }

        FacesValidatorInfo facesValidatorInfo = clazz.getAnnotation(FacesValidatorInfo.class);
        if (facesValidatorInfo != null && !facesValidatorInfo.name().isEmpty()) {
            return facesValidatorInfo.name();
        }

        FacesConverterInfo facesConverterInfo = clazz.getAnnotation(FacesConverterInfo.class);
        if (facesConverterInfo != null && !facesConverterInfo.name().isEmpty()) {
            return facesConverterInfo.name();
        }

        FacesTagHandler facesTagHandler = clazz.getAnnotation(FacesTagHandler.class);
        if (facesTagHandler != null && !facesTagHandler.name().isEmpty()) {
            return facesTagHandler.name();
        }

        String name = String.valueOf(clazz.getSimpleName().charAt(0)).toLowerCase() +
                clazz.getSimpleName().substring(1);
        if (name.endsWith("Behavior")) {
            name = name.substring(0, name.length() - "Behavior".length());
        }
        else if (name.endsWith("TagHandler")) {
            name = name.substring(0, name.length() - "TagHandler".length());
        }
        else if (name.endsWith("Converter")) {
            name = name.substring(0, name.length() - "Converter".length());
        }
        else if (name.endsWith("Validator")) {
            name = name.substring(0, name.length() - "Validator".length());
        }
        return name;
    }

    private static String getDescription(Class<?> clazz) {
        FacesComponentInfo facesComponentInfo = clazz.getAnnotation(FacesComponentInfo.class);
        if (facesComponentInfo != null) return facesComponentInfo.description();

        FacesBehaviorInfo facesBehaviorInfo = clazz.getAnnotation(FacesBehaviorInfo.class);
        if (facesBehaviorInfo != null) return facesBehaviorInfo.description();

        FacesValidatorInfo facesValidatorInfo = clazz.getAnnotation(FacesValidatorInfo.class);
        if (facesValidatorInfo != null) return facesValidatorInfo.description();

        FacesConverterInfo facesConverterInfo = clazz.getAnnotation(FacesConverterInfo.class);
        if (facesConverterInfo != null) return facesConverterInfo.description();

        FacesTagHandler facesTagHandler = clazz.getAnnotation(FacesTagHandler.class);
        if (facesTagHandler != null) return facesTagHandler.description();

        return null;
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
                getDescription(componentClass),
                getComponentType(componentClass),
                getRendererType(componentClass),
                getTagName(componentClass),
                getComponentHandlerClass(componentClass));

        info.setProperties(findAllProperties(componentClass, TagType.COMPONENT));

        return info;
    }

    public static BehaviorInfo getBehaviorInfo(Class<?> behaviorClass) throws IllegalAccessException, ClassNotFoundException {
        BehaviorInfo info = new BehaviorInfo(behaviorClass,
                getDescription(behaviorClass),
                getBehaviorId(behaviorClass),
                getRendererType(behaviorClass),
                getTagName(behaviorClass),
                getBehaviorHandlerClass(behaviorClass));

        info.setProperties(findAllProperties(behaviorClass, TagType.BEHAVIOR));

        return info;
    }

    public static ValidatorInfo getValidatorInfo(Class<?> validatorClass) throws ClassNotFoundException {
        ValidatorInfo info = new ValidatorInfo(validatorClass,
                getDescription(validatorClass),
                getValidatorId(validatorClass),
                getTagName(validatorClass),
                PrimeValidatorHandler.class);

        info.setProperties(findAllProperties(validatorClass, TagType.VALIDATOR));

        return info;
    }

    public static ConverterInfo getConverterInfo(Class<?> converterClass) throws ClassNotFoundException {
        ConverterInfo info = new ConverterInfo(converterClass,
                getDescription(converterClass),
                getTagName(converterClass),
                PrimeConverterHandler.class);

        info.setProperties(findAllProperties(converterClass, TagType.CONVERTER));

        return info;
    }

    public static TagHandlerInfo getTagHandlerInfo(Class<?> tagHandlerClass) throws ClassNotFoundException {
        TagHandlerInfo info = new TagHandlerInfo(tagHandlerClass,
                getDescription(tagHandlerClass),
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
        Map<String, Property> properties = new TreeMap<>(); // sorted by property name

        if (tagType == TagType.COMPONENT || tagType == TagType.BEHAVIOR || tagType == TagType.VALIDATOR || tagType == TagType.CONVERTER) {
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
                                Property.Literal.of((PrimePropertyKeys) enumConstant));
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
