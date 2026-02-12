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

import org.primefaces.cdk.api.FacesBehaviorBase;
import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
import org.primefaces.cdk.api.PrimeFacetKeys;
import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.component.PrimeClientBehaviorHolder;
import org.primefaces.cdk.api.component.PrimeComponent;
import org.primefaces.cdk.impl.CdkUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Generates implementation classes for JSF component and behavior base classes.
 *
 * <p>Processes abstract classes that are annotated with {@code @FacesComponentBase} or {@code @FacesBehaviorBase}.
 * Scans the class hierarchy and implemented interfaces for {@code @Property}, {@code @Facet},
 * and {@code @FacesBehaviorEvent} annotations.</p>
 *
 * <p>Generated classes include:</p>
 * <ul>
 *   <li>PropertyKeys enum with StateHelper-backed getters/setters</li>
 *   <li>FacetKeys enum with facet accessors</li>
 *   <li>ClientBehaviorEventKeys enum with ClientBehaviorHolder implementation</li>
 *   <li>PrimeComponent interface implementation for non-behaviors</li>
 * </ul>
 *
 * @see Property
 * @see Facet
 * @see FacesBehaviorEvent
 * @see FacesComponentBase
 */
@SupportedAnnotationTypes({
    "org.primefaces.cdk.api.Property",
    "org.primefaces.cdk.api.Facet",
    "org.primefaces.cdk.api.FacesBehaviorBase",
    "org.primefaces.cdk.api.FacesBehaviorEvent",
    "org.primefaces.cdk.api.FacesBehaviorEvents",
    "org.primefaces.cdk.api.FacesComponentBase"
})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class AnnotationProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.filer = env.getFiler();
        this.messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        Set<TypeElement> componentsToGenerate = new HashSet<>();
        Map<TypeElement, Set<ExecutableElement>> annotatedPropertiesByType = new HashMap<>();
        Map<TypeElement, Set<ExecutableElement>> annotatedFacetsByType = new HashMap<>();
        Map<TypeElement, List<BehaviorEventInfo>> annotatedBehaviorEventsByType = new HashMap<>();

        // Collect base classes marked with @FacesComponentBase
        for (Element e : roundEnv.getElementsAnnotatedWith(FacesComponentBase.class)) {
            if (e.getKind() == ElementKind.CLASS && e.getModifiers().contains(Modifier.ABSTRACT)) {
                TypeElement typeElement = (TypeElement) e;
                componentsToGenerate.add(typeElement);

                // Immediately scan the hierarchy for all annotations
                // This ensures we find @Property on compiled interfaces from dependencies
                scanHierarchyForAllAnnotations(typeElement, annotatedPropertiesByType,
                        annotatedFacetsByType, annotatedBehaviorEventsByType);
            }
        }

        // Collect base classes marked with @FacesBehaviorBase
        for (Element e : roundEnv.getElementsAnnotatedWith(FacesBehaviorBase.class)) {
            if (e.getKind() == ElementKind.CLASS && e.getModifiers().contains(Modifier.ABSTRACT)) {
                TypeElement typeElement = (TypeElement) e;
                componentsToGenerate.add(typeElement);

                // Immediately scan the hierarchy for all annotations
                scanHierarchyForAllAnnotations(typeElement, annotatedPropertiesByType,
                        annotatedFacetsByType, annotatedBehaviorEventsByType);
            }
        }

        // Also collect from annotations found in current round (for interfaces/classes being compiled now)
        collectAnnotatedMethods(roundEnv, Property.class, annotatedPropertiesByType, componentsToGenerate);
        collectAnnotatedMethods(roundEnv, Facet.class, annotatedFacetsByType, componentsToGenerate);
        collectAnnotatedBehaviorEvents(roundEnv, annotatedBehaviorEventsByType, componentsToGenerate);

        // Build property/facet/event targets for code generation
        Map<TypeElement, Set<ExecutableElement>> propertyTargets = new HashMap<>();
        Map<TypeElement, Set<ExecutableElement>> facetTargets = new HashMap<>();
        Map<TypeElement, List<BehaviorEventInfo>> behaviorEventTargets = new HashMap<>();

        for (TypeElement classElement : componentsToGenerate) {
            Set<ExecutableElement> allProperties = new LinkedHashSet<>();
            Set<ExecutableElement> allFacets = new LinkedHashSet<>();
            List<BehaviorEventInfo> allBehaviorEvents = new ArrayList<>();

            scanHierarchyForAnnotations(classElement, annotatedPropertiesByType, annotatedFacetsByType,
                    annotatedBehaviorEventsByType, allProperties, allFacets, allBehaviorEvents);

            if (!allProperties.isEmpty()) {
                propertyTargets.put(classElement, allProperties);
            }
            if (!allFacets.isEmpty()) {
                facetTargets.put(classElement, allFacets);
            }
            if (!allBehaviorEvents.isEmpty()) {
                behaviorEventTargets.put(classElement, allBehaviorEvents);
            }
        }

        // Generate implementation classes
        for (TypeElement classElement : componentsToGenerate) {
            generateComponent(classElement, annotatedPropertiesByType, annotatedFacetsByType,
                    propertyTargets, facetTargets, behaviorEventTargets);
        }

        return true;
    }

    /**
     * Scans entire class hierarchy (including compiled interfaces from dependencies)
     * and populates the annotation maps. This is called immediately when we find a
     * @FacesComponentBase/@FacesBehaviorBase class.
     */
    private void scanHierarchyForAllAnnotations(TypeElement element,
                                                Map<TypeElement, Set<ExecutableElement>> annotatedPropertiesByType,
                                                Map<TypeElement, Set<ExecutableElement>> annotatedFacetsByType,
                                                Map<TypeElement, List<BehaviorEventInfo>> annotatedBehaviorEventsByType) {
        Set<TypeElement> visited = new HashSet<>();
        scanHierarchyForAllAnnotationsRecursive(element, annotatedPropertiesByType,
                annotatedFacetsByType, annotatedBehaviorEventsByType, visited);
    }

    /**
     * Recursively scans the entire class hierarchy including:
     * - Superclasses
     * - Interfaces (including those from compiled dependencies)
     * - Superinterfaces
     */
    private void scanHierarchyForAllAnnotationsRecursive(TypeElement element,
                                                         Map<TypeElement, Set<ExecutableElement>> annotatedPropertiesByType,
                                                         Map<TypeElement, Set<ExecutableElement>> annotatedFacetsByType,
                                                         Map<TypeElement, List<BehaviorEventInfo>> annotatedBehaviorEventsByType,
                                                         Set<TypeElement> visited) {
        if (!visited.add(element)) {
            return;
        }

        // Scan all methods in the current element for @Property and @Facet
        for (Element enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }

            ExecutableElement method = (ExecutableElement) enclosed;
            String methodName = method.getSimpleName().toString();

            // Check for @Property
            Property propertyAnnotation = method.getAnnotation(Property.class);
            if (propertyAnnotation != null) {
                if (isGetterName(methodName)) {
                    annotatedPropertiesByType.computeIfAbsent(element, k -> new LinkedHashSet<>()).add(method);
                }
                else {
                    messager.printMessage(Diagnostic.Kind.WARNING,
                            "@Property found on non-getter method " + methodName + " in " + element.getQualifiedName());
                }
            }

            // Check for @Facet
            Facet facetAnnotation = method.getAnnotation(Facet.class);
            if (facetAnnotation != null) {
                if (isGetterName(methodName)) {
                    annotatedFacetsByType.computeIfAbsent(element, k -> new LinkedHashSet<>()).add(method);
                }
                else {
                    messager.printMessage(Diagnostic.Kind.WARNING,
                            "@Facet found on non-getter method " + methodName + " in " + element.getQualifiedName());
                }
            }
        }

        // Check for behavior events on the type
        List<BehaviorEventInfo> events = extractBehaviorEvents(element);
        if (!events.isEmpty()) {
            annotatedBehaviorEventsByType.put(element, events);
        }

        // Recursively scan superclass
        TypeMirror superclass = element.getSuperclass();
        if (superclass != null) {
            Element superElement = processingEnv.getTypeUtils().asElement(superclass);
            if (superElement instanceof TypeElement && !superElement.toString().equals("java.lang.Object")) {
                scanHierarchyForAllAnnotationsRecursive((TypeElement) superElement,
                        annotatedPropertiesByType, annotatedFacetsByType,
                        annotatedBehaviorEventsByType, visited);
            }
        }

        // Recursively scan interfaces - THIS IS THE KEY for cross-module compilation!
        // Element.getAnnotation() works even for compiled classes from dependencies
        for (TypeMirror interfaceType : element.getInterfaces()) {
            Element interfaceElement = processingEnv.getTypeUtils().asElement(interfaceType);
            if (interfaceElement instanceof TypeElement) {
                scanHierarchyForAllAnnotationsRecursive((TypeElement) interfaceElement,
                        annotatedPropertiesByType, annotatedFacetsByType,
                        annotatedBehaviorEventsByType, visited);
            }
        }
    }

    /**
     * Collects methods annotated with the specified annotation type.
     */
    private void collectAnnotatedMethods(RoundEnvironment roundEnv,
                                         Class<? extends Annotation> annotationType,
                                         Map<TypeElement, Set<ExecutableElement>> methodsByType,
                                         Set<TypeElement> componentsToGenerate) {

        for (Element e : roundEnv.getElementsAnnotatedWith(annotationType)) {
            if (e.getKind() != ElementKind.METHOD) {
                continue;
            }

            ExecutableElement method = (ExecutableElement) e;
            String name = method.getSimpleName().toString();

            if (!isGetterName(name)) {
                messager.printMessage(Diagnostic.Kind.WARNING,
                        "@" + annotationType.getSimpleName() + " found on non-getter method " + name +
                                " in " + method.getEnclosingElement());
                continue;
            }

            TypeElement owner = (TypeElement) method.getEnclosingElement();
            methodsByType.computeIfAbsent(owner, k -> new LinkedHashSet<>()).add(method);

            // Add to generation set if it's an abstract class and has @FacesComponentBase/@FacesBehaviorBase
            if (owner.getKind() == ElementKind.CLASS &&
                    owner.getModifiers().contains(Modifier.ABSTRACT) &&
                    (owner.getAnnotation(FacesComponentBase.class) != null
                            || owner.getAnnotation(FacesBehaviorBase.class) != null)) {
                componentsToGenerate.add(owner);
            }
        }
    }

    /**
     * Collects behavior events annotated with {@code @FacesBehaviorEvents} or {@code @FacesBehaviorEvent}.
     */
    private void collectAnnotatedBehaviorEvents(RoundEnvironment roundEnv,
                                                Map<TypeElement, List<BehaviorEventInfo>> behaviorEventsByType,
                                                Set<TypeElement> componentsToGenerate) {
        for (Element e : roundEnv.getElementsAnnotatedWith(FacesBehaviorEvents.class)) {
            if (e.getKind() != ElementKind.CLASS && e.getKind() != ElementKind.INTERFACE) {
                continue;
            }

            TypeElement typeElement = (TypeElement) e;
            List<BehaviorEventInfo> events = extractBehaviorEvents(typeElement);

            if (!events.isEmpty()) {
                behaviorEventsByType.put(typeElement, events);

                // Add to generation set if it's an abstract Base class
                if (typeElement.getKind() == ElementKind.CLASS &&
                        typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
                    componentsToGenerate.add(typeElement);
                }
            }
        }

        for (Element e : roundEnv.getElementsAnnotatedWith(FacesBehaviorEvent.class)) {
            if (e.getKind() != ElementKind.CLASS && e.getKind() != ElementKind.INTERFACE) {
                continue;
            }

            TypeElement typeElement = (TypeElement) e;
            List<BehaviorEventInfo> events = extractBehaviorEvents(typeElement);

            if (!events.isEmpty()) {
                behaviorEventsByType.put(typeElement, events);

                // Add to generation set if it's an abstract Base class
                if (typeElement.getKind() == ElementKind.CLASS &&
                        typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
                    componentsToGenerate.add(typeElement);
                }
            }
        }
    }

    /**
     * Generates the implementation class for a component.
     */
    private void generateComponent(TypeElement classElement,
                                   Map<TypeElement, Set<ExecutableElement>> annotatedPropertiesByType,
                                   Map<TypeElement, Set<ExecutableElement>> annotatedFacetsByType,
                                   Map<TypeElement, Set<ExecutableElement>> propertyTargets,
                                   Map<TypeElement, Set<ExecutableElement>> facetTargets,
                                   Map<TypeElement, List<BehaviorEventInfo>> behaviorEventTargets) {
        Map<String, PropertyInfo> propsMap = new LinkedHashMap<>();
        Map<String, FacetInfo> facetsMap = new LinkedHashMap<>();

        // Build property infos
        Set<ExecutableElement> propertyGetters = propertyTargets.get(classElement);
        if (propertyGetters != null) {
            for (ExecutableElement getter : propertyGetters) {
                String propName = extractPropertyName(getter.getSimpleName().toString());

                if (!propsMap.containsKey(propName)) {
                    String returnType = getter.getReturnType().toString();
                    Property annotation = getter.getAnnotation(Property.class);
                    ExecutableElement setter = findSetter(classElement, propName, getter.getReturnType());

                    PropertyInfo info = new PropertyInfo(propName, returnType, getter, setter, annotation);
                    propsMap.put(propName, info);
                }
            }
        }

        boolean isBehavior = isBehaviorClass(classElement);

        Set<PropertyInfo> inheritedPropertyKeys = collectInheritedPropertyKeys(classElement);

        // Add inherited property keys that aren't already defined
        for (PropertyInfo inheritedKey : inheritedPropertyKeys) {
            if (!propsMap.containsKey(inheritedKey.getName())) {
                propsMap.put(inheritedKey.getName(), inheritedKey);
            }
        }
        if (!isBehavior && !propsMap.containsKey("id")) {
            propsMap.put("id", new PropertyInfo("id", "java.lang.String", null, null,
                    "Unique identifier of the component in a namingContainer.", "", "", false));
        }

        // Build facet infos
        Set<ExecutableElement> facetGetters = facetTargets.get(classElement);
        if (facetGetters != null) {
            for (ExecutableElement getter : facetGetters) {
                String facetName = extractFacetName(getter.getSimpleName().toString());

                if (!facetsMap.containsKey(facetName)) {
                    String returnType = getter.getReturnType().toString();
                    Facet annotation = getter.getAnnotation(Facet.class);

                    FacetInfo info = new FacetInfo(facetName, returnType, getter, annotation);
                    facetsMap.put(facetName, info);
                }
            }
        }


        List<BehaviorEventInfo> behaviorEventInfos = behaviorEventTargets.getOrDefault(classElement, new ArrayList<>());

        // Scan PrimeComponent interface for additional properties/facets
        if (!isBehavior) {
            scanPrimeComponentInterface(propsMap, facetsMap);
        }

        List<PropertyInfo> props = propsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        List<FacetInfo> facets = facetsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        try {
            generateImplementation(classElement, props, facets, behaviorEventInfos, isBehavior);
        }
        catch (IOException ioe) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Failed to generate implementation for " + classElement.getQualifiedName() +
                            ": " + ioe.getMessage());
        }
    }

    /**
     * Scans the class hierarchy and interfaces for {@code @Property}, {@code @Facet}, and behavior event annotations.
     */
    private void scanHierarchyForAnnotations(TypeElement element,
                                             Map<TypeElement, Set<ExecutableElement>> annotatedPropertiesByType,
                                             Map<TypeElement, Set<ExecutableElement>> annotatedFacetsByType,
                                             Map<TypeElement, List<BehaviorEventInfo>> annotatedBehaviorEventsByType,
                                             Set<ExecutableElement> allProperties,
                                             Set<ExecutableElement> allFacets,
                                             List<BehaviorEventInfo> allBehaviorEvents) {
        Set<TypeElement> visited = new HashSet<>();
        scanHierarchyRecursive(element, annotatedPropertiesByType, annotatedFacetsByType,
                annotatedBehaviorEventsByType, allProperties, allFacets, allBehaviorEvents, visited);
    }

    /**
     * Recursively scans hierarchy to collect annotations, preventing infinite loops.
     */
    private void scanHierarchyRecursive(TypeElement element,
                                        Map<TypeElement, Set<ExecutableElement>> annotatedPropertiesByType,
                                        Map<TypeElement, Set<ExecutableElement>> annotatedFacetsByType,
                                        Map<TypeElement, List<BehaviorEventInfo>> annotatedBehaviorEventsByType,
                                        Set<ExecutableElement> allProperties,
                                        Set<ExecutableElement> allFacets,
                                        List<BehaviorEventInfo> allBehaviorEvents,
                                        Set<TypeElement> visited) {
        if (!visited.add(element)) {
            return;
        }

        Set<ExecutableElement> props = annotatedPropertiesByType.get(element);
        if (props != null) {
            allProperties.addAll(props);
        }

        Set<ExecutableElement> facets = annotatedFacetsByType.get(element);
        if (facets != null) {
            allFacets.addAll(facets);
        }

        List<BehaviorEventInfo> behaviorEvents = annotatedBehaviorEventsByType.get(element);
        if (behaviorEvents != null) {
            // Add behavior events, avoiding duplicates by name
            for (BehaviorEventInfo event : behaviorEvents) {
                boolean exists = false;
                for (BehaviorEventInfo existing : allBehaviorEvents) {
                    if (existing.getName().equals(event.getName())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    allBehaviorEvents.add(event);
                }
            }
        }

        // Scan superclass
        TypeMirror superclass = element.getSuperclass();
        if (superclass != null) {
            Element superElement = processingEnv.getTypeUtils().asElement(superclass);
            if (superElement instanceof TypeElement &&
                    !superElement.toString().equals("java.lang.Object")) {
                scanHierarchyRecursive((TypeElement) superElement, annotatedPropertiesByType,
                        annotatedFacetsByType, annotatedBehaviorEventsByType, allProperties, allFacets, allBehaviorEvents, visited);
            }
        }

        // Scan interfaces
        for (TypeMirror interfaceType : element.getInterfaces()) {
            Element interfaceElement = processingEnv.getTypeUtils().asElement(interfaceType);
            if (interfaceElement instanceof TypeElement) {
                scanHierarchyRecursive((TypeElement) interfaceElement, annotatedPropertiesByType,
                        annotatedFacetsByType, annotatedBehaviorEventsByType, allProperties, allFacets, allBehaviorEvents, visited);
            }
        }
    }

    /**
     * Extracts behavior events from {@code @FacesBehaviorEvents} or {@code @FacesBehaviorEvent} annotations.
     */
    private List<BehaviorEventInfo> extractBehaviorEvents(TypeElement classElement) {
        List<BehaviorEventInfo> events = new ArrayList<>();

        for (AnnotationMirror am : classElement.getAnnotationMirrors()) {
            String annotationType = am.getAnnotationType().toString();

            if (annotationType.equals(FacesBehaviorEvents.class.getName())) {
                extractMultipleEvents(am, events);
            }
            else if (annotationType.equals(FacesBehaviorEvent.class.getName())) {
                BehaviorEventInfo eventInfo = extractEventInfo(am);
                if (eventInfo != null) {
                    events.add(eventInfo);
                }
            }
        }

        return events;
    }

    /**
     * Extracts multiple events from {@code @FacesBehaviorEvents} annotation.
     */
    private void extractMultipleEvents(AnnotationMirror am, List<BehaviorEventInfo> events) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals("value")) {
                @SuppressWarnings("unchecked")
                List<? extends AnnotationValue> eventAnnotations =
                        (List<? extends AnnotationValue>) entry.getValue().getValue();

                for (AnnotationValue av : eventAnnotations) {
                    AnnotationMirror eventAm = (AnnotationMirror) av.getValue();
                    BehaviorEventInfo eventInfo = extractEventInfo(eventAm);
                    if (eventInfo != null) {
                        events.add(eventInfo);
                    }
                }
            }
        }
    }

    /**
     * Extracts event information from a {@code @FacesBehaviorEvent} annotation mirror.
     */
    private BehaviorEventInfo extractEventInfo(AnnotationMirror eventAnnotation) {
        String name = null;
        String eventClass = null;
        String description = "";
        boolean implicit = false;
        boolean defaultEvent = false;

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
                eventAnnotation.getElementValues().entrySet()) {
            String key = entry.getKey().getSimpleName().toString();

            switch (key) {
                case "name":
                    name = (String) entry.getValue().getValue();
                    break;
                case "event":
                    TypeMirror typeMirror = (TypeMirror) entry.getValue().getValue();
                    eventClass = typeMirror.toString();
                    break;
                case "description":
                    description = (String) entry.getValue().getValue();
                    break;
                case "implicit":
                    implicit = (boolean) entry.getValue().getValue();
                    break;
                case "defaultEvent":
                    defaultEvent = (boolean) entry.getValue().getValue();
                    break;
            }
        }

        if (name != null && eventClass != null) {
            return new BehaviorEventInfo(name, eventClass, description, implicit, defaultEvent);
        }

        return null;
    }

    /**
     * Scans {@code PrimeComponent} interface for additional properties and facets.
     */
    private void scanPrimeComponentInterface(Map<String, PropertyInfo> propsMap,
                                             Map<String, FacetInfo> facetsMap) {
        try {
            TypeElement primeComponentElement = processingEnv.getElementUtils()
                    .getTypeElement(PrimeComponent.class.getName());

            if (primeComponentElement == null) {
                messager.printMessage(Diagnostic.Kind.WARNING,
                        "PrimeComponent interface not found on classpath");
                return;
            }

            for (Element enclosed : primeComponentElement.getEnclosedElements()) {
                if (enclosed.getKind() != ElementKind.METHOD) {
                    continue;
                }

                ExecutableElement method = (ExecutableElement) enclosed;
                String methodName = method.getSimpleName().toString();

                // Check for @Property
                Property propertyAnnotation = method.getAnnotation(Property.class);
                if (propertyAnnotation != null && isGetterName(methodName)) {
                    String propName = extractPropertyName(methodName);
                    if (!propsMap.containsKey(propName)) {
                        String returnType = method.getReturnType().toString();
                        PropertyInfo info = new PropertyInfo(propName, returnType, method, null, propertyAnnotation);
                        propsMap.put(propName, info);
                    }
                }

                // Check for @Facet
                Facet facetAnnotation = method.getAnnotation(Facet.class);
                if (facetAnnotation != null && isGetterName(methodName)) {
                    String facetName = extractFacetName(methodName);
                    if (!facetsMap.containsKey(facetName)) {
                        String returnType = method.getReturnType().toString();
                        FacetInfo info = new FacetInfo(facetName, returnType, method, facetAnnotation);
                        facetsMap.put(facetName, info);
                    }
                }
            }
        }
        catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                    "Failed to scan PrimeComponent interface: " + e.getMessage());
        }
    }

    /**
     * Checks if a class is a behavior by examining its name.
     */
    private boolean isBehaviorClass(TypeElement classElement) {
        return classElement.getSimpleName().toString().contains("Behavior");
    }

    /**
     * Extracts facet name from getter method name (e.g., "getHeaderFacet" → "header").
     */
    private String extractFacetName(String methodName) {
        String name = extractPropertyName(methodName);
        if (name.endsWith("Facet")) {
            return name.substring(0, name.length() - 5);
        }
        return name;
    }

    /**
     * Checks if method name follows getter naming convention.
     */
    private boolean isGetterName(String name) {
        return name.startsWith("get") || name.startsWith("is");
    }

    /**
     * Checks if sub is a subtype of sup.
     */
    private boolean isSubtype(TypeElement sub, TypeElement sup) {
        return !sub.equals(sup) && processingEnv.getTypeUtils().isSubtype(sub.asType(), sup.asType());
    }

    /**
     * Finds an abstract setter method in the class hierarchy.
     * Returns null if a concrete setter exists (no generation needed).
     */
    private ExecutableElement findSetter(TypeElement classElement, String propertyName,
                                         TypeMirror getterType) {
        String expectedSetterName = "set" + capitalize(propertyName);

        TypeElement current = classElement;
        while (current != null && !current.getQualifiedName().toString().equals("java.lang.Object")) {
            for (Element enclosed : current.getEnclosedElements()) {
                if (enclosed.getKind() != ElementKind.METHOD) continue;
                ExecutableElement method = (ExecutableElement) enclosed;

                if (!method.getSimpleName().toString().equals(expectedSetterName)) continue;
                if (method.getParameters().size() != 1) continue;

                String paramType = method.getParameters().get(0).asType().toString();
                if (!paramType.equals(getterType.toString())) continue;

                return method;
            }

            TypeMirror sup = current.getSuperclass();
            if (sup == null || sup.toString().equals("java.lang.Object")) break;
            Element supElement = processingEnv.getTypeUtils().asElement(sup);
            if (!(supElement instanceof TypeElement)) break;
            current = (TypeElement) supElement;
        }

        return null;
    }

    /**
     * Generates the implementation class with properties, facets, and events.
     */
    private void generateImplementation(TypeElement classElement, List<PropertyInfo> props,
                                        List<FacetInfo> facets,
                                        List<BehaviorEventInfo> behaviorEventInfos,
                                        boolean isBehavior) throws IOException {
        String pkg = processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName().toString();
        String baseName = classElement.getSimpleName().toString();
        String genName = baseName + "Impl";

        boolean hasEvents = !behaviorEventInfos.isEmpty();

        JavaFileObject source = filer.createSourceFile(pkg + "." + genName, classElement);

        try (PrintWriter w = new PrintWriter(source.openWriter())) {
            writePackageAndImports(w, pkg, hasEvents);
            writeClassDeclaration(w, genName, baseName, isBehavior, hasEvents);
            writePropertyKeys(w, props);
            writeFacetKeys(w, facets, isBehavior);
            writeClientBehaviorEventKeys(w, behaviorEventInfos, hasEvents);
            writePropertyMethods(w, props);
            writeFacetMethods(w, facets);
            w.println("}");
        }

        messager.printMessage(Diagnostic.Kind.NOTE,
                "Generated " + genName + " for " + classElement.getQualifiedName() +
                        " with " + props.size() + " properties, " + facets.size() + " facets, and " +
                        behaviorEventInfos.size() + " events.");
    }

    /**
     * Writes package declaration and imports.
     */
    private void writePackageAndImports(PrintWriter w, String pkg, boolean hasEvents) {
        w.println("package " + pkg + ";");
        w.println();
        w.println("import jakarta.faces.component.StateHelper;");
        w.println("import jakarta.faces.component.UIComponent;");
        w.println("import javax.annotation.processing.Generated;");
        w.println("import jakarta.faces.event.BehaviorEvent;");
        w.println("import java.util.Arrays;");
        w.println("import java.util.Collection;");
        w.println("import java.util.stream.Collectors;");
        if (hasEvents) {
            w.println("import org.primefaces.cdk.api.component.PrimeClientBehaviorHolder;");
            w.println("import java.util.Map;");
        }
        w.println();
    }

    /**
     * Writes class declaration with interfaces.
     */
    private void writeClassDeclaration(PrintWriter w, String genName, String baseName,
                                       boolean isBehavior, boolean hasEvents) {
        w.println("/**");
        w.println(" * Generated implementation of " + baseName + ".");
        w.println(" * Generated by PrimeFaces CDK.");
        w.println(" */");
        w.println("@Generated(value = \"" + AnnotationProcessor.class.getName() +
                "\", date = \"" + new Date() + "\")");
        w.print("public abstract class " + genName + " extends " + baseName);
        if (!isBehavior || hasEvents) {
            w.print(" implements ");
            if (!isBehavior) {
                w.print(PrimeComponent.class.getName());
            }
            if (hasEvents) {
                if (!isBehavior) {
                    w.print(", ");
                }
                w.print(PrimeClientBehaviorHolder.class.getName());
            }
        }
        w.println(" {");
        w.println();
    }

    /**
     * Collects PropertyKeys from parent classes using reflection and Element API.
     */
    private Set<PropertyInfo> collectInheritedPropertyKeys(TypeElement classElement) {
        Set<PropertyInfo> propertyInfos = new LinkedHashSet<>();

        TypeElement current = classElement.getSuperclass() != null ?
                (TypeElement) processingEnv.getTypeUtils().asElement(classElement.getSuperclass()) : null;

        while (current != null &&
                !current.getQualifiedName().toString().equals("java.lang.Object")) {

            String className = current.getQualifiedName().toString();
            boolean foundViaReflection = false;

            // Try reflection first (works for compiled classes with package-private PropertyKeys)
            try {
                Class<?> clazz = Class.forName(className, false,
                        this.getClass().getClassLoader());
                collectPropertyKeysViaReflection(clazz, current, propertyInfos);
                foundViaReflection = true;
            }
            catch (ClassNotFoundException e) {
                // Expected for classes being compiled in this round
            }
            catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.WARNING,
                        "Reflection failed for " + className + ": " + e.getMessage());
            }

            // Fallback to Element API if reflection didn't work
            if (!foundViaReflection) {
                collectPropertyKeysFromElement(current, propertyInfos);
            }

            // Move to superclass
            TypeMirror superclass = current.getSuperclass();
            if (superclass == null) break;
            Element superElement = processingEnv.getTypeUtils().asElement(superclass);
            current = (superElement instanceof TypeElement) ?
                    (TypeElement) superElement : null;
        }

        return propertyInfos;
    }

    /**
     * Collects PropertyKeys using reflection (works for package-private enums).
     */
    private void collectPropertyKeysViaReflection(Class<?> clazz, TypeElement typeElement,
                                                  Set<PropertyInfo> propertyInfos) {
        try {
            // getDeclaredClasses() returns ALL nested classes including package-private!
            for (Class<?> innerClass : clazz.getDeclaredClasses()) {
                if (innerClass.isEnum() &&
                        innerClass.getSimpleName().equals("PropertyKeys")) {

                    // Get enum constants
                    Object[] enumConstants = innerClass.getEnumConstants();
                    if (enumConstants != null) {
                        messager.printMessage(Diagnostic.Kind.NOTE,
                                "Found " + enumConstants.length + " PropertyKeys in " +
                                        clazz.getName() + " via reflection");

                        for (Object constant : enumConstants) {
                            String keyName = ((Enum<?>) constant).name();

                            if (CdkUtils.shouldIgnoreProperty(clazz, keyName)) {
                                continue;
                            }

                            // Try to find getter method for this property
                            String returnType = findPropertyReturnType(clazz, typeElement, keyName);

                            // Create PropertyInfo with null getter/setter (inherited, no implementation needed)
                            String description = getDefaultPropertyDescription(keyName);
                            String defaultValue = getDefaultPropertyValue(keyName);

                            PropertyInfo info = new PropertyInfo(keyName, returnType, null, null, description, defaultValue, "", false);
                            propertyInfos.add(info);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                    "Failed to scan " + clazz.getName() + " via reflection: " + e.getMessage());
        }
    }

    /**
     * Collects PropertyKeys using Element API (works for classes being compiled).
     */
    private void collectPropertyKeysFromElement(TypeElement element, Set<PropertyInfo> propertyInfos) {
        // Scan using Element API (only works for public/protected and same-package)
        for (Element enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.ENUM &&
                    enclosed.getSimpleName().toString().equals("PropertyKeys")) {

                TypeElement enumElement = (TypeElement) enclosed;
                for (Element enumConstant : enumElement.getEnclosedElements()) {
                    if (enumConstant.getKind() == ElementKind.ENUM_CONSTANT) {
                        String keyName = enumConstant.getSimpleName().toString();

                        if (CdkUtils.shouldIgnoreProperty(element.getQualifiedName().toString(), keyName)) {
                            continue;
                        }

                        // Try to find getter method for this property
                        String returnType = findPropertyReturnTypeFromElement(element, keyName);

                        // Create PropertyInfo with null getter/setter (inherited, no implementation needed)
                        PropertyInfo info = new PropertyInfo(keyName, returnType, null, null, "", "", "", false);
                        propertyInfos.add(info);
                    }
                }

                messager.printMessage(Diagnostic.Kind.NOTE,
                        "Found PropertyKeys in " + element.getQualifiedName() + " via Element API");
            }
        }
    }

    /**
     * Finds the return type of a property getter using reflection.
     */
    private String findPropertyReturnType(Class<?> clazz, TypeElement typeElement, String propertyName) {
        // Try common getter naming patterns
        String[] getterNames = {"get" + capitalize(propertyName), "is" + capitalize(propertyName)};

        // Try reflection first
        for (String getterName : getterNames) {
            try {
                java.lang.reflect.Method method = clazz.getDeclaredMethod(getterName);
                return method.getReturnType().getName();
            }
            catch (NoSuchMethodException e) {
                // Try next pattern
            }
        }

        // Fallback to Element API
        if (typeElement != null) {
            return findPropertyReturnTypeFromElement(typeElement, propertyName);
        }

        // Default to Object if we can't determine
        return "java.lang.Object";
    }

    /**
     * Finds the return type of a property getter using Element API.
     */
    private String findPropertyReturnTypeFromElement(TypeElement element, String propertyName) {
        String[] getterNames = {"get" + capitalize(propertyName), "is" + capitalize(propertyName)};

        for (Element enclosed : element.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) enclosed;
                String methodName = method.getSimpleName().toString();

                for (String getterName : getterNames) {
                    if (methodName.equals(getterName) && method.getParameters().isEmpty()) {
                        return method.getReturnType().toString();
                    }
                }
            }
        }

        // Default to Object if we can't find the getter
        return "java.lang.Object";
    }

    /**
     * Writes PropertyKeys enum and getPropertyKeys method.
     */
    private void writePropertyKeys(PrintWriter w, List<PropertyInfo> props) {
        w.println("    public enum PropertyKeys implements " + PrimePropertyKeys.class.getName() + " {");
        for (int i = 0; i < props.size(); i++) {
            PropertyInfo prop = props.get(i);
            if (prop.isHide()) {
                continue;
            }

            String type = prop.getType() + ".class";
            type = type.replaceAll("<[^>]+>", "");
            String description = prop.getDescription().replace("\"", "\\\"");
            String defaultValue = prop.getDefaultValue().replace("\"", "\\\"");
            if (defaultValue.isEmpty()) {
                defaultValue = getDefaultValueForPrimitive(prop.getType());
            }
            if (defaultValue == null) {
                defaultValue = "";
            }
            String implicitDefaultValue = prop.getImplicitDefaultValue().replace("\"", "\\\"");
            boolean required = prop.isRequired();
            w.print("        " + escapeKeyword(prop.getName()) + "(\"" + prop.getName() + "\", " + type + ", \"" + description + "\", "
                    + required + ", \"" + defaultValue + "\", \"" + implicitDefaultValue + "\")");
            w.println(i < props.size() - 1 ? "," : "");
        }
        w.println(";");
        w.println();
        w.println("        private final String _name;");
        w.println("        private final Class<?> _type;");
        w.println("        private final String _description;");
        w.println("        private final boolean _required;");
        w.println("        private final String _defaultValue;");
        w.println("        private final String _implicitDefaultValue;");
        w.println();
        w.println("        PropertyKeys(String name, Class<?> type, String description, boolean required, String defaultValue, String implicitDefaultValue) {");
        w.println("            this._name = name;");
        w.println("            this._type = type;");
        w.println("            this._description = description;");
        w.println("            this._required = required;");
        w.println("            this._defaultValue = defaultValue;");
        w.println("            this._implicitDefaultValue = implicitDefaultValue;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public String getName() {");
        w.println("            return _name;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public Class<?> getType() {");
        w.println("            return _type;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public String getDescription() {");
        w.println("            return _description;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public boolean isRequired() {");
        w.println("            return _required;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public String getDefaultValue() {");
        w.println("            return _defaultValue;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public String getImplicitDefaultValue() {");
        w.println("            return _implicitDefaultValue;");
        w.println("        }");
        w.println("    }");
        w.println();

        w.println("    @Override");
        w.println("    public " + PrimePropertyKeys.class.getName() + "[] getPropertyKeys() {");
        w.println("        return PropertyKeys.values();");
        w.println("    }");
        w.println();
    }

    /**
     * Writes FacetKeys enum and getFacetKeys method.
     */
    private void writeFacetKeys(PrintWriter w, List<FacetInfo> facets, boolean isBehavior) {
        if (!isBehavior) {
            w.println("    public enum FacetKeys implements " + PrimeFacetKeys.class.getName() + " {");
            for (int i = 0; i < facets.size(); i++) {
                FacetInfo facet = facets.get(i);
                String description = facet.getAnnotation().description().replace("\"", "\\\"");
                w.print("        " + escapeKeyword(facet.getName()) + "(\"" + facet.getName() + "\", \"" + description + "\")");
                w.println(i < facets.size() - 1 ? "," : "");
            }
            w.println(";");
            w.println();
            w.println("        private final String _name;");
            w.println("        private final String _description;");
            w.println();
            w.println("        FacetKeys(String name, String description) {");
            w.println("            this._name = name;");
            w.println("            this._description = description;");
            w.println("        }");
            w.println();
            w.println("        @Override");
            w.println("        public String getName() {");
            w.println("            return _name;");
            w.println("        }");
            w.println();
            w.println("        @Override");
            w.println("        public String getDescription() {");
            w.println("            return _description;");
            w.println("        }");
            w.println();
            w.println("    }");
            w.println();

            w.println("    @Override");
            w.println("    public " + PrimeFacetKeys.class.getName() + "[] getFacetKeys() {");
            w.println("        return FacetKeys.values();");
            w.println("    }");
            w.println();
        }
    }

    /**
     * Writes BehaviorEventKeys enum and ClientBehaviorHolder methods.
     */
    private void writeClientBehaviorEventKeys(PrintWriter w, List<BehaviorEventInfo> events, boolean hasEvents) {
        if (!hasEvents) return;

        BehaviorEventInfo defaultEvent = null;

        // BehaviorEventKeys enum
        w.println("    public enum ClientBehaviorEventKeys implements " + PrimeClientBehaviorEventKeys.class.getName() + " {");
        for (int i = 0; i < events.size(); i++) {
            BehaviorEventInfo event = events.get(i);
            String description = event.getDescription().replace("\"", "\\\"");
            w.print("        " + escapeKeyword(event.getName()) + "(\"" + event.getName() + "\", " +
                    event.getEventClass() + ".class, \"" + description + "\", " + event.isImplicit() + ", " + event.isDefaultEvent() + ")");
            w.println(i < events.size() - 1 ? "," : ";");
            if (event.isDefaultEvent() && defaultEvent == null) {
                defaultEvent = event;
            }
        }
        w.println();
        w.println("        private final String _name;");
        w.println("        private final Class<? extends BehaviorEvent> _type;");
        w.println("        private final String _description;");
        w.println("        private final boolean _implicit;");
        w.println("        private final boolean _defaultEvent;");
        w.println();
        w.println("        ClientBehaviorEventKeys(String name, Class<? extends BehaviorEvent> type, String description, boolean implicit, ");
        w.println("            boolean defaultEvent) {");
        w.println("            this._name = name;");
        w.println("            this._type = type;");
        w.println("            this._description = description;");
        w.println("            this._implicit = implicit;");
        w.println("            this._defaultEvent = defaultEvent;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public String getName() {");
        w.println("            return _name;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public Class<? extends BehaviorEvent> getType() {");
        w.println("            return _type;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public String getDescription() {");
        w.println("            return _description;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public boolean isImplicit() {");
        w.println("            return _implicit;");
        w.println("        }");
        w.println();
        w.println("        @Override");
        w.println("        public boolean isDefaultEvent() {");
        w.println("            return _defaultEvent;");
        w.println("        }");
        w.println("    }");
        w.println();

        // Static fields and initializer
        w.println("    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING;");
        w.println("    private static final Collection<String> IMPLICIT_BEHAVIOR_EVENT_NAMES;");
        w.println("    private static final Collection<String> EVENT_NAMES;");
        w.println();
        w.println("    static {");
        w.println("        Map<String, Class<? extends BehaviorEvent>> map = new java.util.HashMap<>();");
        w.println("        for (ClientBehaviorEventKeys key : ClientBehaviorEventKeys.values()) {");
        w.println("            map.put(key.getName(), key.getType());");
        w.println("        }");
        w.println("        BEHAVIOR_EVENT_MAPPING = java.util.Collections.unmodifiableMap(map);");
        w.println("        IMPLICIT_BEHAVIOR_EVENT_NAMES = Arrays.stream(ClientBehaviorEventKeys.values())");
        w.println("                .filter(ClientBehaviorEventKeys::isImplicit)");
        w.println("                .map(ClientBehaviorEventKeys::getName)");
        w.println("                .collect(Collectors.toUnmodifiableList());");
        w.println("        EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();");
        w.println("    }");
        w.println();

        // ClientBehaviorHolder methods
        w.println("    @Override");
        w.println("    public " + PrimeClientBehaviorEventKeys.class.getName() + "[] getClientBehaviorEventKeys() {");
        w.println("        return ClientBehaviorEventKeys.values();");
        w.println("    }");
        w.println();

        w.println("    @Override");
        w.println("    public String getDefaultEventName() {");
        w.println("        return " + (defaultEvent != null ? "\"" + defaultEvent.getName() + "\"" : "null") + ";");
        w.println("    }");
        w.println();

        w.println("    @Override");
        w.println("    public Collection<String> getImplicitBehaviorEventNames() {");
        w.println("        return IMPLICIT_BEHAVIOR_EVENT_NAMES;");
        w.println("    }");
        w.println();

        w.println("    @Override");
        w.println("    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {");
        w.println("        return BEHAVIOR_EVENT_MAPPING;");
        w.println("    }");
        w.println();

        w.println("    @Override");
        w.println("    public Collection<String> getEventNames() {");
        w.println("        return EVENT_NAMES;");
        w.println("    }");
        w.println();
    }

    /**
     * Writes property getter and setter methods.
     */
    private void writePropertyMethods(PrintWriter w, List<PropertyInfo> props) {
        for (PropertyInfo p : props) {
            writeGetter(w, p);
            writeSetter(w, p);
        }
    }

    /**
     * Writes facet getter methods.
     */
    private void writeFacetMethods(PrintWriter w, List<FacetInfo> facets) {
        for (FacetInfo f : facets) {
            writeFacetGetter(w, f);
        }
    }

    /**
     * Writes a single facet getter method.
     */
    private void writeFacetGetter(PrintWriter w, FacetInfo f) {
        if (f.getGetterElement() == null) return;

        String methodName = f.getGetterElement().getSimpleName().toString();

        w.println("    @Override");
        w.println("    public UIComponent " + methodName + "() {");
        w.println("        return getFacet(FacetKeys." + escapeKeyword(f.getName()) + ");");
        w.println("    }");
        w.println();
    }

    /**
     * Writes a property getter method with StateHelper access or super call.
     */
    private void writeGetter(PrintWriter w, PropertyInfo p) {
        if (!p.isGenerateGetter() || p.getGetterElement() == null || p.isCallSuper() || p.isHide()) return;

        String type = p.getType();
        String methodName = p.getGetterElement().getSimpleName().toString();

        w.println("    @Override");
        w.println("    public " + type + " " + methodName + "() {");

        String defaultValue = p.getDefaultValue();
        if (defaultValue == null || defaultValue.isBlank()) {
            defaultValue = getDefaultValueForPrimitive(type);
        }

        if (defaultValue != null) {
            if (String.class.getName().equals(type)) {
                w.println("        return (" + type + ") getStateHelper().eval(PropertyKeys." +
                        escapeKeyword(p.getName()) + ", \"" + defaultValue + "\");");
            }
            else {
                w.println("        return (" + type + ") getStateHelper().eval(PropertyKeys." +
                        escapeKeyword(p.getName()) + ", " + defaultValue + ");");
            }
        }
        else {
            w.println("        return (" + type + ") getStateHelper().eval(PropertyKeys." + escapeKeyword(p.getName()) + ");");
        }
        w.println("    }");
        w.println();
    }

    /**
     * Returns default value for primitive types.
     */
    private String getDefaultValueForPrimitive(String type) {
        switch (type) {
            case "boolean":
                return "false";
            case "byte":
            case "short":
            case "int":
            case "long":
                return "0";
            case "float":
                return "0.0f";
            case "double":
                return "0.0";
            case "char":
                return "'\\u0000'";
            default:
                return null;
        }
    }

    /**
     * Writes a property setter method with StateHelper access or super call.
     */
    private void writeSetter(PrintWriter w, PropertyInfo p) {
        if (!p.isGenerateSetter() || p.isCallSuper() || p.isHide()) return;

        String setterName = "set" + capitalize(p.getName());
        String type = p.getType();

        if (p.getSetterElement() != null) {
            w.println("    @Override");
        }
        w.println("    public void " + setterName + "(" + type + " " + escapeKeyword(p.getName()) + ") {");
        w.println("        getStateHelper().put(PropertyKeys." + escapeKeyword(p.getName()) + ", " + escapeKeyword(p.getName()) + ");");
        w.println("    }");
        w.println();
    }

    /**
     * Extracts property name from getter method name (e.g., "getValue" → "value").
     */
    private String extractPropertyName(String methodName) {
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            String core = methodName.substring(3);
            return decap(core);
        }
        else if (methodName.startsWith("is")) {
            String core = methodName.substring(2);
            return decap(core);
        }
        return decap(methodName);
    }

    /**
     * Decapitalizes a string (first character to lowercase).
     */
    private String decap(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Capitalizes a string (first character to uppercase).
     */
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String escapeKeyword(String name) {
        String[] javaKeywords = {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while",
            "true", "false", "null"
        };
        return Arrays.asList(javaKeywords).contains(name) ? "_" + name : name;
    }

    private String getDefaultPropertyValue(String propertyName) {
        switch (propertyName) {
            case "rendered":
                return "true";
        }

        return "";
    }

    private String getDefaultPropertyDescription(String propertyName) {
        switch (propertyName) {
            // PF / Faces
            case "id":
                return "Defines a string value that labels an interactive element.";
            case "rendered":
                return "Boolean value to specify the rendering of the component, when set to false component will not be rendered.";
            case "binding":
                return "An EL expression referring to a server side UIComponent instance in a backing bean.";
            case "ariaLabel":
                return "The aria-label attribute is used to define a string that labels the current element for accessibility.";
            case "disableClientWindow":
                return "Disable appending the on the rendering of this element.";
            case "accesskey":
                return "Access key to transfer focus to the input element.";
            case "dir":
                return "Direction indication for text that does not inherit directionality.";
            case "converter":
                return "An EL expression or a literal text that defines a converter for the component. "
                        + " When it's an EL expression, it's resolved to a converter instance."
                        + " In case it's a static text, it must refer to a converter id.";
            case "immediate":
                return "When set true, process validations logic is executed at apply request values phase for this component.";
            case "required":
                return "Marks component as required.";
            case "validator":
                return "An EL expression or a literal text that defines a validator for the component. "
                        + " When it's an EL expression, it's resolved to a validator instance."
                        + " In case it's a static text, it must refer to a validator id.";
            case "requiredMessage":
                return "Message to display when required field validation fails.";
            case "converterMessage":
                return "Message to display when conversion fails.";
            case "validatorMessage":
                return "Message to display when validation fails.";
            case "action":
                return "A method expression or a string outcome to process when command is executed.";
            case "actionListener":
                return "An actionlistener to process when command is executed.";
            case "lang":
                return "Code describing the language used in the generated markup.";
            case "label":
                return "A localized user presentable name.";

            // Event Handlers
            case "onclick":
                return "Fires when a mouse click on the element.";
            case "ondblclick":
                return "Fires when a mouse double-click on the element.";
            case "onmousedown":
                return "Fires when a mouse button is pressed down on an element.";
            case "onmouseup":
                return "Fires when a mouse button is released over an element.";
            case "onmouseover":
                return "Fires when the mouse pointer moves onto an element.";
            case "onmouseout":
                return "Fires when the mouse pointer moves out of an element.";
            case "onmousemove":
                return "Fires when the mouse pointer is moving while it is over an element.";
            case "onkeydown":
                return "Fires when a user is pressing a key.";
            case "onkeyup":
                return "Fires when a user releases a key.";
            case "onkeypress":
                return "Fires when a user presses a key.";
            case "onfocus":
                return "Fires when an element gets focus.";
            case "onblur":
                return "Fires when an element loses focus.";
            case "onchange":
                return "Fires when the value of an element has been changed.";
            case "onsubmit":
                return "Fires when a form is submitted.";
            case "onload":
                return "Fires after the page is finished loading.";
            case "onunload":
                return "Fires once a page has unloaded.";
            case "onresize":
                return "Fires when the browser window is resized.";
            case "onscroll":
                return "Fires when an element's scrollbar is being scrolled.";

            // Common HTML Attributes
            case "class":
                return "Specifies one or more classnames for an element.";
            case "style":
                return "Specifies an inline CSS style for an element.";
            case "title":
                return "Specifies extra information about an element (displayed as a tooltip).";
            case "alt":
                return "Specifies an alternate text for an image, if the image cannot be displayed.";
            case "href":
                return "Specifies the URL of the page the link goes to.";
            case "src":
                return "Specifies the URL of the media file.";
            case "width":
                return "Specifies the width of an element.";
            case "height":
                return "Specifies the height of an element.";
            case "type":
                return "Specifies the type of element.";
            case "name":
                return "Specifies the name of an element.";
            case "value":
                return "Specifies the value of an element.";
            case "placeholder":
                return "Specifies a short hint that describes the expected value.";
            case "disabled":
                return "Specifies that an element should be disabled.";
            case "readonly":
                return "Specifies that an input field is read-only.";
            case "checked":
                return "Specifies that an input element should be pre-selected.";
            case "selected":
                return "Specifies that an option should be pre-selected.";
            case "maxlength":
                return "Specifies the maximum number of characters allowed.";
            case "min":
                return "Specifies a minimum value.";
            case "max":
                return "Specifies a maximum value.";
            case "step":
                return "Specifies the legal number intervals.";
            case "pattern":
                return "Specifies a regular expression for validation.";

            // Accessibility Attributes
            case "tabindex":
                return "Specifies the tab order of an element.";
            case "role":
                return "Defines the role of an element for accessibility.";
            case "aria-label":
                return "Defines a string value that labels an interactive element.";
            case "aria-hidden":
                return "Indicates whether the element is exposed to accessibility APIs.";
            case "aria-describedby":
                return "Identifies the element that describes the object.";
            case "aria-labelledby":
                return "Identifies the element that labels the current element.";

            // Link/Form Attributes
            case "target":
                return "Specifies where to open the linked document.";
            case "rel":
                return "Specifies the relationship between the current and linked document.";
            case "method":
                return "Specifies the HTTP method to use when sending form-data.";
            case "enctype":
                return "Specifies how the form-data should be encoded.";

            // Media Attributes
            case "autoplay":
                return "Specifies that audio/video should start playing automatically.";
            case "controls":
                return "Specifies that audio/video controls should be displayed.";
            case "loop":
                return "Specifies that audio/video will start over when finished.";
            case "muted":
                return "Specifies that audio/video output should be muted.";
            case "poster":
                return "Specifies an image to be shown while video is downloading.";

            default:
                return "";
        }
    }

}