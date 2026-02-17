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
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
import org.primefaces.cdk.api.PrimeFacetKeys;
import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.component.PrimeClientBehaviorHolder;
import org.primefaces.cdk.api.component.PrimeComponent;
import org.primefaces.cdk.impl.CdkUtils;
import org.primefaces.cdk.impl.container.BehaviorEventInfo;
import org.primefaces.cdk.impl.container.FacetInfo;
import org.primefaces.cdk.impl.container.PropertyInfo;
import org.primefaces.cdk.impl.literal.PropertyLiteral;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import jakarta.faces.component.UIComponent;

/**
 * Generates implementation classes for JSF component and behavior base classes.
 *
 * <p>Processes abstract classes annotated with {@code @FacesComponentBase} or
 * {@code @FacesBehaviorBase}. Delegates all hierarchy scanning to
 * {@link HierarchyScanner}, which handles both compiled dependency classes
 * (via reflection) and in-round source classes (via the Element API).</p>
 *
 * <p>Generated classes include:</p>
 * <ul>
 *   <li>PropertyKeys enum with StateHelper-backed getters/setters</li>
 *   <li>FacetKeys enum with facet accessors</li>
 *   <li>ClientBehaviorEventKeys enum with ClientBehaviorHolder implementation</li>
 *   <li>PrimeComponent interface implementation for non-behaviors</li>
 * </ul>
 *
 * @see HierarchyScanner
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
    private HierarchyScanner scanner;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        this.filer = env.getFiler();
        this.messager = env.getMessager();
        this.scanner = new HierarchyScanner(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        Set<TypeElement> componentsToGenerate = new HashSet<>();

        for (Element e : roundEnv.getElementsAnnotatedWith(FacesComponentBase.class)) {
            if (e.getKind() == ElementKind.CLASS && e.getModifiers().contains(Modifier.ABSTRACT)) {
                componentsToGenerate.add((TypeElement) e);
            }
        }

        for (Element e : roundEnv.getElementsAnnotatedWith(FacesBehaviorBase.class)) {
            if (e.getKind() == ElementKind.CLASS && e.getModifiers().contains(Modifier.ABSTRACT)) {
                componentsToGenerate.add((TypeElement) e);
            }
        }

        for (TypeElement classElement : componentsToGenerate) {
            generateComponent(classElement);
        }

        return true;
    }

    /**
     * Scans the full hierarchy of {@code classElement} via {@link HierarchyScanner},
     * applies generation-time concerns (id injection, PrimeComponent interface scan,
     * setter resolution, sorting), then writes the {@code *Impl} source file.
     */
    private void generateComponent(TypeElement classElement) {
        HierarchyScannerResult hierarchyScannerResult = scanner.scan(classElement);
        boolean isBehavior = isBehaviorClass(classElement);

        // --- Properties ---
        // Start from the scan result and key by name for override/injection logic.
        Map<String, PropertyInfo> propsMap = new LinkedHashMap<>();
        for (PropertyInfo p : hierarchyScannerResult.getProperties()) {
            propsMap.put(p.getName(), p);
        }

        // Inject synthetic "id" property for components (not behaviors) when absent.
        if (!isBehavior) {
            if (!propsMap.containsKey("id")) {
                Property property = new PropertyLiteral("Unique identifier of the component in a namingContainer.",
                        false,
                        "",
                        "generated", false, String.class, false);
                propsMap.put("id", new PropertyInfo("id", property));
            }
            if (!propsMap.containsKey("binding")) {
                Property property = new PropertyLiteral("An EL expression referring to a server side UIComponent instance in a backing bean.",
                        false,
                        "",
                        "generated", false, UIComponent.class, false);
                propsMap.put("binding", new PropertyInfo("binding", property));
            }
            if (!propsMap.containsKey("rendered")) {
                Property property = new PropertyLiteral("Unique identifier of the component in a namingContainer.",
                        false,
                        "",
                        "generated", false, Boolean.class, false);
                propsMap.put("rendered", new PropertyInfo("rendered", property));
            }
        }

        // Pull in properties/facets declared on the PrimeComponent interface itself.
        Map<String, FacetInfo> facetsMap = new LinkedHashMap<>();
        for (FacetInfo f : hierarchyScannerResult.getFacets()) {
            facetsMap.put(f.getName(), f);
        }
        if (!isBehavior) {
            scanPrimeComponentInterface(propsMap, facetsMap);
        }

        // Sort alphabetically — stable, predictable generated output.
        List<PropertyInfo> props = propsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        List<FacetInfo> facets = facetsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        List<BehaviorEventInfo> events = hierarchyScannerResult.getBehaviorEvents();

        try {
            generateImplementation(classElement, props, facets, events, isBehavior);
        }
        catch (IOException ioe) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Failed to generate implementation for " + classElement.getQualifiedName()
                            + ": " + ioe.getMessage());
        }
    }

    /**
     * Scans the {@link PrimeComponent} interface for {@code @Property} and {@code @Facet}
     * annotations that every component must expose, adding any that are not already present.
     */
    private void scanPrimeComponentInterface(Map<String, PropertyInfo> propsMap,
                                             Map<String, FacetInfo> facetsMap) {
        TypeElement primeComponent = processingEnv.getElementUtils()
                .getTypeElement(PrimeComponent.class.getName());

        if (primeComponent == null) {
            messager.printMessage(Diagnostic.Kind.WARNING,
                    "PrimeComponent interface not found on classpath");
            return;
        }

        for (Element enclosed : primeComponent.getEnclosedElements()) {
            if (enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }
            ExecutableElement method = (ExecutableElement) enclosed;
            String methodName = method.getSimpleName().toString();

            if (!HierarchyScanner.isGetterName(methodName)) {
                continue;
            }

            Property propertyAnnotation = method.getAnnotation(Property.class);
            if (propertyAnnotation != null) {
                String propName = HierarchyScanner.extractPropertyName(methodName);
                if (!propsMap.containsKey(propName)) {
                    propsMap.put(propName,
                            new PropertyInfo(propName, propertyAnnotation, method.getReturnType().toString()));
                }
            }

            Facet facetAnnotation = method.getAnnotation(Facet.class);
            if (facetAnnotation != null) {
                String facetName = HierarchyScanner.extractFacetName(methodName);
                if (!facetsMap.containsKey(facetName)) {
                    facetsMap.put(facetName,
                            new FacetInfo(facetName,
                                    method.getReturnType().toString(),
                                    method,
                                    facetAnnotation));
                }
            }
        }
    }

    /**
     * Returns {@code true} if {@code classElement} represents a JSF behavior
     * (detected by name convention: simple name contains "Behavior").
     */
    private boolean isBehaviorClass(TypeElement classElement) {
        return classElement.getSimpleName().toString().contains("Behavior");
    }

    private void generateImplementation(TypeElement classElement, List<PropertyInfo> props,
                                        List<FacetInfo> facets,
                                        List<BehaviorEventInfo> behaviorEventInfos,
                                        boolean isBehavior) throws IOException {
        String pkg = processingEnv.getElementUtils()
                .getPackageOf(classElement).getQualifiedName().toString();
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
                "Generated " + genName + " for " + classElement.getQualifiedName()
                        + " with " + props.size() + " properties, "
                        + facets.size() + " facets, and "
                        + behaviorEventInfos.size() + " events.");
    }

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

    private void writeClassDeclaration(PrintWriter w, String genName, String baseName,
                                       boolean isBehavior, boolean hasEvents) {
        w.println("/**");
        w.println(" * Generated implementation of " + baseName + ".");
        w.println(" * Generated by PrimeFaces CDK.");
        w.println(" */");
        w.println("@Generated(value = \"" + AnnotationProcessor.class.getName()
                + "\", date = \"" + new Date() + "\")");
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

    private void writePropertyKeys(PrintWriter w, List<PropertyInfo> props) {
        w.println("    public enum PropertyKeys implements " + PrimePropertyKeys.class.getName() + " {");
        for (int i = 0; i < props.size(); i++) {
            PropertyInfo prop = props.get(i);
            String type = prop.getTypeName() == null ? prop.getAnnotation().type().getName() : prop.getTypeName();
            String description = prop.getAnnotation().description().replace("\"", "\\\"");
            String defaultValue = prop.getAnnotation().defaultValue().replace("\"", "\\\"");
            if (defaultValue.isEmpty()) {
                defaultValue = getDefaultValueForPrimitive(type);
            }
            if (defaultValue == null) {
                defaultValue = "";
            }
            String implicitDefaultValue = prop.getAnnotation().implicitDefaultValue().replace("\"", "\\\"");
            boolean required = prop.getAnnotation().required();
            w.print("        " + escapeKeyword(prop.getName())
                    + "(\"" + prop.getName() + "\", " + type.replaceAll("<[^>]+>", "") + ".class"
                    + ", \"" + description + "\", " + required
                    + ", \"" + defaultValue + "\", \"" + implicitDefaultValue + "\", " + prop.getAnnotation().hide() + ")");
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
        w.println("        private final boolean _hidden;");
        w.println();
        w.println("        PropertyKeys(String name, Class<?> type, String description, boolean required, String defaultValue, String implicitDefaultValue,"
                + " boolean hidden) {");
        w.println("            this._name = name;");
        w.println("            this._type = type;");
        w.println("            this._description = description;");
        w.println("            this._required = required;");
        w.println("            this._defaultValue = defaultValue;");
        w.println("            this._implicitDefaultValue = implicitDefaultValue;");
        w.println("            this._hidden = hidden;");
        w.println("        }");
        w.println();
        w.println("        @Override public String getName()              { return _name; }");
        w.println("        @Override public Class<?> getType()            { return _type; }");
        w.println("        @Override public String getDescription()       { return _description; }");
        w.println("        @Override public boolean isRequired()          { return _required; }");
        w.println("        @Override public String getDefaultValue()      { return _defaultValue; }");
        w.println("        @Override public String getImplicitDefaultValue() { return _implicitDefaultValue; }");
        w.println("        @Override public boolean isHidden() { return _hidden; }");
        w.println("    }");
        w.println();
        w.println("    @Override");
        w.println("    public " + PrimePropertyKeys.class.getName() + "[] getPropertyKeys() {");
        w.println("        return PropertyKeys.values();");
        w.println("    }");
        w.println();
    }

    private void writeFacetKeys(PrintWriter w, List<FacetInfo> facets, boolean isBehavior) {
        if (isBehavior) {
            return;
        }
        w.println("    public enum FacetKeys implements " + PrimeFacetKeys.class.getName() + " {");
        for (int i = 0; i < facets.size(); i++) {
            FacetInfo facet = facets.get(i);
            String description = facet.getAnnotation().description().replace("\"", "\\\"");
            w.print("        " + escapeKeyword(facet.getName())
                    + "(\"" + facet.getName() + "\", \"" + description + "\")");
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
        w.println("        @Override public String getName()        { return _name; }");
        w.println("        @Override public String getDescription() { return _description; }");
        w.println("    }");
        w.println();
        w.println("    @Override");
        w.println("    public " + PrimeFacetKeys.class.getName() + "[] getFacetKeys() {");
        w.println("        return FacetKeys.values();");
        w.println("    }");
        w.println();
    }

    private void writeClientBehaviorEventKeys(PrintWriter w, List<BehaviorEventInfo> events,
                                              boolean hasEvents) {
        if (!hasEvents) {
            return;
        }

        BehaviorEventInfo defaultEvent = null;

        w.println("    public enum ClientBehaviorEventKeys implements "
                + PrimeClientBehaviorEventKeys.class.getName() + " {");
        for (int i = 0; i < events.size(); i++) {
            BehaviorEventInfo event = events.get(i);
            String description = event.getAnnotation().description().replace("\"", "\\\"");
            w.print("        " + escapeKeyword(event.getName())
                    + "(\"" + event.getName() + "\", "
                    + event.getEventClassName() + ".class, \""
                    + description + "\", "
                    + event.getAnnotation().implicit() + ", "
                    + event.getAnnotation().defaultEvent() + ")");
            w.println(i < events.size() - 1 ? "," : ";");
            if (event.getAnnotation().defaultEvent() && defaultEvent == null) {
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
        w.println("        ClientBehaviorEventKeys(String name, Class<? extends BehaviorEvent> type,");
        w.println("                String description, boolean implicit, boolean defaultEvent) {");
        w.println("            this._name = name;");
        w.println("            this._type = type;");
        w.println("            this._description = description;");
        w.println("            this._implicit = implicit;");
        w.println("            this._defaultEvent = defaultEvent;");
        w.println("        }");
        w.println();
        w.println("        @Override public String getName()                              { return _name; }");
        w.println("        @Override public Class<? extends BehaviorEvent> getType()     { return _type; }");
        w.println("        @Override public String getDescription()                       { return _description; }");
        w.println("        @Override public boolean isImplicit()                          { return _implicit; }");
        w.println("        @Override public boolean isDefaultEvent()                      { return _defaultEvent; }");
        w.println("    }");
        w.println();
        w.println("    private static final java.util.Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING;");
        w.println("    private static final java.util.Collection<String> IMPLICIT_BEHAVIOR_EVENT_NAMES;");
        w.println("    private static final java.util.Collection<String> EVENT_NAMES;");
        w.println();
        w.println("    static {");
        w.println("        java.util.Map<String, Class<? extends BehaviorEvent>> map = new java.util.HashMap<>();");
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
        w.println("    @Override");
        w.println("    public " + PrimeClientBehaviorEventKeys.class.getName() + "[] getClientBehaviorEventKeys() {");
        w.println("        return ClientBehaviorEventKeys.values();");
        w.println("    }");
        w.println();
        w.println("    @Override");
        w.println("    public String getDefaultEventName() {");
        w.println("        return " + (defaultEvent != null
                ? "\"" + defaultEvent.getName() + "\""
                : "null") + ";");
        w.println("    }");
        w.println();
        w.println("    @Override");
        w.println("    public java.util.Collection<String> getImplicitBehaviorEventNames() {");
        w.println("        return IMPLICIT_BEHAVIOR_EVENT_NAMES;");
        w.println("    }");
        w.println();
        w.println("    @Override");
        w.println("    public java.util.Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {");
        w.println("        return BEHAVIOR_EVENT_MAPPING;");
        w.println("    }");
        w.println();
        w.println("    @Override");
        w.println("    public java.util.Collection<String> getEventNames() {");
        w.println("        return EVENT_NAMES;");
        w.println("    }");
        w.println();
    }

    private void writePropertyMethods(PrintWriter w, List<PropertyInfo> props) {
        for (PropertyInfo p : props) {
            writeGetter(w, p);
            writeSetter(w, p);
        }
    }

    private void writeFacetMethods(PrintWriter w, List<FacetInfo> facets) {
        for (FacetInfo f : facets) {
            writeFacetGetter(w, f);
        }
    }

    private void writeFacetGetter(PrintWriter w, FacetInfo f) {
        if (f.getGetterElement() == null) {
            return;
        }
        w.println("    @Override");
        w.println("    public UIComponent " + f.getGetterElement().getSimpleName() + "() {");
        w.println("        return getFacet(FacetKeys." + escapeKeyword(f.getName()) + ");");
        w.println("    }");
        w.println();
    }

    private void writeGetter(PrintWriter w, PropertyInfo p) {
        if (p.isImplementedGetterExists() || p.getAnnotation().callSuper()) {
            return;
        }
        if ("id".equals(p.getName()) || "binding".equals(p.getName()) || "rendered".equals(p.getName())) {
            return;
        }

        String type = p.getTypeName() == null ? p.getAnnotation().type().getName() : p.getTypeName();
        String methodName = "boolean".equals(type)
                ? "is" + CdkUtils.capitalize(p.getName())
                : "get" + CdkUtils.capitalize(p.getName());
        String defaultValue = p.getAnnotation().defaultValue();
        if (defaultValue == null || defaultValue.isBlank()) {
            defaultValue = getDefaultValueForPrimitive(type);
        }

        w.println("    @Override");
        w.println("    public " + type + " " + methodName + "() {");
        if (defaultValue != null) {
            String literal = String.class.getName().equals(type)
                    ? "\"" + defaultValue + "\""
                    : defaultValue;
            w.println("        return (" + type + ") getStateHelper().eval(PropertyKeys."
                    + escapeKeyword(p.getName()) + ", " + literal + ");");
        }
        else {
            w.println("        return (" + type + ") getStateHelper().eval(PropertyKeys."
                    + escapeKeyword(p.getName()) + ");");
        }
        w.println("    }");
        w.println();
    }

    private void writeSetter(PrintWriter w, PropertyInfo p) {
        if ((p.isImplementedSetterExists() && p.isImplementedGetterExists()) || p.getAnnotation().callSuper()) {
            return;
        }
        if ("id".equals(p.getName()) || "binding".equals(p.getName()) || "rendered".equals(p.getName())) {
            return;
        }

        String setterName = "set" + CdkUtils.capitalize(p.getName());
        String type = p.getTypeName() == null ? p.getAnnotation().type().getName() : p.getTypeName();
        String param = escapeKeyword(p.getName());

        w.println("    public void " + setterName + "(" + type + " " + param + ") {");
        w.println("        getStateHelper().put(PropertyKeys." + escapeKeyword(p.getName())
                + ", " + param + ");");
        w.println("    }");
        w.println();
    }

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

    private String escapeKeyword(String name) {
        return CdkUtils.isJavaKeyword(name) ? "_" + name : name;
    }
}