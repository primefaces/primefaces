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
import org.primefaces.cdk.api.Function;
import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.impl.CdkUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.FacesBehavior;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.TagHandler;

public final class TaglibUtils {

    private static final Logger LOG = Logger.getLogger(TaglibUtils.class.getName());

    private static final String DEFAULT_RENDERER_NAME = "DEFAULT_RENDERER";

    private TaglibUtils() {

    }

    public enum TagType {
        BEHAVIOR,
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

    public static ComponentInfo getComponentInfo(Class<?> componentClass) throws IllegalAccessException {
        ComponentInfo info = new ComponentInfo(componentClass,
                getComponentDescription(componentClass),
                getComponentType(componentClass),
                getRendererType(componentClass),
                getTagName(componentClass),
                getComponentHandlerClass(componentClass));

        for (Map.Entry<String, PrimePropertyKeys> value : findAllProperties(componentClass, TagType.COMPONENT).entrySet()) {
            PropertyInfo pi = TaglibUtils.findPropertyInfo(componentClass, value.getKey(), value.getValue(), TagType.COMPONENT);
            info.getProperties().add(pi);
        }

        return info;
    }

    public static BehaviorInfo getBehaviorInfo(Class<?> behaviorClass) throws IllegalAccessException {
        BehaviorInfo info = new BehaviorInfo(behaviorClass,
                getBehaviorDescription(behaviorClass),
                getBehaviorId(behaviorClass),
                getRendererType(behaviorClass),
                getTagName(behaviorClass),
                getBehaviorHandlerClass(behaviorClass));

        for (Map.Entry<String, PrimePropertyKeys> value : findAllProperties(behaviorClass, TagType.BEHAVIOR).entrySet()) {
            PropertyInfo pi = TaglibUtils.findPropertyInfo(behaviorClass, value.getKey(), value.getValue(), TagType.BEHAVIOR);
            info.getProperties().add(pi);
        }

        return info;
    }

    public static TagHandlerInfo getTagHandlerInfo(Class<?> tagHandlerClass) throws IllegalAccessException {
        TagHandlerInfo info = new TagHandlerInfo(tagHandlerClass,
                getTagHandlerDescription(tagHandlerClass),
                getTagName(tagHandlerClass));

        for (Map.Entry<String, PrimePropertyKeys> value : findAllProperties(tagHandlerClass, TagType.TAG_HANDLER).entrySet()) {
            PropertyInfo pi = TaglibUtils.findPropertyInfo(tagHandlerClass, value.getKey(), value.getValue(), TagType.TAG_HANDLER);
            info.getProperties().add(pi);
        }

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

    private static Class<? extends TagHandler> getBehaviorHandlerClass(Class<?> behaviorClass) {
        FacesBehaviorHandler annotation = behaviorClass.getAnnotation(FacesBehaviorHandler.class);
        return annotation == null ? null : annotation.value();
    }

    private static Class<? extends ComponentHandler> getComponentHandlerClass(Class<?> componentClass) {
        FacesComponentHandler annotation = componentClass.getAnnotation(FacesComponentHandler.class);
        return annotation == null ? null : annotation.value();
    }

    private static PropertyInfo findPropertyInfo(Class<?> clazz, String name, PrimePropertyKeys ppk, TagType tagType) {

        Class<?> type = null;
        String description = null;
        boolean required = false;
        String defaultValue = null;
        String implicitDefaultValue = null;
        boolean remove = false;

        if (ppk != null) {
            type = ppk.getType();
            description = ppk.getDescription();
            required = ppk.isRequired();
            defaultValue = ppk.getDefaultValue();
            implicitDefaultValue = ppk.getImplicitDefaultValue();
        }
        else {
            Property property = findProperty(clazz, name, tagType);
            if (property != null) {
                type = property.type();
                description = property.description();
                required = property.required();
                defaultValue = property.defaultValue();
                implicitDefaultValue = property.implicitDefaultValue();
                remove = property.hide();
            }

            if (type == null) {
                if (tagType == TagType.COMPONENT || tagType == TagType.BEHAVIOR) {
                    Method getter = findGetter(clazz, name);
                    if (getter == null && !("id".equals(name) || "binding".equals(name) || "rendered".equals(name))) {
                        return null;
                    }
                    if (getter != null) {
                        type = getter.getReturnType();
                    }
                }
            }
        }

        PropertyInfo propertyInfo = new PropertyInfo(name, description, type, required, defaultValue, implicitDefaultValue, remove);
        if (propertyInfo.getDescription() == null) {
            switch (name) {
                case "id":
                    propertyInfo.setDescription("Unique identifier of the component in a namingContainer.");
                    propertyInfo.setImplicitDefaultValue("generated");
                    break;
                case "binding":
                    propertyInfo.setDescription("An el expression referring to a server side UIComponent instance in a backing bean.");
                    break;
                case "rendered":
                    propertyInfo.setDescription("Boolean value to specify the rendering of the component, when set to false component will not be rendered.");
                    break;
            }
        }
        if (propertyInfo.getType() == null) {
            switch (name) {
                case "id":
                    propertyInfo.setType(String.class);
                    break;
                case "rendered":
                    propertyInfo.setType(boolean.class);
                    break;
                case "binding":
                    propertyInfo.setType(UIComponent.class);
                    break;
            }
        }

        return propertyInfo;
    }

    private static Property findProperty(Class<?> clazz, String name, TagType tagType) {
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            if (tagType == TagType.COMPONENT || tagType == TagType.BEHAVIOR) {
                for (Method method : current.getDeclaredMethods()) {
                    Property property = method.getAnnotation(Property.class);
                    if (property != null && isGetterForProperty(method, name)) {
                        return property;
                    }
                }
            }
            else if (tagType == TagType.TAG_HANDLER) {
                for (Field field : current.getDeclaredFields()) {
                    Property property = field.getAnnotation(Property.class);
                    if (property != null && field.getName().equals(name)) {
                        return property;
                    }
                }
            }
            current = current.getSuperclass();
        }

        return null;
    }

    private static Method findGetter(Class<?> clazz, String name) {
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            for (Method method : current.getDeclaredMethods()) {
                if (isGetterForProperty(method, name)) {
                    return method;
                }
            }
            current = current.getSuperclass();
        }

        return null;
    }

    private static boolean isGetterForProperty(Method method, String propertyName) {
        String methodName = method.getName();

        // Handle "is" prefix for boolean getters
        if (methodName.startsWith("is") && methodName.length() > 2) {
            String extracted = methodName.substring(2);
            if (extracted.equalsIgnoreCase(propertyName)) {
                return true;
            }
        }

        // Handle "get" prefix for regular getters
        if (methodName.startsWith("get") && methodName.length() > 3) {
            String extracted = methodName.substring(3);
            if (extracted.equalsIgnoreCase(propertyName)) {
                return true;
            }
        }

        return false;
    }

    private static Map<String, PrimePropertyKeys> findAllProperties(Class<?> clazz, TagType tagType) {
        Map<String, PrimePropertyKeys> properties = new HashMap<>();
        if (tagType == TagType.COMPONENT) {
            properties.put("id", null);
            properties.put("rendered", null);
            properties.put("binding", null);
        }

        Class<?> cls = clazz;
        while (cls != null && !cls.equals(Object.class)) {
            // Look for nested PropertyKeys enum
            if (tagType == TagType.COMPONENT || tagType == TagType.BEHAVIOR) {
                for (Class<?> innerClass : cls.getDeclaredClasses()) {
                    if (innerClass.isEnum() && innerClass.getSimpleName().equals("PropertyKeys")) {
                        Object[] enumConstants = innerClass.getEnumConstants();
                        if (enumConstants == null) {
                            continue;
                        }

                        for (Object enumConstant : enumConstants) {
                            String propertyName = ((Enum<?>) enumConstant).name();
                            if (enumConstant instanceof PrimePropertyKeys) {
                                propertyName = ((PrimePropertyKeys) enumConstant).getName();
                            }

                            if (properties.containsKey(propertyName) || CdkUtils.shouldIgnoreProperty(cls, propertyName)) {
                                continue;
                            }
                            if (enumConstant instanceof PrimePropertyKeys) {
                                properties.put(propertyName, (PrimePropertyKeys) enumConstant);
                            }
                            else {
                                properties.put(propertyName, null);
                            }
                        }
                    }
                }
            }
            else if (tagType == TagType.TAG_HANDLER) {
                for (Field field : cls.getDeclaredFields()) {
                    Property property = field.getAnnotation(Property.class);
                    if (property != null) {
                        properties.put(field.getName(), null);
                    }
                }
            }

            // Move up to the superclass
            cls = cls.getSuperclass();
        }

        return properties;
    }
}
