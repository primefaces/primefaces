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
package org.primefaces.cdk.impl.subclass;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.impl.CdkUtils;
import org.primefaces.cdk.impl.container.BehaviorEventInfo;
import org.primefaces.cdk.impl.container.FacetInfo;
import org.primefaces.cdk.impl.container.PropertyInfo;
import org.primefaces.cdk.impl.literal.FacesBehaviorEventLiteral;
import org.primefaces.cdk.impl.literal.PropertyLiteral;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Scans a JSF component or behavior class hierarchy and returns merged
 * {@link PropertyInfo}, {@link FacetInfo}, and {@link BehaviorEventInfo} instances
 * ready for code generation.
 *
 * <h2>Per-node strategy</h2>
 * <p>For each type in the hierarchy the scanner picks one of two strategies:</p>
 * <ul>
 *   <li><b>PropertyKeys enum present</b> — compiled legacy class; enum constants are read
 *       via reflection (required to see package-private nested enums). The getter is
 *       inspected to decide whether generation is still needed.</li>
 *   <li><b>No PropertyKeys enum</b> — properties are declared via {@code @Property} on
 *       getter methods and read through the Element API.</li>
 * </ul>
 *
 * <h2>Merge order</h2>
 * <p>The hierarchy is walked ancestor-first; child definitions overwrite parent ones for
 * the same property name. Blank metadata fields (description, default values) in the child
 * are back-filled from the parent before the child wins.</p>
 */
public class HierarchyScanner {

    private final ProcessingEnvironment processingEnv;
    private final Messager messager;
    private final ClassLoader processorClassLoader;

    /**
     * Creates a scanner bound to the given processing environment.
     */
    public HierarchyScanner(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.messager = processingEnv.getMessager();
        this.processorClassLoader = getClass().getClassLoader();
    }

    /**
     * Scans the full hierarchy of {@code root} and returns the merged result.
     * Ancestors are processed first so child definitions win on conflict.
     */
    public HierarchyScannerResult scan(TypeElement root) {
        Deque<TypeElement> orderedNodes = collectHierarchyRootFirst(root);

        Map<String, PropertyInfo> mergedProperties = new LinkedHashMap<>();
        Map<String, FacetInfo> mergedFacets = new LinkedHashMap<>();
        Map<String, BehaviorEventInfo> mergedEvents = new LinkedHashMap<>();

        for (TypeElement node : orderedNodes) {
            HierarchyScannerResult nodeResult = scanSingleNode(node);

            for (PropertyInfo p : nodeResult.getProperties()) {
                PropertyInfo before = mergedProperties.get(p.getName());
                if (before != null) {
                    // Back-fill blank metadata from the parent before the child wins.
                    if (p.getAnnotation().description().isBlank()
                            || p.getAnnotation().defaultValue().isBlank()
                            || p.getAnnotation().implicitDefaultValue().isBlank()) {
                        p.setAnnotation(new PropertyLiteral(
                                before.getAnnotation().description(),
                                p.getAnnotation().required(),
                                before.getAnnotation().defaultValue(),
                                before.getAnnotation().implicitDefaultValue(),
                                p.getAnnotation().callSuper(),
                                p.getAnnotation().type(),
                                p.getAnnotation().hide()));
                    }
                }

                mergedProperties.put(p.getName(), p);
            }

            for (FacetInfo f : nodeResult.getFacets()) {
                mergedFacets.put(f.getName(), f);
            }
            for (BehaviorEventInfo e : nodeResult.getBehaviorEvents()) {
                mergedEvents.put(e.getName(), e);
            }
        }

        return new HierarchyScannerResult(
                new ArrayList<>(mergedProperties.values()),
                new ArrayList<>(mergedFacets.values()),
                new ArrayList<>(mergedEvents.values()));
    }

    /**
     * Walks superclasses and interfaces depth-first from {@code root}, returning nodes
     * ancestor-first so child overrides are applied last during merging.
     * Each element is visited at most once.
     */
    private Deque<TypeElement> collectHierarchyRootFirst(TypeElement root) {
        Deque<TypeElement> stack = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        collectRecursive(root, stack, visited);
        return stack;
    }

    /**
     * Pushes {@code element} before recursing so that ancestors end up deeper in the deque than descendants.
     */
    private void collectRecursive(TypeElement element, Deque<TypeElement> result, Set<String> visited) {
        String qualifiedName = element.getQualifiedName().toString();
        if ("java.lang.Object".equals(qualifiedName) || !visited.add(qualifiedName)) {
            return;
        }

        result.addFirst(element);

        TypeMirror superclassMirror = element.getSuperclass();
        if (superclassMirror != null) {
            Element superElement = processingEnv.getTypeUtils().asElement(superclassMirror);
            if (superElement instanceof TypeElement) {
                collectRecursive((TypeElement) superElement, result, visited);
            }
        }

        for (TypeMirror ifaceMirror : element.getInterfaces()) {
            Element ifaceElement = processingEnv.getTypeUtils().asElement(ifaceMirror);
            if (ifaceElement instanceof TypeElement) {
                collectRecursive((TypeElement) ifaceElement, result, visited);
            }
        }
    }

    /**
     * Scans a single type element without recursion; chooses reflection or annotation strategy.
     */
    HierarchyScannerResult scanSingleNode(TypeElement element) {
        return new HierarchyScannerResult(
                scanProperties(element),
                scanFacets(element),
                scanBehaviorEvents(element));
    }

    /**
     * Tries the {@code PropertyKeys} reflection strategy first, then the {@code @Property} annotation strategy.
     */
    private List<PropertyInfo> scanProperties(TypeElement element) {
        String className = element.getQualifiedName().toString();
        try {
            Class<?> clazz = Class.forName(className, false, processorClassLoader);
            return scanPropertyKeysViaReflection(clazz, element);
        }
        catch (ClassNotFoundException ignored) {
            // in-round source — fall through to annotation scan
        }
        catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                    "Reflection failed for " + className + ": " + e.getMessage());
        }
        return scanPropertyAnnotations(element);
    }

    /**
     * Reads properties from a nested {@code PropertyKeys} enum via reflection.
     * <p>For each constant the getter is inspected: a concrete getter means the ancestor
     * already owns the implementation ({@code getterElement = null}, no generation);
     * an abstract getter means a body still needs to be generated.</p>
     *
     * @return list of {@link PropertyInfo}, or {@code null} if no {@code PropertyKeys} enum exists
     */
    private List<PropertyInfo> scanPropertyKeysViaReflection(Class<?> clazz, TypeElement typeElement) {
        List<PropertyInfo> result = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            Property property = method.getAnnotation(Property.class);
            if (property == null) {
                continue;
            }

            String propertyName = extractPropertyName(method.getName());
            PropertyInfo propertyInfo = new PropertyInfo(propertyName, property, null, null,
                    method.getReturnType().getName());

            if (clazz.isInterface()
                    || java.lang.reflect.Modifier.isAbstract(method.getModifiers())) {
                propertyInfo.setGenerateGetter(true);
            }
            if (propertyInfo.isGenerateGetter()
                    || clazz.isInterface()) {
                propertyInfo.setGenerateSetter(true);
            }

            result.add(propertyInfo);
        }

        messager.printMessage(Diagnostic.Kind.NOTE,
                "Found " + result.size() + " @Properties in " + clazz.getName() + " via reflection");

        for (Class<?> inner : clazz.getDeclaredClasses()) {
            if (!inner.isEnum() || !"PropertyKeys".equals(inner.getSimpleName())) {
                continue;
            }

            Object[] constants = inner.getEnumConstants();
            if (constants == null) {
                continue;
            }

            for (Object constant : constants) {
                String keyName = ((Enum<?>) constant).name();
                if (CdkUtils.shouldIgnoreProperty(clazz, keyName)) {
                    continue;
                }

                String propertyName = escapePropertyName(keyName);
                String returnType = resolveReturnTypeViaReflection(clazz, typeElement, propertyName);
                Property property = new PropertyLiteral(
                        CdkUtils.getWellKnownDescription(propertyName),
                        false,
                        CdkUtils.getWellKnownDefaultValue(propertyName),
                        "", false, null, false);

                ExecutableElement getterElement = isGetterConcrete(clazz, propertyName)
                        ? null
                        : findGetterElement(typeElement, propertyName);

                result.add(new PropertyInfo(propertyName, property, getterElement, null, returnType));
            }
        }

        return result;
    }

    /**
     * Returns {@code true} if a concrete (non-abstract) getter for {@code propertyName}
     * exists anywhere in the {@code clazz} superclass chain.
     */
    private boolean isGetterConcrete(Class<?> clazz, String propertyName) {
        String[] names = {
            "get" + capitalize(propertyName),
            "is" + capitalize(propertyName)
        };
        for (Class<?> c = clazz; c != null && !Object.class.equals(c); c = c.getSuperclass()) {
            for (String name : names) {
                try {
                    return !java.lang.reflect.Modifier.isAbstract(c.getDeclaredMethod(name).getModifiers());
                }
                catch (NoSuchMethodException ignored) {
                }
            }
        }
        return false;
    }

    /**
     * Returns the {@link ExecutableElement} for the getter of {@code propertyName}, or {@code null} if not declared here.
     */
    private ExecutableElement findGetterElement(TypeElement element, String propertyName) {
        String[] names = {
            "get" + capitalize(propertyName),
            "is" + capitalize(propertyName)
        };
        for (Element enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement method = (ExecutableElement) enclosed;
            if (method.getParameters().isEmpty()) {
                for (String name : names) {
                    if (name.equals(method.getSimpleName().toString())) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Scans {@code @Property}-annotated getters via the Element API.
     */
    private List<PropertyInfo> scanPropertyAnnotations(TypeElement element) {
        List<PropertyInfo> result = new ArrayList<>();
        for (Element enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement method = (ExecutableElement) enclosed;
            Property annotation = method.getAnnotation(Property.class);
            if (annotation == null) {
                continue;
            }

            String methodName = method.getSimpleName().toString();
            if (!isGetterName(methodName)) {
                messager.printMessage(Diagnostic.Kind.WARNING,
                        "@Property on non-getter '" + methodName + "' in " + element.getQualifiedName() + " — ignored");
                continue;
            }

            String propName = extractPropertyName(methodName);
            ExecutableElement setter = findSetterInElement(element, propName, method.getReturnType());

            PropertyInfo propertyInfo = new PropertyInfo(propName,
                    new PropertyLiteral(annotation.description(), annotation.required(), annotation.defaultValue(),
                            annotation.implicitDefaultValue(), annotation.callSuper(), null, annotation.hide()),
                    method, setter, method.getReturnType().toString());

            if (element.getKind() == ElementKind.INTERFACE
                    || propertyInfo.getGetterElement() == null
                    || propertyInfo.getGetterElement().getModifiers().contains(Modifier.ABSTRACT)) {
                propertyInfo.setGenerateGetter(true);
            }
            if (propertyInfo.isGenerateGetter()
                    || element.getKind() == ElementKind.INTERFACE
                    || propertyInfo.getSetterElement() == null
                    || propertyInfo.getSetterElement().getModifiers().contains(Modifier.ABSTRACT)) {
                propertyInfo.setGenerateSetter(true);
            }

            result.add(propertyInfo);
        }

        return result;
    }

    /**
     * Scans {@code @Facet}-annotated getters on {@code element}.
     */
    private List<FacetInfo> scanFacets(TypeElement element) {
        List<FacetInfo> result = new ArrayList<>();

        for (Element enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement method = (ExecutableElement) enclosed;
            Facet annotation = method.getAnnotation(Facet.class);
            if (annotation == null) {
                continue;
            }

            String methodName = method.getSimpleName().toString();
            if (!isGetterName(methodName)) {
                messager.printMessage(Diagnostic.Kind.WARNING,
                        "@Facet on non-getter '" + methodName + "' in " + element.getQualifiedName() + " — ignored");
                continue;
            }

            result.add(new FacetInfo(extractFacetName(methodName), method.getReturnType().toString(), method, annotation));
        }

        return result;
    }

    /**
     * Reads {@code @FacesBehaviorEvent} / {@code @FacesBehaviorEvents} via {@link AnnotationMirror}
     * because the {@code event} class attribute cannot be accessed safely at compile time.
     */
    private List<BehaviorEventInfo> scanBehaviorEvents(TypeElement element) {
        List<BehaviorEventInfo> result = new ArrayList<>();

        for (AnnotationMirror am : element.getAnnotationMirrors()) {
            String type = am.getAnnotationType().toString();
            if (FacesBehaviorEvents.class.getName().equals(type)) {
                extractBehaviorEvents(am, result);
            }
            else if (FacesBehaviorEvent.class.getName().equals(type)) {
                BehaviorEventInfo info = extractBehaviorEvent(am);
                if (info != null) {
                    result.add(info);
                }
            }
        }

        return result;
    }

    /**
     * Unpacks the repeated {@code @FacesBehaviorEvent} values from a {@code @FacesBehaviorEvents} container.
     */
    private void extractBehaviorEvents(AnnotationMirror containerMirror, List<BehaviorEventInfo> result) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                : containerMirror.getElementValues().entrySet()) {
            if (!"value".equals(entry.getKey().getSimpleName().toString())) {
                continue;
            }
            @SuppressWarnings("unchecked")
            List<? extends AnnotationValue> repeated = (List<? extends AnnotationValue>) entry.getValue().getValue();
            for (AnnotationValue av : repeated) {
                BehaviorEventInfo info = extractBehaviorEvent((AnnotationMirror) av.getValue());
                if (info != null) {
                    result.add(info);
                }
            }
        }
    }

    /**
     * Extracts a single {@link BehaviorEventInfo} from a {@code @FacesBehaviorEvent} mirror, or {@code null} if incomplete.
     */
    private BehaviorEventInfo extractBehaviorEvent(AnnotationMirror mirror) {
        String name = null;
        String eventClassName = null;
        String description = "";
        boolean implicit = false;
        boolean defaultEvent = false;

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                : mirror.getElementValues().entrySet()) {
            switch (entry.getKey().getSimpleName().toString()) {
                case "name":
                    name = (String)  entry.getValue().getValue();
                    break;
                case "event":
                    eventClassName = entry.getValue().getValue().toString();
                    break;
                case "description":
                    description = (String)  entry.getValue().getValue();
                    break;
                case "implicit":
                    implicit = (boolean) entry.getValue().getValue();
                    break;
                case "defaultEvent":
                    defaultEvent = (boolean) entry.getValue().getValue();
                    break;
                default:
                    break;
            }
        }

        if (name == null) {
            return null;
        }

        return new BehaviorEventInfo(name,
                new FacesBehaviorEventLiteral(name, null, description, implicit, defaultEvent),
                eventClassName);
    }

    /**
     * Resolves the return type via reflection, falling back to the Element API.
     */
    private String resolveReturnTypeViaReflection(Class<?> clazz, TypeElement typeElement, String propertyName) {
        String[] names = {
            "get" + capitalize(propertyName),
            "is" + capitalize(propertyName)
        };
        for (Class<?> c = clazz; c != null && !Object.class.equals(c); c = c.getSuperclass()) {
            for (String name : names) {
                try {
                    return c.getDeclaredMethod(name).getReturnType().getName();
                }
                catch (NoSuchMethodException ignored) {
                }
            }
        }
        return resolveReturnTypeViaElementApi(typeElement, propertyName);
    }

    /**
     * Resolves the return type via the Element API; falls back to {@code java.lang.Object} if the getter is not found.
     */
    private String resolveReturnTypeViaElementApi(TypeElement element, String propertyName) {
        String[] names = {
            "get" + capitalize(propertyName),
            "is" + capitalize(propertyName)
        };
        for (Element enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement method = (ExecutableElement) enclosed;
            if (method.getParameters().isEmpty()) {
                for (String name : names) {
                    if (name.equals(method.getSimpleName().toString())) {
                        return method.getReturnType().toString();
                    }
                }
            }
        }
        return "java.lang.Object";
    }

    /**
     * Finds a setter declared directly in {@code element}.
     * Returns {@code null} if absent — the generator walks the full hierarchy separately.
     */
    private ExecutableElement findSetterInElement(TypeElement element, String propertyName, TypeMirror getterReturnType) {
        String expectedName = "set" + capitalize(propertyName);
        for (Element enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement method = (ExecutableElement) enclosed;
            if (expectedName.equals(method.getSimpleName().toString())
                    && method.getParameters().size() == 1
                    && method.getParameters().get(0).asType().toString().equals(getterReturnType.toString())) {
                return method;
            }
        }
        return null;
    }

    /**
     * Returns {@code true} for {@code getXxx} and {@code isXxx} method names.
     */
    static boolean isGetterName(String methodName) {
        return methodName.startsWith("get") || methodName.startsWith("is");
    }

    /**
     * Derives the property name from a getter or setter method name, then applies
     * {@link #escapePropertyName} to normalise keyword-escaping conventions.
     */
    static String extractPropertyName(String methodName) {
        String core;
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            core = methodName.substring(3);
        }
        else if (methodName.startsWith("is")) {
            core = methodName.substring(2);
        }
        else {
            core = methodName;
        }
        return escapePropertyName(decap(core));
    }

    /**
     * Normalises keyword-escaped property names:
     * <ul>
     *   <li>{@code _for} → {@code for} (leading underscore used as keyword workaround)</li>
     *   <li>{@code forValue} / {@code forVal} → {@code for} (suffix used as keyword workaround)</li>
     * </ul>
     */
    static String escapePropertyName(String name) {
        if (name.startsWith("_")) {
            return name.substring(1);
        }
        if (name.endsWith("Val") || name.endsWith("Value")) {
            String prefix = name.substring(0, name.lastIndexOf("Val"));
            if (CdkUtils.isJavaKeyword(prefix)) {
                return prefix;
            }
        }
        return name;
    }

    /**
     * Derives the facet name from a getter, stripping a trailing {@code Facet} suffix.
     * {@code getHeaderFacet} → {@code header}, {@code getContent} → {@code content}.
     */
    static String extractFacetName(String methodName) {
        String propName = extractPropertyName(methodName);
        return propName.endsWith("Facet")
                ? propName.substring(0, propName.length() - "Facet".length())
                : propName;
    }

    /**
     * Decapitalises the first character of {@code s}.
     */
    static String decap(String s) {
        return (s == null || s.isEmpty()) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Capitalises the first character of {@code s}.
     */
    static String capitalize(String s) {
        return (s == null || s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}